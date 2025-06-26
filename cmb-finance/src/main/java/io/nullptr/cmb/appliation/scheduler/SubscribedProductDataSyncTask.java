package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.appliation.service.EastMoneyProductDataSyncService;
import io.nullptr.cmb.appliation.service.ProductDataSyncTaskSupport;
import io.nullptr.cmb.appliation.service.SubscribeProductDataSyncService;
import io.nullptr.cmb.client.SnowballFundApiClient;
import io.nullptr.cmb.client.dto.response.IndexFundTraceDTO;
import io.nullptr.cmb.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribedProductDataSyncTask {

    private final DataSyncTaskProperties dataSyncTaskProperties;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private final List<SubscribeProductDataSyncService> subscribeProductDataSyncServices;

    private final EastMoneyProductDataSyncService eastMoneyProductDataSyncService;

    private final SnowballFundApiClient snowballFundApiClient;

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

        for (DataSyncTaskProperties.SubscribedProduct subscribedProduct : subscribedProducts) {
            String traceIndexSymbol = subscribedProduct.getTraceIndexSymbol();

            if (!StringUtils.hasText(traceIndexSymbol)) {
                continue;
            }

            IndexFundTraceDTO indexFundTraceDTO = snowballFundApiClient.getIndexTraces(traceIndexSymbol);

            IndexFundTraceDTO.FundsGroupByType fundsGroupByType = indexFundTraceDTO.getItems().getFirst();
            List<IndexFundTraceDTO.FundDTO> funds = fundsGroupByType.getFunds();

            for (IndexFundTraceDTO.FundDTO fundDTO : funds) {
                String fundCode = fundDTO.getFundCode();

                Product product = eastMoneyProductDataSyncService.updateProduct(traceIndexSymbol, fundCode);
                productDataSyncTaskSupport.updateProductNetValue(product);
            }
        }

        for (SubscribeProductDataSyncService service : subscribeProductDataSyncServices) {
            List<DataSyncTaskProperties.SubscribedProduct> filtered = subscribedProducts.stream()
                    .filter(it -> service.support(it.getSalesPlatform(), it.getProductType()))
                    .toList();

            if (CollectionUtils.isEmpty(filtered)) {
                continue;
            }

            service.doSync(filtered);
        }
    }

}
