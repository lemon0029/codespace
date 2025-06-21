package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FundDetailDTO {

    @JsonProperty("FCODE")
    private String code;

    @JsonProperty("SHORTNAME")
    private String shortName;

    @JsonProperty("FULLNAME")
    private String fullName;

    @JsonProperty("FTYPE")
    private String type;

    @JsonProperty("ESTABDATE")
    private String establishmentDate;

    @JsonProperty("RISKLEVEL")
    private String riskLevel;

    @JsonProperty("JJGS")
    private String fundCompany;
}