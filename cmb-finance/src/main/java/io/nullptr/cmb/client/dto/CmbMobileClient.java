package io.nullptr.cmb.client.dto;

import io.nullptr.cmb.client.CmbMobileApiService;
import io.nullptr.cmb.client.dto.request.ProductNetValueQuery;
import io.nullptr.cmb.client.dto.response.ProductHistoryNetValueQueryResult;
import io.nullptr.cmb.client.dto.response.ProductHistoryPerformanceQueryResult;
import io.nullptr.cmb.client.dto.response.ProductListQueryResult;
import io.nullptr.cmb.client.dto.response.base.BizResult;
import io.nullptr.cmb.client.dto.response.base.ResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

@Component
public class CmbMobileClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://mobile.cmbchina.com")
            .build();

    private final RestClientAdapter adapter = RestClientAdapter.create(restClient);
    private final HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    private final CmbMobileApiService service = factory.createClient(CmbMobileApiService.class);

    public ProductListQueryResult queryProductList(Integer productTag) {
        ResponseWrapper<ProductListQueryResult> responseWrapper = service.getProducts("", productTag);
        BizResult<ProductListQueryResult> bizResult = responseWrapper.getBizResult();

        return Optional.ofNullable(bizResult)
                .map(BizResult::getData)
                .orElse(null);
    }

    public ProductHistoryPerformanceQueryResult queryHistoryPerformance(String saCode, String innerCode) {
        ResponseWrapper<ProductHistoryPerformanceQueryResult> responseWrapper = service.getHistoryPerformance(saCode, innerCode);

        BizResult<ProductHistoryPerformanceQueryResult> bizResult = responseWrapper.getBizResult();

        return Optional.ofNullable(bizResult)
                .map(BizResult::getData)
                .orElse(null);
    }

    /**
     * 获取产品的历史净值数据
     *
     * @param dataType 数据范围：A - 近一个月; B - 近三个月; C - 近一年; D - 成立以来
     */
    public ProductHistoryNetValueQueryResult queryHistoryNetValue(String dataType, String saCode, String innerCode) {
        ProductNetValueQuery productNetValueQuery = new ProductNetValueQuery();
        productNetValueQuery.setDatTyp(dataType); // A 近一个月; B 近三个月; C 近一年; D 成立以来
        productNetValueQuery.setSaCode(saCode);
        productNetValueQuery.setPrdCode(innerCode);

        ResponseWrapper<ProductHistoryNetValueQueryResult> responseWrapper = service.getHistoryNetValue(productNetValueQuery);

        BizResult<ProductHistoryNetValueQueryResult> bizResult = responseWrapper.getBizResult();

        return Optional.ofNullable(bizResult)
                .map(BizResult::getData)
                .orElse(null);
    }
}
