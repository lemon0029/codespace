package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
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

    private Map<String, Object> allProperties = new HashMap<>();

    @JsonAnySetter
    public void set(String key, Object value) {
        allProperties.put(key, value);
    }
}