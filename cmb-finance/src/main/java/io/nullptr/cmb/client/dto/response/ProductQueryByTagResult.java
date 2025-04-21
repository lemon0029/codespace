package io.nullptr.cmb.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductQueryByTagResult {
    private String productType;
    private String riskType;

    private List<ProductDetail> productDetailList;
    private List<ProductRisk> productRiskList;

    @Data
    public static class ProductDetail {
        private String yieldRate;
        private String yieldRateDes;
        private String shortName;
        private String saleCode;
        private String saleTag;
        private String saleOut;
        private String offNae;
        private String basPrf;
        private String basPrfAdd;
        private String lockOpenDesOne;
        private String lockOpenDesTwo;
        private String opnDesc;
        private String innerCode;
        private String saCode;
        private String riskType;
    }

    @Data
    public static class ProductRisk {
        private String riskType;
        private String riskDes;
    }
}
