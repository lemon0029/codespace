package io.nullptr.cmb.scheduler;

import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.ProductHistoryNetValueQueryResult;
import io.nullptr.cmb.client.dto.response.ProductListQueryResult;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductNetValue;
import io.nullptr.cmb.domain.repository.ProductNetValueRepository;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZZBProductDataSyncTask {

    private final CmbMobileClient cmbMobileClient;

    private final ProductRepository productRepository;

    private final ProductNetValueRepository productNetValueRepository;

    private static final Integer PRODUCT_TAG = 7;

    /**
     * 更新产品的净值数据（每五分钟执行一次）
     */
    @Scheduled(fixedDelay = 300_000, initialDelay = 20_000)
    public void execute() {
        log.info("Start to execute zzb product data sync task");
        List<Product> products = updateProduct();

        for (Product product : products) {
            updateProductNetValue(product);
        }

        log.info("Task finished: zzb product data sync");
    }

    /**
     * 更新产品列表
     */
    private List<Product> updateProduct() {
        ProductListQueryResult productListQueryResult = cmbMobileClient.queryProductList(PRODUCT_TAG);
        List<ProductListQueryResult.ProductDetail> productDetailList = productListQueryResult.getProductDetailList();

        for (ProductListQueryResult.ProductDetail dto : productDetailList) {
            String saCode = dto.getSaCode();
            String innerCode = dto.getInnerCode();

            Optional<Product> product = productRepository.findByInnerCode(innerCode);

            if (product.isEmpty()) {
                Product newProduct = new Product();
                newProduct.setProductTag(PRODUCT_TAG);
                newProduct.setSaCode(saCode);
                newProduct.setShortName(dto.getShortName());
                newProduct.setInnerCode(innerCode);
                productRepository.save(newProduct);

                log.info("Found new product: {}", newProduct);
                // TODO 发现新产品事件通知
            }

            // TODO 更新其它字段数据
            // TODO 删除不属于周周宝系列的产品？
        }

        return productRepository.findAllByProductTag(PRODUCT_TAG);
    }

    /**
     * 更新产品净值数据
     */
    private void updateProductNetValue(Product product) {
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

            productNetValueRepository.save(productNetValue);
        }
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
