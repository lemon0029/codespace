package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.request.ProductHistoryYieldOrNetValueQuery;
import io.nullptr.cmb.client.dto.request.ProductNetValueQuery;
import io.nullptr.cmb.client.dto.response.ProductHistoryNetValueQueryResult;
import io.nullptr.cmb.client.dto.response.ProductHistoryPerformanceQueryResult;
import io.nullptr.cmb.client.dto.response.ProductHistoryYieldOrNetValueResult;
import io.nullptr.cmb.client.dto.response.ProductListQueryResult;
import io.nullptr.cmb.client.dto.response.base.ResponseWrapper;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;

public interface CmbMobileApiService {

    /**
     * 获取产品列表
     *
     * @param riskType   风险类型
     * @param productTag 产品标签
     * @return 产品列表
     */
    @GetExchange("/iextef/cp-product-list/get-products")
    ResponseWrapper<ProductListQueryResult> getProducts(@RequestParam("riskType") String riskType,
                                                        @RequestParam("productTag") String productTag);

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
}
