package io.nullptr.cmb.client;

import com.fasterxml.jackson.databind.JsonNode;
import io.nullptr.cmb.client.dto.request.*;
import io.nullptr.cmb.client.dto.response.*;
import io.nullptr.cmb.client.dto.response.base.ResponseWrapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;

public interface CmbMobileApiService {

    /**
     * 根据标签查询产品（仅限于周周宝、月月宝这些产品）
     *
     * @param riskType   风险类型
     * @param productTag 产品标签
     * @return 产品列表
     */
    @GetExchange("/iextef/cp-product-list/get-products")
    ResponseWrapper<ProductQueryByTagResult> queryProductByTag(@RequestParam("riskType") String riskType,
                                                               @RequestParam("productTag") String productTag);

    /**
     * 根据风险类型查询产品
     */
    @PostExchange("/ientrustfinance/financelist/bcdlist")
    ResponseWrapper<ProductBCDListQueryResult> queryBCDProductList(@RequestBody ProductBCDListQuery query);

    /**
     * 获取产品的历史业绩
     */
    @GetExchange("/ientrustfinance/product-statistics/get-history-performance")
    ResponseWrapper<ProductHistoryPerformanceQueryResult> getHistoryPerformance(@RequestParam("saaCode") String saCode,
                                                                                @RequestParam("ripInn") String innerCode);

    /**
     * 获取产品的历史净值
     */
    @PostExchange("/ientrustfinance/product-statistics/net-value-chart")
    ResponseWrapper<ProductHistoryNetValueQueryResult> getHistoryNetValue(@RequestBody ProductNetValueQuery query);

    /**
     * 获取产品的历史每周收益和净值
     */
    @PostExchange("/ientrustfinance/sa-finance-detail/label-descript")
    ResponseWrapper<Map<String, List<ProductHistoryYieldOrNetValueResult>>> getHistoryYieldOrNetValue(@RequestBody ProductHistoryYieldOrNetValueQuery query);

    /**
     * 获取产品信息
     */
    @PostExchange("/ientrustfinance/sa-finance-detail/prd-info")
    ResponseWrapper<ProductInfoDTO> queryProductInfoByCode(@RequestBody ProductInfoQuery query);

    /**
     * 获取基金详情
     */
    @PostExchange("/ifundprds/fund-detail/query-fund-detail")
    ResponseWrapper<JsonNode> queryFundDetail(@RequestBody String fundCode);

    /**
     * 获取基金的历史净值
     */
    @PostExchange("/ifundprds/fund-detail-charts/query-net-value")
    ResponseWrapper<List<FundHistoryNetValueDTO>> queryFundHistoryNetValue(@RequestBody FundHistoryNetValueQuery query);
}
