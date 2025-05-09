package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.request.WeBankWealthProductListQuery;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListQueryResult;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductYieldDTO;
import io.nullptr.cmb.client.dto.response.base.WeBankApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

public interface WeBankApiService {

    /**
     * 根据 Product Code 查询产品信息
     */
    @PostExchange("/wm-hjhtr/wm_product/productinfo/getproductlistbycode")
    WeBankApiResponse<WeBankWealthProductListQueryResult> queryProductList(@RequestBody WeBankWealthProductListQuery query);

    /**
     * 查询产品收益情况（净值、年化收益率）
     */
    @GetExchange("/wm-hjhtr/wm-pqs/query/ta/stock_rates")
    WeBankApiResponse<Map<String, WeBankWealthProductYieldDTO>> queryProductYield(@RequestParam("param") String param);
}
