package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.appliation.service.ProductDataSyncTaskSupport;
import io.nullptr.cmb.appliation.service.SubscribeProductDataSyncService;
import io.nullptr.cmb.domain.SalesPlatform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribedProductDataSyncTask {

    private final DataSyncTaskProperties dataSyncTaskProperties;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private final List<SubscribeProductDataSyncService> subscribeProductDataSyncServices;

    @Scheduled(cron = "0 */5 * * * *")
    public void execute() {
        List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts = dataSyncTaskProperties.getSubscribedProducts();

        if (CollectionUtils.isEmpty(subscribedProducts)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (productDataSyncTaskSupport.todayIsRestDayOrHoliday()) {
            return;
        }

        // 如果当前时间是在 00:00 ~ 09:00 这个时间段则不更新数据
        if (now.getHour() < 9) {
            return;
        }

        log.info("Start to execute subscribed product data sync task: {}", subscribedProducts);

        var groupBySalesPlatform = subscribedProducts.stream()
                .collect(Collectors.groupingBy(DataSyncTaskProperties.SubscribedProduct::getSalesPlatform));

        for (var entry : groupBySalesPlatform.entrySet()) {

            SalesPlatform salesPlatform = entry.getKey();

            SubscribeProductDataSyncService subscribeProductDataSyncService = subscribeProductDataSyncServices.stream()
                    .filter(it -> it.support(salesPlatform))
                    .findFirst()
                    .orElse(null);

            if (subscribeProductDataSyncService == null) {
                log.warn("unsupported sales platform: {}", entry);
                continue;
            }

            subscribeProductDataSyncService.doSync(entry.getValue());
        }
    }

}
