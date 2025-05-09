package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeBankWealthProductListDTO {

    @JsonProperty("wms_product_type")
    private String wmsProductType;

    @JsonProperty("invest_term")
    private String investTerm;

    @JsonProperty("invest_term_unit")
    private String investTermUnit;

    @JsonProperty("prod_buy_switch")
    private String prodBuySwitch;

    @JsonProperty("prod_code")
    private String prodCode;

    @JsonProperty("prod_full_name")
    private String prodFullName;

    @JsonProperty("prod_short_name")
    private String prodShortName;

    @JsonProperty("prod_name")
    private String prodName;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("purchase_quota_flag")
    private String purchaseQuotaFlag;

    @JsonProperty("risk_level")
    private String riskLevel;

    @JsonProperty("ta_code")
    private String taCode;

    @JsonProperty("ta_name")
    private String taName;
}
