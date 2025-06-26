package io.nullptr.cmb.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.nullptr.cmb.client.dto.response.IndexFundTraceDTO;
import io.nullptr.cmb.client.dto.response.base.SnowballApiCallResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnowballFundApiClient {

    private static final SnowballFundApiService API_SERVICE = createSnowballApiService();

    public void getIndexTraces(String symbol) {
        SnowballApiCallResponse<IndexFundTraceDTO> apiCallResponse = API_SERVICE.getIndexTraces(symbol);

        System.out.println(apiCallResponse.getData());
    }

    public void queryFundInfo(String symbol) {
        SnowballApiCallResponse<JsonNode> apiCallResponse = API_SERVICE.queryFundInfo(symbol);

        System.out.println(apiCallResponse.getData());
    }

    private static SnowballFundApiService createSnowballApiService() {
        final RestClient restClient = RestClient.builder()
                .baseUrl("https://danjuanfunds.com")
                .build();

        final RestClientAdapter adapter = RestClientAdapter.create(restClient);
        final HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(SnowballFundApiService.class);
    }
}
