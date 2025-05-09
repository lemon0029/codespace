package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HotProductListDTO {
    private String rettype;
    private String code;
    private String os;
    private List<String> city;

    @JsonProperty("effective_os")
    private String effectiveOs;

    @JsonProperty("hotvalue")
    private Long hotValue;

    private String description;
    private Map<String, String> source;
    private String label;
    private String title;
    private String type;
    private String wordGuide;

    @JsonProperty("minitype")
    private String miniType;

    @JsonProperty("tinytype")
    private String tinyType;

    @JsonProperty("subtype")
    private String subType;

    @JsonProperty("uishow")
    private Map<String, Object> uiShow;

    private String tagType;

    @JsonProperty("adddate")
    private String addDate;
}
