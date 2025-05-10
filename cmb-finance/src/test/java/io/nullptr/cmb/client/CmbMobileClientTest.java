package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.response.HotProductListDTO;
import io.nullptr.cmb.client.dto.response.ProductBCDListDTO;
import io.nullptr.cmb.client.dto.response.ProductInfoDTO;
import io.nullptr.cmb.domain.ProductRiskType;
import io.nullptr.cmb.model.DailyNetValue;
import io.nullptr.cmb.model.WeeklyYield;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.util.List;

@SpringBootTest
class CmbMobileClientTest {

    @Autowired
    private CmbMobileClient cmbMobileClient;

    @Test
    void queryFinanceProduct() {
        List<ProductBCDListDTO> items = cmbMobileClient.queryFinanceProduct(ProductRiskType.STEADY_LOW_VOLATILITY, 1);
        Assertions.assertNotNull(items);
        Assertions.assertFalse(items.isEmpty());
    }

    @Test
    void queryProductLabelChart() {
        Pair<List<WeeklyYield>, List<DailyNetValue>> pair =
                cmbMobileClient.queryProductWeeklyYieldAndDailyNet("D07", "JY040210");

        List<WeeklyYield> weeklyYields = pair.getFirst();
        List<DailyNetValue> dailyNets = pair.getSecond();

        Assertions.assertNotNull(weeklyYields);
        Assertions.assertFalse(weeklyYields.isEmpty());

        Assertions.assertNotNull(dailyNets);
        Assertions.assertFalse(dailyNets.isEmpty());
    }

    @Test
    void queryHotProductList() {
        List<HotProductListDTO> hotProducts = cmbMobileClient.queryHotProductList();
        Assertions.assertNotNull(hotProducts);
        Assertions.assertFalse(hotProducts.isEmpty());
    }

    @Test
    void queryProductInfo() {
        String prdCode = "8880A";
        String saCode = "D07";

        ProductInfoDTO productInfoDTO = cmbMobileClient.queryProductInfo(saCode, prdCode);
        Assertions.assertNotNull(productInfoDTO);
        Assertions.assertEquals("招银理财日日金80号A", productInfoDTO.getRipSnm());
    }
}