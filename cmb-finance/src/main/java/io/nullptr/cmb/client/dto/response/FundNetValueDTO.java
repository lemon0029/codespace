package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundNetValueDTO {

    @JsonProperty("FSRQ")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("DWJZ")
    private BigDecimal netValue;

    @JsonProperty("LJJZ")
    private BigDecimal totalNetValue;

    @JsonProperty("JZZZL")
    private BigDecimal pctChange;

    @JsonProperty("NAVTYPE")
    private String navType;
}
