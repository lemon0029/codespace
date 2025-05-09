package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.client.WeBankApiClient;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.SalesPlatform;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WeBankProductDataSyncService implements SubscribeProductDataSyncService {

    private final WeBankApiClient weBankApiClient;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private final ProductRepository productRepository;

    @Override
    public void doSync(List<String> products) {
        List<Product> updated = updateProduct(products);

        for (Product product : updated) {
            productDataSyncTaskSupport.updateProductNetValue(product);
        }
    }

    private List<Product> updateProduct(List<String> products) {
        List<WeBankWealthProductListDTO> weBankWealthProductListDTOS = weBankApiClient.queryProductByCode(products);

        List<Product> result = new ArrayList<>();

        for (WeBankWealthProductListDTO weBankWealthProductListDTO : weBankWealthProductListDTOS) {
            String prodCode = weBankWealthProductListDTO.getProdCode();
            Product product = productRepository.findByInnerCode(prodCode)
                    .orElse(new Product());

            product.setInnerCode(prodCode);
            product.setSaCode(prodCode);
            product.setRiskLevel(weBankWealthProductListDTO.getRiskLevel());
            product.setProductTag("N/A");
            product.setRiskType("N/A");
            product.setHotProduct(false);
            product.setSellOut("UNKNOWN");
            product.setSalesPlatform(SalesPlatform.WE_BANK);
            product.setOffNae(weBankWealthProductListDTO.getTaName());
            product.setShortName(weBankWealthProductListDTO.getProdShortName());

            productRepository.save(product);
            result.add(product);
        }

        return result;
    }

    @Override
    public boolean support(SalesPlatform salesPlatform) {
        return salesPlatform == SalesPlatform.WE_BANK;
    }
}
