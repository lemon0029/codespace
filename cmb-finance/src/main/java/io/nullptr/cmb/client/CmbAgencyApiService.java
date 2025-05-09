package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.request.HotProductListQuery;
import io.nullptr.cmb.client.dto.response.HotProductListQueryResult;
import io.nullptr.cmb.client.dto.response.base.BizResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface CmbAgencyApiService {

    /**
     * 获取基金/理财产品排行榜
     */
    @PostExchange("/init/subhotrank")
    BizResult<HotProductListQueryResult> queryHotProductList(@RequestBody HotProductListQuery query);
}
