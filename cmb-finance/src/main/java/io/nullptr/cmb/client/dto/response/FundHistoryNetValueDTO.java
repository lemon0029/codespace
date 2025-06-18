package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FundHistoryNetValueDTO {

    @JsonProperty("ZDACFLG")
    private String zdacflg;

    @JsonProperty("ZDACRAT")
    private String zdacrat;

    @JsonProperty("ZDACTYP")
    private String zdactyp;

    @JsonProperty("ZHSNYLD")
    private String zhsnyld;

    @JsonProperty("ZNAVCHG")
    private String znavchg;

    /**
     * 日期
     */
    @JsonProperty("ZNAVDAT")
    private String znavdat;

    /**
     * 日跌涨幅
     */
    @JsonProperty("ZNAVPCT")
    private String znavpct;

    /**
     * 单位净值
     */
    @JsonProperty("ZNAVVAL")
    private String znavval;

    @JsonProperty("ZRHTFLG")
    private String zrhtflg;

    @JsonProperty("ZRHTRAT")
    private String zrhrtat;
}
