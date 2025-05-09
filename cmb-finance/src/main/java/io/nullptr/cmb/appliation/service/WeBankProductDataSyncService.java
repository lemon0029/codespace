package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.appliation.scheduler.DataSyncTaskProperties;
import io.nullptr.cmb.client.WeBankApiClient;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.SalesPlatform;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WeBankProductDataSyncService implements SubscribeProductDataSyncService {

    private final WeBankApiClient weBankApiClient;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private final ProductRepository productRepository;

    @Override
    public void doSync(List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts) {
        List<Product> products = updateProduct(subscribedProducts);

        for (Product product : products) {
            productDataSyncTaskSupport.updateProductNetValue(product);
        }
    }

    private List<Product> updateProduct(List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts) {
        List<String> productCodes = subscribedProducts.stream()
                .map(DataSyncTaskProperties.SubscribedProduct::getProductSaleCode)
                .toList();

        List<WeBankWealthProductListDTO> weBankWealthProductListDTOS = weBankApiClient.queryProductByCode(productCodes);

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
            product.setSubscribed(true);

            productRepository.save(product);
            result.add(product);
        }

        for (DataSyncTaskProperties.SubscribedProduct subscribedProduct : subscribedProducts) {
            String productCode = subscribedProduct.getProductSaleCode();
            String productName = subscribedProduct.getProductName();

            boolean found = weBankWealthProductListDTOS.stream()
                    .anyMatch(it -> Objects.equals(it.getProdCode(), productCode));

            if (found) {
                continue;
            }

            Product product = productRepository.findByInnerCode(productCode)
                    .orElse(new Product());

            product.setInnerCode(productCode);
            product.setSaCode(productCode);
            product.setRiskLevel("N/A");
            product.setProductTag("N/A");
            product.setRiskType("N/A");
            product.setHotProduct(false);
            product.setSellOut("UNKNOWN");
            product.setSalesPlatform(SalesPlatform.WE_BANK);
            product.setOffNae("N/A");
            product.setShortName(productName);
            product.setSubscribed(true);

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
