package io.nullptr.cmb.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.nullptr.cmb.client.dto.response.base.SnowballApiCallResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface SnowballFundApiService {

    @GetExchange("/djapi/fund/{symbol}")
    SnowballApiCallResponse<JsonNode> queryFundInfo(@PathVariable String symbol);
}
