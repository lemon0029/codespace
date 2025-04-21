package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.ProductBCDListDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductRiskType;
import io.nullptr.cmb.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 理财产品数据同步任务
 */
@Component
@RequiredArgsConstructor
public class FinanceProductDataSyncTask {

    private final CmbMobileClient cmbMobileClient;

    private final DataSyncTaskProperties dataSyncTaskProperties;

    private final ProductRepository productRepository;

    private final ZsProductDataSyncTaskSupport zsProductDataSyncTaskSupport;

    @Transactional
    @Scheduled(fixedDelay = 1800_000, initialDelay = 100_000)
    public void updateProduct() {

        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() < 10) {
            return;
        }

        List<ProductRiskType> riskTypes = dataSyncTaskProperties.getRiskTypes();
        if (CollectionUtils.isEmpty(riskTypes)) {
            return;
        }

        for (ProductRiskType riskType : riskTypes) {
            List<ProductBCDListDTO> productBCDListDTOS = cmbMobileClient.queryFinanceProduct(riskType);

            List<Product> products = productBCDListDTOS.stream()
                    .map(it -> dto2Domain(riskType, it))
                    .toList();

            productRepository.saveAll(products);
        }
    }

    @Transactional
    @Scheduled(fixedDelay = 7200_000, initialDelay = 200_000)
    public void updateProductNetValue() throws InterruptedException {
        List<ProductRiskType> riskTypes = dataSyncTaskProperties.getRiskTypes();
        if (CollectionUtils.isEmpty(riskTypes)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.getHour() < 10) {
            return;
        }

        for (ProductRiskType riskType : riskTypes) {
            List<Product> products = productRepository.findAllByRiskType(riskType.getCode());

            for (Product product : products) {
                // 多宝理财系列产品，交给另外一个定时任务去同步数据吧
                if (StringUtils.hasText(product.getProductTag())) {
                    continue;
                }

                zsProductDataSyncTaskSupport.updateProductNetValue(product);
                TimeUnit.MILLISECONDS.sleep(700);
            }
        }
    }


    private Product dto2Domain(ProductRiskType riskType, ProductBCDListDTO dto) {
        Product product = productRepository.findByInnerCode(dto.getRipCod())
                .orElse(new Product());

        if (product.getId() == null) {
            product.setOffNae(dto.getZylTag());
            product.setSaleCode(dto.getRipCod());
            product.setInnerCode(dto.getRipCod());
            product.setShortName(dto.getRipSnm());
            product.setSaCode(dto.getSaaCod());
            product.setRiskType(riskType.getCode());
            product.setProductTag(dto.getJjbTag());
        }

        String sellOut = dto.getSellOut();
        product.setSellOut(sellOut == null ? "" : sellOut);
        product.setQuota(dto.getDxsTag());

        String prdInf = dto.getPrdInf();
        if (prdInf != null) {
            String[] parts = prdInf.split(" I ", 2);
            if (parts.length == 2) {
                product.setRiskLevel(parts[0]);
            }
        }

        return product;
    }
}
