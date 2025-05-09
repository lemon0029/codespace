package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.domain.ProductRiskType;
import io.nullptr.cmb.domain.ProductZsTag;
import io.nullptr.cmb.domain.SalesPlatform;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "data-sync-task")
public class DataSyncTaskProperties {

    /**
     * 是否开启周周宝系列产品数据同步任务
     */
    private List<ProductZsTag> zsTags;

    /**
     * 是否按风险类型全量同步产品数据
     */
    private List<ProductRiskType> riskTypes;

    /**
     * 是否开启招商银行热门理财产品数据同步任务
     */
    private boolean hotProductListDataSyncEnabled;

    /**
     * 订阅产品
     */
    private List<SubscribeProduct> subscribeProducts;

    @Data
    public static class SubscribeProduct {
        private SalesPlatform salesPlatform;
        private String productCode;
    }
}
