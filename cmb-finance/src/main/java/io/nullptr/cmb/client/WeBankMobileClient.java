package io.nullptr.cmb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nullptr.cmb.client.dto.request.WeBankWealthProductListQuery;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListDTO;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListQueryResult;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductYieldDTO;
import io.nullptr.cmb.client.dto.response.base.WeBankApiResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeBankMobileClient {

    private static final WeBankApiService weBankApiService = createWeBankApiService();

    private final ObjectMapper objectMapper;

    /**
     * 查询产品收益情况
     */
    @NonNull
    public List<WeBankWealthProductYieldDTO> queryProductYield(String productCode, LocalDate startDate, LocalDate endDate) {
        // {"prod_code":"ZGN2460033","start_date":"20240509","end_date":"20250509"}
        Map<String, String> param = Map.of(
                "prod_code", productCode,
                "start_date", startDate.format(DateTimeFormatter.BASIC_ISO_DATE),
                "end_date", endDate.format(DateTimeFormatter.BASIC_ISO_DATE)
        );

        String paramAsString;

        try {
            paramAsString = objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException ignored) {
            // never reached!!!
            return Collections.emptyList();
        }

        WeBankApiResponse<Map<String, WeBankWealthProductYieldDTO>> response = weBankApiService.queryProductYield(paramAsString);

        if (response == null || !"23520000".equals(response.getRetCode()) || response.getRetData() == null) {
            log.warn("Failed to query we-bank wealth product yield, param:{}, response: {}", param, response);
            return Collections.emptyList();
        }

        Map<String, WeBankWealthProductYieldDTO> retData = response.getRetData();
        return retData.values().stream()
                .sorted((o1, o2) -> o2.getEarningsRateDate().compareTo(o1.getEarningsRateDate()))
                .toList();
    }

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
            log.warn("Failed to query we-bank wealth product, response: {}", response);
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
