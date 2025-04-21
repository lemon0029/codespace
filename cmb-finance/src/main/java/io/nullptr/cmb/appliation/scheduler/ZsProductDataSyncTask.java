package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductZsTag;
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
public class ZsProductDataSyncTask {

    private final ZsProductDataSyncTaskSupport support;

    private final SchedulerProperties schedulerProperties;

    /**
     * 周周宝
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 30_000)
    public void updateDataForZZB() throws InterruptedException {
        execute(ProductZsTag.ZZB);
    }

    /**
     * 月月宝
     */
    @Transactional
    @Scheduled(fixedDelay = 300_000, initialDelay = 60_000)
    public void updateDataForYYB() throws InterruptedException {
        execute(ProductZsTag.YYB);
    }

    /**
     * 季季宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 300_000)
    public void updateDataForJJB() throws InterruptedException {
        execute(ProductZsTag.JJB);
    }

    /**
     * 半年宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 360_000)
    public void updateDataForBNB() throws InterruptedException {
        execute(ProductZsTag.BNB);
    }

    /**
     * 多月宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 420_000)
    public void updateDataForDYB() throws InterruptedException {
        execute(ProductZsTag.DYB);
    }

    /**
     * 定期宝
     */
    @Transactional
    @Scheduled(fixedDelay = 3600_000, initialDelay = 480_000)
    public void updateDataForDQB() throws InterruptedException {
        execute(ProductZsTag.DQB);
    }

    private void execute(ProductZsTag productZsTag) throws InterruptedException {

        if (schedulerProperties.getDataSyncEnabled() == null ||
                !schedulerProperties.getDataSyncEnabled().contains(productZsTag)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 如果当前时间是在 00:00 ~ 09:00 这个时间段则不更新数据
        if (now.getHour() < 9) {
            return;
        }

        log.info("Start to execute product data sync task, tag: {}", productZsTag);
        List<Product> products = support.updateProduct(productZsTag.getCode());

        for (Product product : products) {
            support.updateProductNetValue(product);
            TimeUnit.SECONDS.sleep(1);
        }

        log.info("Task finished: product[tag={}] data sync", products);
    }
}
