package io.nullptr.cmb.service;

import io.nullptr.cmb.client.CmbMobileClient;
import io.nullptr.cmb.client.dto.response.ProductHistoryPerformanceQueryResult;
import io.nullptr.cmb.client.dto.response.ProductQueryByTagResult;
import io.nullptr.cmb.client.dto.response.ProductHistoryNetValueQueryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class CmbFinanceMCPToolService {

    private final CmbMobileClient cmbMobileClient;

    @Tool(description = "获取周周宝理财产品列表")
    public String queryProductListForZZB() {
        String cacheResult = loadResult();

        if (StringUtils.hasText(cacheResult)) {
            return cacheResult;
        }

        StringBuilder builder = new StringBuilder();

        ProductQueryByTagResult result = cmbMobileClient.queryProductByRiskTypeAndTag("", "7");
        List<ProductQueryByTagResult.ProductDetail> productList = result.getProductDetailList();

        for (ProductQueryByTagResult.ProductDetail productDetail : productList) {
            String saCode = productDetail.getSaCode();
            String innerCode = productDetail.getInnerCode();
            ProductHistoryPerformanceQueryResult result1 = cmbMobileClient.queryHistoryPerformance(saCode, innerCode);
            ProductHistoryNetValueQueryResult result2 = cmbMobileClient.queryHistoryNetValue("B", saCode, innerCode);

            builder.append("\n\n\n产品详情：\n");

            for (Field field : productDetail.getClass().getDeclaredFields()) {
                try {
                    field.trySetAccessible();
                    Object fieldValue = field.get(productDetail);

                    builder.append(field.getName()).append(":").append(fieldValue).append(";");
                } catch (IllegalAccessException ignored) {
                }
            }

            builder.append("\n\n")
                    .append("业绩表现：\n");
            for (var performance : result1.getList()) {
                String timeInterval = performance.getTimeInterval();
                String yeaYld = performance.getYeaYld();
                String netValueChange = performance.getNetValueChange();

                builder.append(timeInterval).append(" ")
                        .append("年化：").append(yeaYld).append("；")
                        .append("净值变化：").append(netValueChange).append("\n");
            }

            builder.append("\n")
                    .append("净值变化: \n");

            result2.getNetValueMap()
                    .forEach((date, value) ->
                            builder.append(date)
                                    .append(":")
                                    .append(value)
                                    .append(";")
                    );
        }

        saveResult(builder.toString());

        return builder.toString();
    }

    private String loadResult() {
        String userHome = System.getProperty("user.home");

        Path path = Path.of("%s/.cmb-finance/zzb-products.dat".formatted(userHome));

        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            Duration duration = Duration.between(lastModifiedTime.toInstant(), Instant.now());

            if (duration.toMinutes() > 1) {
                Files.delete(path);
                return null;
            }

            if (Files.exists(path)) {
                return Files.readString(path);
            }
        } catch (IOException ignored) {
        }

        return null;
    }

    private void saveResult(String result) {
        String userHome = System.getProperty("user.home");

        Path path = Path.of("%s/.cmb-finance/zzb-products.dat".formatted(userHome));

        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            Files.write(path, result.getBytes());
        } catch (Exception ignored) {
        }
    }
}
