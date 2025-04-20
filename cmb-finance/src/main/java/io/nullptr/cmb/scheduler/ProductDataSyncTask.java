package io.nullptr.cmb.scheduler;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.infrastructure.common.Constants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDataSyncTask {

    private final ProductDataSyncTaskSupport support;

    /**
     * 周周宝
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 30_000)
    public void updateDataForZZB() throws InterruptedException {
        execute(Constants.ZZB_PRODUCT_TAG);
    }

    /**
     * 月月宝
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 60_000)
    public void updateDataForYYB() throws InterruptedException {
        execute(Constants.YYB_PRODUCT_TAG);
    }

    /**
     * 季季宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 300_000)
    public void updateDataForJJB() throws InterruptedException {
        execute(Constants.JJB_PRODUCT_TAG);
    }

    /**
     * 半年宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 360_000)
    public void updateDataForBNB() throws InterruptedException {
        execute(Constants.BNB_PRODUCT_TAG);
    }

    /**
     * 多月宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 420_000)
    public void updateDataForDYB() throws InterruptedException {
        execute(Constants.DYB_PRODUCT_TAG);
    }

    /**
     * 定期宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 480_000)
    public void updateDataForDQB() throws InterruptedException {
        execute(Constants.DQB_PRODUCT_TAG);
    }

    private void execute(String productTag) throws InterruptedException {

        LocalDateTime now = LocalDateTime.now();

        // 如果当前时间是在 00:00 ~ 09:00 这个时间段则不更新数据
        if (now.getHour() < 9) {
            return;
        }

        log.info("Start to execute product data sync task, tag: {}", productTag);
        List<Product> products = support.updateProduct(productTag);

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: product[tag={}] data sync", products);
    }
}
