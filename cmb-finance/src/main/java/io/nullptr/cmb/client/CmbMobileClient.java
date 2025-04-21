package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.request.ProductBCDListQuery;
import io.nullptr.cmb.client.dto.request.ProductHistoryYieldOrNetValueQuery;
import io.nullptr.cmb.client.dto.request.ProductNetValueQuery;
import io.nullptr.cmb.client.dto.response.*;
import io.nullptr.cmb.client.dto.response.base.BizResult;
import io.nullptr.cmb.client.dto.response.base.ResponseWrapper;
import io.nullptr.cmb.domain.ProductRiskType;
import io.nullptr.cmb.model.DailyNetValue;
import io.nullptr.cmb.model.WeeklyYield;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CmbMobileClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://mobile.cmbchina.com")
            .build();

    private final RestClientAdapter adapter = RestClientAdapter.create(restClient);
    private final HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    private final CmbMobileApiService service = factory.createClient(CmbMobileApiService.class);

    public List<ProductBCDListDTO> queryFinanceProduct(ProductRiskType riskType) {
        return queryFinanceProduct(riskType, 50);
    }

    public List<ProductBCDListDTO> queryFinanceProduct(ProductRiskType riskType, Integer pageLimit) {
        ProductBCDListQuery query = new ProductBCDListQuery();
        query.setPrdTyp(riskType.getCode());
        query.setTimTmp(System.currentTimeMillis());

        int pageCount = 0;

        List<ProductBCDListDTO> result = new ArrayList<>();

        log.info("Start to query finance product, query: {}", query);

        while (true) {
            var responseWrapper = service.queryBCDProductList(query);

            var sysCode = responseWrapper.getSysCode();
            var sysMsg = responseWrapper.getSysMsg();

            if (!"Success".equals(sysMsg) || !Objects.equals(1014, sysCode)) {
                log.warn("Failed to query product bcd list, params: {}, response: {}", query, responseWrapper);
                break;
            }

            var bizResult = responseWrapper.getBizResult();

            Integer code = bizResult.getCode();
            ProductBCDListQueryResult queryResult = bizResult.getData();

            if (!Objects.equals(code, 200) || queryResult == null || CollectionUtils.isEmpty(queryResult.getPrdList())) {
                log.warn("Failed to query product bcd list, params: {}, result: {}", query, queryResult);
                break;
            }

            pageCount++;

            result.addAll(queryResult.getPrdList());

            log.info("Query fiance product success, page: {}/{}, result: {}", pageCount, pageLimit, queryResult.getPrdList());

            query.setYDalCod(queryResult.getYDalCod());
            query.setYPagCnt(queryResult.getYPagCnt());
            query.setYRipCod(queryResult.getYRipCod());
            query.setYSaaCod(queryResult.getYSaaCod());
            query.setTimTmp(queryResult.getTimTmp());

            if (!StringUtils.hasText(query.getYRipCod())) {
                // 没有下一页了，停止循环
                break;
            }

            if (pageLimit != null && pageCount >= pageLimit) {
                // 达到分页请求限制了，通常是为了避免死循环请求
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        log.info("Finished to query bcd product list, page count: {}, total product: {}", pageCount, result.size());
        return result;
    }

    public ProductQueryByTagResult queryProductByTag(String productTag) {
        ResponseWrapper<ProductQueryByTagResult> responseWrapper = service.queryProductByTag("", productTag);
        BizResult<ProductQueryByTagResult> bizResult = responseWrapper.getBizResult();

        return Optional.ofNullable(bizResult)
                .map(BizResult::getData)
                .orElse(null);
    }

    public ProductHistoryPerformanceQueryResult queryHistoryPerformance(String saCode, String innerCode) {
        ResponseWrapper<ProductHistoryPerformanceQueryResult> responseWrapper = service.getHistoryPerformance(saCode, innerCode);

        BizResult<ProductHistoryPerformanceQueryResult> bizResult = responseWrapper.getBizResult();

        return Optional.ofNullable(bizResult)
                .map(BizResult::getData)
                .orElse(null);
    }

    /**
     * 获取产品的历史净值数据
     *
     * @param dataType 数据范围：A - 近一个月; B - 近三个月; C - 近一年; D - 成立以来
     */
    public ProductHistoryNetValueQueryResult queryHistoryNetValue(String dataType, String saCode, String innerCode) {
        ProductNetValueQuery productNetValueQuery = new ProductNetValueQuery();
        productNetValueQuery.setDatTyp(dataType); // A 近一个月; B 近三个月; C 近一年; D 成立以来
        productNetValueQuery.setSaCode(saCode);
        productNetValueQuery.setPrdCode(innerCode);

        ResponseWrapper<ProductHistoryNetValueQueryResult> responseWrapper = service.getHistoryNetValue(productNetValueQuery);

        BizResult<ProductHistoryNetValueQueryResult> bizResult = responseWrapper.getBizResult();

        return Optional.ofNullable(bizResult)
                .map(BizResult::getData)
                .orElse(null);
    }

    /**
     * 获取产品的历史 12 周收益和近一年的净值变化
     */
    public Pair<List<WeeklyYield>, List<DailyNetValue>>
    queryProductWeeklyYieldAndDailyNet(String saCode, String innerCode) {
        ProductHistoryYieldOrNetValueQuery query = new ProductHistoryYieldOrNetValueQuery();
        query.setSaCode(saCode);
        query.setPrdCode(innerCode);
        query.setLabelIds(List.of("200003.LY", "200005.LY"));

        var responseWrapper = service.getHistoryYieldOrNetValue(query);

        var bizResult = responseWrapper.getBizResult();

        Map<String, List<ProductHistoryYieldOrNetValueResult>> data = bizResult.getData();

        if (data == null || data.isEmpty()) {
            return null;
        }

        // 200003.LY 最近 12 周的收益
        List<ProductHistoryYieldOrNetValueResult> label2 = data.get("label2");

        // 200005.LY 最近 1 年的净值变化
        List<ProductHistoryYieldOrNetValueResult> label3 = data.get("label3");

        List<WeeklyYield> weeklyYields = Collections.emptyList();
        List<DailyNetValue> dailyNets = Collections.emptyList();

        if (label2 != null && !label2.isEmpty()) {
            weeklyYields = label2.getFirst().getWeeklyYield();
        }

        if (label3 != null && !label3.isEmpty()) {
            dailyNets = label3.getFirst().getDailyNetValue();
        }

        return Pair.of(weeklyYields, dailyNets);
    }
}
