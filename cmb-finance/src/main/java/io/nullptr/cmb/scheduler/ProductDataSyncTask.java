package io.nullptr.cmb.scheduler;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.infrastructure.common.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataSyncTask {

    private final ProductDataSyncTaskSupport support;

    /**
     * 定时获取周周宝产品数据（每五分钟执行一次）
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 30_000)
    public void updateDataForZZB() {
        log.info("Start to execute zzb product data sync task");
        List<Product> products = support.updateProduct(Constants.ZZB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
        }

        log.info("Task finished: zzb product data sync");
    }

    /**
     * 定时获取月月宝产品数据（每五分钟执行一次）
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 60_000)
    public void updateDataForYYB() {
        log.info("Start to execute yyb product data sync task");
        List<Product> products = support.updateProduct(Constants.YYB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
        }

        log.info("Task finished: yyb product data sync");
    }
}
