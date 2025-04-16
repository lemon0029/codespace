package io.nullptr.cmb.client.dto.response;

import io.nullptr.cmb.model.DailyNetValue;
import io.nullptr.cmb.model.WeeklyYield;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ProductHistoryYieldOrNetValueResult {
    private String annotation;
    private String comment;
    private String id;
    private String name;
    private String riskWarning;
    private String subtitle;
    private String link;
    private List<Map<String, String>> chart;

    public List<WeeklyYield> getWeeklyYield() {
        List<WeeklyYield> result = new ArrayList<>();

        for (Map<String, String> item : chart) {
            String range = item.get("range");
            String yield = item.get("yield");

            LocalDate rangeDate = LocalDate.parse(range);
            WeeklyYield weeklyYield = new WeeklyYield();
            weeklyYield.setRange(rangeDate);
            weeklyYield.setValue(yield);

            result.add(weeklyYield);
        }

        return result;
    }

    public List<DailyNetValue> getDailyNetValue() {
        List<DailyNetValue> result = new ArrayList<>();

        for (Map<String, String> item : chart) {
            String date = item.get("date");
            String net = item.get("net");

            LocalDate rangeDate = LocalDate.parse(date.replaceAll("\\.", "-"));
            DailyNetValue dailyNetValue = new DailyNetValue();
            dailyNetValue.setDate(rangeDate);
            dailyNetValue.setValue(net);

            result.add(dailyNetValue);
        }

        return result;
    }
}
