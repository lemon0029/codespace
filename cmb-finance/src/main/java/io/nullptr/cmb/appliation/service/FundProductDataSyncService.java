package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.appliation.scheduler.DataSyncTaskProperties;
import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.FundInfoDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductType;
import io.nullptr.cmb.domain.SalesPlatform;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundProductDataSyncService implements SubscribeProductDataSyncService {

    private final CmbMobileClient cmbMobileClient;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private final ProductRepository productRepository;

    @Override
    public void doSync(List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts) {
        for (DataSyncTaskProperties.SubscribedProduct subscribedProduct : subscribedProducts) {
            Product product = updateProduct(subscribedProduct);

            productDataSyncTaskSupport.updateProductNetValue(product);
        }
    }

    private Product updateProduct(DataSyncTaskProperties.SubscribedProduct subscribedProduct) {
        String fundCode = subscribedProduct.getProductSaleCode();
        FundInfoDTO fundInfoDTO = cmbMobileClient.queryFundInfo(fundCode);

        Product product = productRepository.findByInnerCode(fundCode)
                .orElse(new Product());

        product.setSaleCode(fundCode);
        product.setInnerCode(fundCode);
        product.setSaCode(fundCode);
        product.setRiskLevel(fundInfoDTO.getRiskLevel());
        product.setProductTag(fundInfoDTO.getFundTypeDesc());
        product.setType(ProductType.FUND);

        if (product.getId() == null) {
            product.setRiskType("N/A");
            product.setHotProduct(false);
            product.setSellOut("UNKNOWN");
        }

        product.setSalesPlatform(SalesPlatform.CMB);

        String crpNam = fundInfoDTO.getCompanyName();

        product.setOffNae(crpNam);

        product.setShortName(fundInfoDTO.getFundName());
        product.setSubscribed(true);

        productRepository.save(product);

        return product;
    }

    @Override
    public boolean support(SalesPlatform salesPlatform, ProductType productType) {
        return salesPlatform == SalesPlatform.CMB && productType == ProductType.FUND;
    }
}
