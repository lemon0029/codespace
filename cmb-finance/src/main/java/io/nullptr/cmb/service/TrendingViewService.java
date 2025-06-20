package io.nullptr.cmb.service;

import io.nullptr.cmb.controller.dto.GrafanaDataFrame;
import io.nullptr.cmb.controller.dto.TrendViewRequestData;
import io.nullptr.cmb.domain.Product;
import io.nullptr.cmb.domain.ProductNetValue;
import io.nullptr.cmb.domain.repository.ProductNetValueRepository;
import io.nullptr.cmb.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendingViewService {

    private final ProductRepository productRepository;

    private final ProductNetValueRepository productNetValueRepository;

    public List<GrafanaDataFrame> generate(TrendViewRequestData requestData) {
        List<String> productCodes = requestData.getProductCodes();

        LocalDate startDate = requestData.getStartDate();
        LocalDate endDate = requestData.getEndDate();

        List<ProductNetValue> allNetValues = productNetValueRepository.findAllByInnerCodeInAndDateBetween(productCodes, startDate, endDate);

        Map<String, List<ProductNetValue>> netValuesGroupedByProductCode = allNetValues.stream()
                .collect(Collectors.groupingBy(ProductNetValue::getInnerCode));

        List<GrafanaDataFrame> result = new ArrayList<>();

        for (String productCode : productCodes) {
            Product product = productRepository.findByInnerCode(productCode).orElseThrow();
            List<ProductNetValue> netValues = netValuesGroupedByProductCode.get(productCode);

            List<GrafanaDataFrame> tmp = generate(product, startDate, endDate, requestData.getMetricName(), netValues);
            result.addAll(tmp);
        }

        return result;
    }

    private List<GrafanaDataFrame> generate(Product product, LocalDate startDate, LocalDate endDate, String metricName, List<ProductNetValue> netValues) {

        List<GrafanaDataFrame> result = new ArrayList<>();

        Map<LocalDate, ProductNetValue> netValuesGroupedByDate = netValues.stream()
                .collect(Collectors.toMap(ProductNetValue::getDate, Function.identity()));

        BigDecimal amount = new BigDecimal("10000");

        GrafanaDataFrame init = new GrafanaDataFrame();
        init.setProductCode(product.getInnerCode());
        init.setProductName(product.getShortName());

        long iepochMilli = startDate.atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        init.setTime(iepochMilli);
        init.setValue(amount.multiply(BigDecimal.ONE));

        result.add(init);

        for (LocalDate date = startDate.plusDays(1); date.isBefore(endDate); date = date.plusDays(1)) {
            ProductNetValue netValue = netValuesGroupedByDate.get(date);

            if (netValue != null) {
                BigDecimal pctChange = netValue.getPctChange();

                amount = amount.multiply(pctChange)
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                        .add(amount);
            }

            GrafanaDataFrame grafanaDataFrame = new GrafanaDataFrame();
            grafanaDataFrame.setProductCode(product.getInnerCode());
            grafanaDataFrame.setProductName(product.getShortName());

            long epochMilli = date.atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            grafanaDataFrame.setTime(epochMilli);
            grafanaDataFrame.setValue(amount.multiply(BigDecimal.ONE));

            result.add(grafanaDataFrame);
        }


        return result;
    }
}
