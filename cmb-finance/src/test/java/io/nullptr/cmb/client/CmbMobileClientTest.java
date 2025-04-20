package io.nullptr.cmb.client;

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
}