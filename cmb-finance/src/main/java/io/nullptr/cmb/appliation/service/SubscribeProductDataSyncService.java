package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.appliation.scheduler.DataSyncTaskProperties;
import io.nullptr.cmb.domain.ProductType;
import io.nullptr.cmb.domain.SalesPlatform;

import java.util.List;

public interface SubscribeProductDataSyncService {

    void doSync(List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts);

    boolean support(SalesPlatform salesPlatform, ProductType productType);
}
