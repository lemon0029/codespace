package io.nullptr.cmb.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.nullptr.cmb.client.dto.response.FundDetailDTO;
import io.nullptr.cmb.client.dto.response.FundNetValueDTO;
import io.nullptr.cmb.client.dto.response.base.EastMoneyApiCallResponse;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EastMoneyMobileApiService {

    private static final EastMoneyApiClient API_CLIENT = createEastMoneyMobileApiClient();

    public FundDetailDTO getFundDetail(String fundCode) {
        EastMoneyApiCallResponse<FundDetailDTO> response = API_CLIENT.getFundDetail(fundCode);

        return response.getData();
    }

    /**
     * 获取基金净值
     *
     * @param fundCode 基金代码
     * @param range    时间范围
     * @return 基金净值
     */
    public List<FundNetValueDTO> listFundNetValue(String fundCode, String range) {
        EastMoneyApiCallResponse<List<FundNetValueDTO>> response = API_CLIENT.listFundNetValue(fundCode, range);

        return response.getData();
    }

    private static EastMoneyApiClient createEastMoneyMobileApiClient() {
        Map<String, String> commonUriVariables = new HashMap<>();
        commonUriVariables.put("product", "EFund");
        commonUriVariables.put("deviceID", "874C427C-7C24-4980-A835-66FD40B67605");
        commonUriVariables.put("plat", "iPhone");
        commonUriVariables.put("version", "6.5.5");

        String baseUrl = "https://fundmobapi.eastmoney.com";

        final RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    Field uriField = ReflectionUtils.findField(request.getClass(), "uri");
                    Assert.notNull(uriField, "URI field not found in request class");

                    ReflectionUtils.makeAccessible(uriField);
                    URI currentUri = (URI) ReflectionUtils.getField(uriField, request);
                    Assert.notNull(currentUri, "Unexpected null URI in request");

                    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(currentUri);
                    commonUriVariables.forEach(uriComponentsBuilder::queryParam);

                    ReflectionUtils.setField(uriField, request, uriComponentsBuilder.build().toUri());

                    return execution.execute(request, body);
                })
                .build();

        final RestClientAdapter adapter = RestClientAdapter.create(restClient);
        final HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(EastMoneyApiClient.class);
    }
}