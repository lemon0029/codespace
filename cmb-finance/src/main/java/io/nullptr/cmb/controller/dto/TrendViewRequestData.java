package io.nullptr.cmb.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Data
public class TrendViewRequestData {

    @JsonProperty("products")
    private String productCodes;

    private String metricName;

    private Long startTime;
    private Long endTime;

    public List<String> getProductCodes() {
        String tmp = productCodes;

        if (tmp.startsWith("{")) {
            tmp = tmp.substring(1);
        }

        if (tmp.endsWith("}")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }

        return List.of(tmp.split(","));
    }

    public LocalDate getStartDate() {
        Instant instant = Instant.ofEpochMilli(startTime);
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    public LocalDate getEndDate() {
        Instant instant = Instant.ofEpochMilli(endTime);
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }
}
