package io.nullptr.cmb.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.nullptr.cmb.client.dto.response.FundDetailDTO;
import io.nullptr.cmb.client.dto.response.FundNetValueDTO;
import io.nullptr.cmb.client.dto.response.base.EastMoneyApiCallResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

public interface EastMoneyApiClient {

    /**
     * 获取基金详情
     */
    @GetExchange("/FundMNewApi/FundMNDetailInformation")
    EastMoneyApiCallResponse<FundDetailDTO> getFundDetail(@RequestParam("FCODE") String fundCode);

    /**
     * 获取基金净值
     */
    @PostExchange("/FundMNewApi/FundMNHisNetList")
    EastMoneyApiCallResponse<List<FundNetValueDTO>> listFundNetValue(@RequestParam("FCODE") String fundCode,
                                                                     @RequestParam("pageIndex") int page,
                                                                     @RequestParam("pageSize") int size);

    @GetExchange("https://fundcomapi.tiantianfunds.com/mm/newCore/FundVPageDiagram")
    EastMoneyApiCallResponse<List<FundNetValueDTO>> listFundNetValue(@RequestParam("FCODE") String fundCode,
                                                                     @RequestParam("RANGE") String range);
}