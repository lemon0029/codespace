package io.nullptr.cmb.appliation.scheduler;

import io.nullptr.cmb.appliation.service.ProductDataSyncTaskSupport;
import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.HotProductListDTO;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HotProductDataSyncTask {

    private final DataSyncTaskProperties dataSyncTaskProperties;

    private final CmbMobileClient cmbMobileClient;

    private final ProductDataSyncTaskSupport productDataSyncTaskSupport;

    private final ProductRepository productRepository;

    @Transactional
    @Scheduled(cron = "0 5 */1 * * *")
    public void execute() {
        if (!dataSyncTaskProperties.isHotProductListDataSyncEnabled()) {
            return;
        }

        List<HotProductListDTO> hotProducts = cmbMobileClient.queryHotProductList();

        for (HotProductListDTO dto : hotProducts) {
            Map<String, String> source = dto.getSource();
            String crpSnm = source.get("CRP_SNM");
            String saaCod = source.get("SAA_COD");
            String invTyp = source.get("INV_TYP");
            String productCode = dto.getCode();

            Product product = productRepository.findByInnerCode(productCode)
                    .orElse(new Product());

            product.setSaleCode(productCode);
            product.setInnerCode(productCode);
            product.setShortName(dto.getTitle());
            product.setOffNae(crpSnm);
            product.setSaCode(saaCod);
            product.setRiskType(invTyp);
            product.setHotProduct(true);

            if (product.getId() == null) {
                product.setProductTag("");
                product.setSellOut("UNKNOWN");
            }

            productRepository.save(product);

            productDataSyncTaskSupport.updateProductNetValue(product);
        }
    }
}
