package io.nullptr.cmb.appliation.service;

import io.nullptr.cmb.appliation.scheduler.DataSyncTaskProperties;
import io.nullptr.cmb.client.EastMoneyApiService;
import io.nullptr.cmb.client.dto.response.FundDetailDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductType;
import io.nullptr.cmb.domain.SalesPlatform;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EastMoneyProductDataSyncService implements SubscribeProductDataSyncService {

    private final EastMoneyApiService eastMoneyApiService;

    private final ProductRepository productRepository;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    @Override
    public void doSync(List<DataSyncTaskProperties.SubscribedProduct> subscribedProducts) {
        for (DataSyncTaskProperties.SubscribedProduct subscribedProduct : subscribedProducts) {
            Product product = updateProduct(null, subscribedProduct.getProductSaleCode());

            productDataSyncTaskSupport.updateProductNetValue(product);
        }
    }

    public Product updateProduct(String traceIndex, String fundCode) {
        FundDetailDTO fundDetail = eastMoneyApiService.getFundDetail(fundCode);

        Product product = productRepository.findByInnerCode(fundCode)
                .orElse(new Product());

        product.setSaleCode(fundCode);
        product.setInnerCode(fundCode);
        product.setSaCode(fundCode);
        product.setRiskLevel(fundDetail.getRiskLevel());
        product.setProductTag(fundDetail.getType());
        product.setType(ProductType.FUND);

        if (StringUtils.hasText(traceIndex)) {
            product.setTraceIndex(traceIndex);
        }

        if (product.getId() == null) {
            product.setRiskType("N/A");
            product.setHotProduct(false);
            product.setSellOut("UNKNOWN");
        }

        product.setSalesPlatform(SalesPlatform.EAST_MONEY);

        String crpNam = fundDetail.getFundCompany();

        product.setOffNae(crpNam);

        product.setShortName(fundDetail.getShortName());
        product.setSubscribed(true);

        productRepository.save(product);

        return product;
    }

    @Override
    public boolean support(SalesPlatform salesPlatform, ProductType productType) {
        return salesPlatform == SalesPlatform.EAST_MONEY && productType == ProductType.FUND;
    }
}
