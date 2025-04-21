package io.nullptr.cmb.client.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductBCDListDTO {

    /**
     * 成立以来的年化收益率
     */
    private String prdRat;
    private String ratDes;

    /**
     * 产品名称
     */
    private String ripSnm;

    /**
     * 销售机构
     */
    private String zylTag;
    private String ripCod;
    private String saaCod;

    /**
     * 固定为 Y?
     */
    private String newFlg;
    private String fndNbr;

    /**
     * 风险等级和起购设定
     */
    private String prdInf;
    private List<Map<String, String>> prdTags;
    private String terDay;

    /**
     * 剩余可购买额度
     */
    private String dxsTag;

    /**
     * 是否已售罄
     */
    private String sellOut;

    /**
     * 多宝理财标签（周周宝、月月宝等）
     */
    private String jjbTag;
    private String salTim;
    private String prfOpenTag;
}
