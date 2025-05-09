package io.nullptr.cmb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nullptr.cmb.client.dto.request.WeBankWealthProductListQuery;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListDTO;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListQueryResult;
import io.nullptr.cmb.client.dto.response.base.WeBankApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeBankMobileClient {

    private static final WeBankApiService weBankApiService = createWeBankApiService();

    private final ObjectMapper objectMapper;

    /**
     * 查询产品信息
     */
    public List<WeBankWealthProductListDTO> queryProductByCode(List<String> products) {
        WeBankWealthProductListQuery query = new WeBankWealthProductListQuery();

        for (String productCode : products) {
            query.addQueryProduct(productCode);
        }

        WeBankApiResponse<WeBankWealthProductListQueryResult> response = weBankApiService.queryProductList(query);

        if (response == null || !"20350000".equals(response.getRetCode()) || response.getRetData() == null) {
            log.warn("Failed query we-bank wealth product, response: {}", response);
            return Collections.emptyList();
        }

        WeBankWealthProductListQueryResult queryResult = response.getRetData();
        List<WeBankWealthProductListQueryResult.Item> productInfoList = queryResult.getProductInfoList();

        return productInfoList.stream()
                .map(it -> {
                    try {
                        return objectMapper.readValue(it.getContent(), WeBankWealthProductListDTO.class);
                    } catch (JsonProcessingException e) {
                        log.warn("Failed convert to WeBankWealthProductListDTO: {}", it.getContent(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static WeBankApiService createWeBankApiService() {
        final RestClient restClient = RestClient.builder()
                .baseUrl("https://personalv6.webankwealth.com")
                .defaultHeader("User-Agent", "WebankApp/2025041815493355 CFNetwork/3826.500.111.2.2 Darwin/24.4.0")
                .build();

        final RestClientAdapter adapter = RestClientAdapter.create(restClient);
        final HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(WeBankApiService.class);
    }
}
