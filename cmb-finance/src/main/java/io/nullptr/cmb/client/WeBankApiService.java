package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.request.WeBankWealthProductListQuery;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductListQueryResult;
import io.nullptr.cmb.client.dto.response.base.WeBankApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface WeBankApiService {

    @PostExchange("/wm-hjhtr/wm_product/productinfo/getproductlistbycode")
    WeBankApiResponse<WeBankWealthProductListQueryResult> queryProductList(@RequestBody WeBankWealthProductListQuery query);
}
