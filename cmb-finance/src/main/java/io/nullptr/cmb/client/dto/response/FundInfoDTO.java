package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundInfoDTO {

    @JsonProperty("ZCHNABR")
    private String fundName;

    @JsonProperty("ZAPPDES")
    private String fundTypeDesc;

    @JsonProperty("ZRSKLVL")
    private String riskLevel;

    @JsonProperty("ZSIZQTY")
    private String fundScale;

    @JsonProperty("ZCHNMID")
    private String companyName;
}
