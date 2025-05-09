package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WeBankWealthProductYieldDTO {

    @JsonProperty("prod_code")
    private String productCode;

    @JsonFormat(pattern = "yyyyMMdd")
    @JsonProperty("earnings_rate_date")
    private LocalDate earningsRateDate;

    @JsonProperty("accu_net_value")
    private BigDecimal accuNetValue;

    @JsonProperty("unit_net_value")
    private BigDecimal unitNetValue;

    @JsonProperty("daily_increase_change")
    private BigDecimal dailyIncreaseChange;

    @JsonProperty("fund_begin_yield")
    private BigDecimal fundBeginYield;

    @JsonProperty("month_yield")
    private BigDecimal monthYield;
}
