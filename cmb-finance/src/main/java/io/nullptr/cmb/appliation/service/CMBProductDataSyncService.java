package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.appliation.scheduler.DataSyncTaskProperties;
import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.ProductInfoDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.SalesPlatform;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CMBProductDataSyncService implements SubscribeProductDataSyncService {

    private final CmbMobileClient cmbMobileClient;

    private final ProductRepository productRepository;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private static final String DEFAULT_SAA_CODE = "D07";

    @Override
    public void doSync(List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts) {
        for (DataSyncTaskProperties.SubscribedProduct subscribedProduct : subscribedProducts) {

            Product product = updateProduct(subscribedProduct);

            if (product == null) {
                continue;
            }

            productDataSyncTaskSupport.updateProductNetValue(product);
        }
    }

    private Product updateProduct(DataSyncTaskProperties.SubscribedProduct subscribedProduct) {
        String productSaleCode = subscribedProduct.getProductSaleCode();
        ProductInfoDTO productInfoDTO = cmbMobileClient.queryProductInfo(DEFAULT_SAA_CODE, productSaleCode);

        if (productInfoDTO == null) {
            return null;
        }

        Product product = productRepository.findByInnerCode(productSaleCode)
                .orElse(new Product());

        product.setInnerCode(productSaleCode);
        product.setSaCode(productInfoDTO.getSaaCod());
        product.setRiskLevel(productInfoDTO.getRiskLvl());
        product.setProductTag(productInfoDTO.getJjbTag());

        if (product.getId() == null) {
            product.setRiskType("N/A");
            product.setHotProduct(false);
            product.setSellOut("UNKNOWN");
        }

        product.setSalesPlatform(SalesPlatform.CMB);

        String crpNam = productInfoDTO.getCrpNam();
        if (crpNam.endsWith("有限责任公司")) {
            crpNam = crpNam.replace("有限责任公司", "");
        }

        product.setOffNae(crpNam);

        product.setShortName(productInfoDTO.getRipSnm());
        product.setSubscribed(true);

        productRepository.save(product);

        return product;
    }

    @Override
    public boolean support(SalesPlatform salesPlatform) {
        return salesPlatform == SalesPlatform.CMB;
    }
}
