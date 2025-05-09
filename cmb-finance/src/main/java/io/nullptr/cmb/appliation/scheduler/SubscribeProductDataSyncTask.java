package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.appliation.service.SubscribeProductDataSyncService;
import io.nullptr.cmb.domain.SalesPlatform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeProductDataSyncTask {

    private final DataSyncTaskProperties dataSyncTaskProperties;

    private final List<SubscribeProductDataSyncService> subscribeProductDataSyncServices;

    @Scheduled(cron = "0 */5 * * * *")
    public void execute() {
        List<DataSyncTaskProperties.SubscribeProduct> subscribeProducts = dataSyncTaskProperties.getSubscribeProducts();

        if (CollectionUtils.isEmpty(subscribeProducts)) {
            return;
        }

        var groupBySalesPlatform = subscribeProducts.stream()
                .collect(Collectors.groupingBy(DataSyncTaskProperties.SubscribeProduct::getSalesPlatform));

        for (var entry : groupBySalesPlatform.entrySet()) {

            SalesPlatform salesPlatform = entry.getKey();
            List<String> products = entry.getValue().stream()
                    .map(DataSyncTaskProperties.SubscribeProduct::getProductCode)
                    .toList();

            SubscribeProductDataSyncService subscribeProductDataSyncService = subscribeProductDataSyncServices.stream()
                    .filter(it -> it.support(salesPlatform))
                    .findFirst()
                    .orElse(null);

            if (subscribeProductDataSyncService == null) {
                log.warn("unsupported sales platform: {}", entry);
                continue;
            }

            subscribeProductDataSyncService.doSync(products);
        }
    }

}
