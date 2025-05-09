package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.domain.SalesPlatform;

import java.util.List;

public interface SubscribeProductDataSyncService {

    void doSync(List<String> products);

    boolean support(SalesPlatform salesPlatform);
}
