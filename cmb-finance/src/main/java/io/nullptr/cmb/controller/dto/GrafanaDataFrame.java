package io.nullptr.cmb.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GrafanaDataFrame {
    private String productCode;
    private String productName;
    private Long time;
    private BigDecimal value;
}
