package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexFundTraceDTO {

    private int size;

    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("total_items")
    private int totalItems;

    @JsonProperty("total_pages")
    private int totalPages;

    private List<FundsGroupByType> items;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FundsGroupByType {
        @JsonProperty("fund_type")
        private String fundType;

        private List<FundDTO> funds;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FundDTO {
        @JsonProperty("declare_rate")
        private double declareRate;

        @JsonProperty("establish_years")
        private int establishYears;

        @JsonProperty("fund_code")
        private String fundCode;

        @JsonProperty("fund_name")
        private String fundName;
    }
}
