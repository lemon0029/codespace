package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInfoDTO {
    private String saaCod;
    private String ripInn;
    private String ripSnm;
    private String ribNbr;
    private String crpNam;
    private String crpCod;
    private String terDay;
    private String riskLvl;
    private String buySum;
    private String jjbTag;
    private String salTag;
}
