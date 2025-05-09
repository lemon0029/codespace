package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.domain.SalesPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CMBProductDataSyncService implements SubscribeProductDataSyncService {

    @Override
    public void doSync(List<String> products) {

    }

    @Override
    public boolean support(SalesPlatform salesPlatform) {
        return salesPlatform == SalesPlatform.CMB;
    }
}
