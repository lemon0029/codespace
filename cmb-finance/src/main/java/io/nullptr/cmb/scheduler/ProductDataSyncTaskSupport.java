package io.nullptr.cmb.scheduler;

import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.ProductHistoryNetValueQueryResult;
import io.nullptr.cmb.client.dto.response.ProductListQueryResult;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductNetValue;
import io.nullptr.cmb.domain.event.ProductCreatedEvent;
import io.nullptr.cmb.domain.event.ProductSaleOutStateChangedEvent;
import io.nullptr.cmb.domain.repository.ProductNetValueRepository;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataSyncTaskSupport {

    private final CmbMobileClient cmbMobileClient;

    private final ProductRepository productRepository;

    private final ProductNetValueRepository productNetValueRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 更新产品列表
     */
    public List<Product> updateProduct(String productTag) {
        ProductListQueryResult productListQueryResult = cmbMobileClient.queryProductList(productTag);
        List<ProductListQueryResult.ProductDetail> productDetailList = productListQueryResult.getProductDetailList();

        for (ProductListQueryResult.ProductDetail dto : productDetailList) {
            String saCode = dto.getSaCode();
            String innerCode = dto.getInnerCode();

            Product product = productRepository.findByInnerCode(innerCode)
                    .orElse(new Product());

            String saleOutState = product.getSaleOut();

            product.setProductTag(productTag);
            product.setSaCode(saCode);
            product.setShortName(dto.getShortName());
            product.setInnerCode(innerCode);
            product.setSaleCode(dto.getSaleCode());
            product.setSaleOut(dto.getSaleOut());
            product.setRiskType(dto.getRiskType());
            product.setOffNae(dto.getOffNae());
            productRepository.save(product);

            if (!Objects.equals(saleOutState, product.getSaleOut())) {
                ProductSaleOutStateChangedEvent event =
                        new ProductSaleOutStateChangedEvent(
                                product.getId(),
                                product.getSaleCode(),
                                saleOutState,
                                product.getSaleOut()
                        );
                applicationEventPublisher.publishEvent(event);
            }

            if (product.getId() == null) {
                ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(product.getSaleCode());
                applicationEventPublisher.publishEvent(productCreatedEvent);
            }
        }

        return productRepository.findAllByProductTag(productTag);
    }

    /**
     * 更新产品净值数据
     */
    public void updateProductNetValue(Product product) {
        LocalDate today = LocalDate.now();

        String saCode = product.getSaCode();
        String innerCode = product.getInnerCode();

        Map<LocalDate, ProductNetValue> currentNetValues = productNetValueRepository.findAllByInnerCode(innerCode).stream()
                .collect(Collectors.toMap(ProductNetValue::getDate, Function.identity()));

        // 已更新净值数据的日期
        LocalDate latestDate = currentNetValues.keySet().stream()
                .max(LocalDate::compareTo)
                .orElse(null);

        if (latestDate != null && !latestDate.isBefore(today)) {
            return;
        }

        String dataScope = calculateFetchDataScope(today, latestDate);

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

        // TODO 在使用自增主键时，Hibernate 没法做批量插入
        productNetValueRepository.saveAll(netValues);
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
        } else

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
