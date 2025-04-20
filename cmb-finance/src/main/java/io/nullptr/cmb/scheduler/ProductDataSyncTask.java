package io.nullptr.cmb.scheduler;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.infrastructure.common.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void updateDataForZZB() throws InterruptedException {
        log.info("Start to execute zzb product data sync task");
        List<Product> products = support.updateProduct(Constants.ZZB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: zzb product data sync");
    }

    /**
     * 定时获取月月宝产品数据（每五分钟执行一次）
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 60_000)
    public void updateDataForYYB() throws InterruptedException {
        log.info("Start to execute yyb product data sync task");
        List<Product> products = support.updateProduct(Constants.YYB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: yyb product data sync");
    }

    /**
     * 季季宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 300_000)
    public void updateDataForJJB() throws InterruptedException {
        log.info("Start to execute jjb product data sync task");
        List<Product> products = support.updateProduct(Constants.JJB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: jjb product data sync");
    }

    /**
     * 半年宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 360_000)
    public void updateDataForBNB() throws InterruptedException {
        log.info("Start to execute bnb product data sync task");
        List<Product> products = support.updateProduct(Constants.BNB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: bnb product data sync");
    }

    /**
     * 多月宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 420_000)
    public void updateDataForDYB() throws InterruptedException {
        log.info("Start to execute dyb product data sync task");
        List<Product> products = support.updateProduct(Constants.DYB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: dyb product data sync");
    }

    /**
     * 定期宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 480_000)
    public void updateDataForDQB() throws InterruptedException {
        log.info("Start to execute dqb product data sync task");
        List<Product> products = support.updateProduct(Constants.DQB_PRODUCT_TAG);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: dqb product data sync");
    }
}
