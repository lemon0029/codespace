package io.nullptr.cmb.appliation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.WeBankApiClient;
import io.nullptr.cmb.client.dto.response.FundHistoryNetValueDTO;
import io.nullptr.cmb.client.dto.response.ProductHistoryNetValueQueryResult;
import io.nullptr.cmb.client.dto.response.ProductQueryByTagResult;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductYieldDTO;
import io.nullptr.cmb.domain.*;
import io.nullptr.cmb.domain.event.ProductCreatedEvent;
import io.nullptr.cmb.domain.event.ProductSellOutStateChangedEvent;
import io.nullptr.cmb.domain.repository.ProductNetValueRepository;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataSyncTaskSupport {

    private final CmbMobileClient cmbMobileClient;

    private final WeBankApiClient weBankApiClient;

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    private final ProductRepository productRepository;

    private final ProductNetValueRepository productNetValueRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * 更新产品列表
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Product> updateProduct(ProductRiskType riskType, String productTag) {

        // 周周宝系列产品的接口是 A, B, C... 这里需要处理下
        String mappingRiskType = switch (riskType) {
            case BALANCED_ADVANCE -> "C";
            case STEADY_GROWTH -> "B";
            case STEADY_LOW_VOLATILITY -> "A";
        };

        ProductQueryByTagResult queryResult = cmbMobileClient.queryProductByRiskTypeAndTag(mappingRiskType, productTag);

        if (queryResult == null || CollectionUtils.isEmpty(queryResult.getProductDetailList())) {
            return Collections.emptyList();
        }

        List<ProductQueryByTagResult.ProductDetail> productDetailList = queryResult.getProductDetailList();

        for (ProductQueryByTagResult.ProductDetail dto : productDetailList) {
            String saCode = dto.getSaCode();
            String innerCode = dto.getInnerCode();

            // A - 在售；C - 非在售
            String saleTag = dto.getSaleTag();

            if (!"A".equals(saleTag)) {
                productRepository.deleteBySaleCode(innerCode);
                productNetValueRepository.deleteAllByInnerCode(innerCode);
                continue;
            }

            Product product = productRepository.findByInnerCode(innerCode)
                    .orElse(new Product());

            boolean newProduct = product.getId() == null;
            String sellOutState = product.getSellOut();

            product.setProductTag(productTag);
            product.setSaCode(saCode);
            product.setShortName(dto.getShortName());
            product.setInnerCode(innerCode);
            product.setSaleCode(dto.getSaleCode());
            product.setSellOut(dto.getSaleOut());
            product.setRiskType(riskType.getCode());
            product.setOffNae(dto.getOffNae());
            productRepository.save(product);

            if (sellOutState != null && !Objects.equals(sellOutState, product.getSellOut())) {
                // TODO 售罄状态更新时间需要一个单独的字段
                Duration duration = Duration.between(product.getUpdatedAt(), LocalDateTime.now());
                ProductSellOutStateChangedEvent event =
                        new ProductSellOutStateChangedEvent(
                                product.getId(),
                                product.getSaleCode(),
                                sellOutState,
                                product.getSellOut(),
                                duration
                        );
                applicationEventPublisher.publishEvent(event);
            }

            if (newProduct) {
                ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(product.getSaleCode());
                applicationEventPublisher.publishEvent(productCreatedEvent);
            }
        }

        return productRepository.findAllByProductTag(productTag);
    }

    /**
     * 更新产品净值数据
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProductNetValue(Product product) {
        LocalDate today = LocalDate.now();
        LocalDate qDate = today.minusDays(1);

        String innerCode = product.getInnerCode();

        Map<LocalDate, ProductNetValue> currentNetValues = productNetValueRepository.findAllByInnerCode(innerCode).stream()
                .collect(Collectors.toMap(ProductNetValue::getDate, Function.identity()));

        // 已更新净值数据的日期
        LocalDate latestDate = currentNetValues.keySet().stream()
                .max(LocalDate::compareTo)
                .orElse(null);

        if (latestDate != null && !latestDate.isBefore(qDate)) {
            log.info("Net value data is up to date, no need to update: {}", product.getSaleCode());
            return;
        }

        if (product.getSalesPlatform() == SalesPlatform.CMB && product.getType() == ProductType.FUND) {
            updateCMBFundProductNetValue(product, currentNetValues, qDate, latestDate);
        } else if (product.getSalesPlatform() == null || product.getSalesPlatform() == SalesPlatform.CMB) {
            updateCMBProductNetValue(product, currentNetValues, qDate, latestDate);
        } else if (product.getSalesPlatform() == SalesPlatform.WE_BANK) {
            updateWeBankProductNetValue(product, currentNetValues, qDate, latestDate);
        }
    }

    private void updateCMBFundProductNetValue(Product product,
                                              Map<LocalDate, ProductNetValue> currentNetValues,
                                              LocalDate qDate,
                                              LocalDate latestDate) {
        String fundCode = product.getSaleCode();
        String dataScope = calculateFetchDataScope(qDate, latestDate);

        String expressCode = switch (dataScope) {
            case "A" -> "M001";
            case "B" -> "M003";
            case "C" -> "Y001";
            default -> "X000";
        };

        List<FundHistoryNetValueDTO> fundHistoryNetValueDTOS = cmbMobileClient.queryFundHistoryNetValue(fundCode, expressCode);

        log.info("Query cmb fund product history net value, [{}, {}]: {}", fundCode, expressCode, fundHistoryNetValueDTOS);

        List<ProductNetValue> netValues = new ArrayList<>();

        for (FundHistoryNetValueDTO dto : fundHistoryNetValueDTOS) {
            LocalDate date = dto.getDate();
            BigDecimal netValue = dto.getNetValue();
            BigDecimal pctChange = dto.getPctChange();

            ProductNetValue netValueEntity = currentNetValues.get(date);

            if (netValueEntity != null) {
                continue;
            }

            netValueEntity = new ProductNetValue();
            netValueEntity.setProductSaleCode(fundCode);
            netValueEntity.setInnerCode(fundCode);
            netValueEntity.setProductName(product.getShortName());
            netValueEntity.setDate(date);
            netValueEntity.setValue(netValue);
            netValueEntity.setPctChange(pctChange);

            netValues.add(netValueEntity);
        }

        saveAllProductNetValues(netValues);
    }

    private void updateWeBankProductNetValue(Product product, Map<LocalDate, ProductNetValue> currentNetValues, LocalDate qDate, LocalDate latestDate) {
        String saleCode = product.getSaleCode();
        String dataScope = calculateFetchDataScope(qDate, latestDate);
        LocalDate startDate = switch (dataScope) {
            case "A" -> qDate.minusMonths(1);
            case "B" -> qDate.minusMonths(3);
            default -> qDate.minusYears(1);
        };

        List<WeBankWealthProductYieldDTO> weBankWealthProductYieldDTOS = weBankApiClient.queryProductYield(saleCode, startDate, qDate);

        log.info("Query we-bank product history net value, [{}, {}]: {}",
                saleCode, dataScope, weBankWealthProductYieldDTOS);

        List<ProductNetValue> netValues = new ArrayList<>();
        for (WeBankWealthProductYieldDTO weBankWealthProductYieldDTO : weBankWealthProductYieldDTOS) {
            LocalDate earningsRateDate = weBankWealthProductYieldDTO.getEarningsRateDate();
            BigDecimal unitNetValue = weBankWealthProductYieldDTO.getUnitNetValue();

            ProductNetValue productNetValue = currentNetValues.get(earningsRateDate);

            if (productNetValue != null) {
                // 已经更新过的产品净值理论上来说是不会变化的
                continue;
            }

            productNetValue = new ProductNetValue();
            productNetValue.setInnerCode(saleCode);
            productNetValue.setProductSaleCode(saleCode);
            productNetValue.setProductName(product.getShortName());
            productNetValue.setDate(earningsRateDate);
            productNetValue.setValue(unitNetValue);

            netValues.add(productNetValue);
        }

        saveAllProductNetValues(netValues);
    }

    private void updateCMBProductNetValue(Product product, Map<LocalDate, ProductNetValue> currentNetValues, LocalDate qDate, LocalDate latestDate) {

        String saCode = product.getSaCode();
        String innerCode = product.getInnerCode();

        String dataScope = calculateFetchDataScope(qDate, latestDate);

        ProductHistoryNetValueQueryResult historyNetValueQueryResult =
                cmbMobileClient.queryHistoryNetValue(dataScope, saCode, innerCode);

        log.info("Query product history net value, [{}, {}, {}]: {}",
                saCode, innerCode, dataScope, historyNetValueQueryResult);

        List<ProductNetValue> netValues = new ArrayList<>();

        Map<String, String> netValueMap = historyNetValueQueryResult.getNetValueMap();
        for (Map.Entry<String, String> entry : netValueMap.entrySet()) {
            LocalDate date = LocalDate.parse(entry.getKey());

            ProductNetValue productNetValue = currentNetValues.get(date);

            if (productNetValue != null) {
                // 已经更新过的产品净值理论上来说是不会变化的
                continue;
            }

            productNetValue = new ProductNetValue();
            productNetValue.setInnerCode(innerCode);
            productNetValue.setDate(date);
            productNetValue.setValue(new BigDecimal(entry.getValue()));

            netValues.add(productNetValue);
        }

        saveAllProductNetValues(netValues);
    }

    public boolean todayIsRestDayOrHoliday() {
        LocalDate today = LocalDate.now();

        DayOfWeek dayOfWeek = today.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return true;
        }

        Set<LocalDate> holidayOfYear = loadHolidays(today.getYear());

        return holidayOfYear.contains(today);
    }

    private Set<LocalDate> loadHolidays(int year) {
        String data = fetchHolidays(year);

        if (!StringUtils.hasText(data)) {
            return Collections.emptySet();
        }

        Set<LocalDate> result = new LinkedHashSet<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            JsonNode daysNode = jsonNode.get("days");

            for (JsonNode node : daysNode) {
                String date = node.get("date").asText();
                result.add(LocalDate.parse(date));
            }

        } catch (Exception e) {
            log.warn("Failed to parse holiday data: {}", data, e);
        }

        return result;
    }

    private static synchronized String fetchHolidays(int year) {
        String userHome = System.getProperty("user.home");
        Path path = Path.of(userHome, ".cmb-finance", "holiday", "%d.json".formatted(year));

        try {
            if (Files.exists(path)) {
                return Files.readString(path);
            }

            String url = "https://fastly.jsdelivr.net/gh/NateScarlet/holiday-cn@master/%d.json".formatted(year);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                return null;
            }

            String data = responseEntity.getBody();

            if (StringUtils.hasText(data)) {
                if (!Files.exists(path)) {
                    Files.createDirectories(path.getParent());
                }

                Files.writeString(path, data);
            }

            return data;
        } catch (Exception e) {
            log.warn("Failed to fetch holiday for {}", year, e);
            return null;
        }
    }

    private void saveAllProductNetValues(List<ProductNetValue> netValues) {
        // TODO 在使用自增主键时，Hibernate 没法做批量插入
        // productNetValueRepository.saveAll(netValues);

        if (netValues.isEmpty()) {
            return;
        }

        var startTime = System.currentTimeMillis();

        String sql = "insert into t_product_net_value(inner_code, date, value, product_sale_code, product_name, created_at, updated_at) values (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, netValues, 1024, (ps, argument) -> {
            ps.setString(1, argument.getInnerCode());
            ps.setObject(2, argument.getDate());
            ps.setBigDecimal(3, argument.getValue());
            ps.setString(4, argument.getProductSaleCode());
            ps.setString(5, argument.getProductName());
            ps.setObject(6, LocalDateTime.now());
            ps.setObject(7, LocalDateTime.now());
        });

        log.info("Save {} product net values, cost: {}ms", netValues.size(), System.currentTimeMillis() - startTime);
    }

    /**
     * 计算这一次获取净值数据的日期范围
     *
     * @param current  当前日期，一般为当天
     * @param previous 已更新数据的最新日期
     */
    private String calculateFetchDataScope(LocalDate current, LocalDate previous) {

        // 如果之前从未获取过数据，则首次需要获取产品成立以来的所有数据
        if (previous == null) {
            return "D";
        }

        long days = ChronoUnit.DAYS.between(previous, current);

        // 需要获取近一个月的数据即可
        if (days < 25) {
            return "A";
        }

        // 需要获取近三个月的数据
        if (days < 80) {
            return "B";
        }

        // 需要获取近一年的数据
        if (days < 300) {
            return "C";
        }

        return "D";
    }
}
