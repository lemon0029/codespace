package io.nullptr.cmb.client.dto.request;

import lombok.Data;

@Data
public class ProductBCDListQuery {
    private String ngtMkt;
    private String timeLow;
    private String ordTyp;
    private String hotFlg;
    private String timLmt;
    private String rseFrm;
    private String prdFrm;
    private String offRgn;
    private String yRipCod;
    private String ccyNbr;
    private String yDalCod;
    private String ySaaCod;
    private String slfTag;
    private String timHig;
    private String yPagCnt;
    private String evlLvl;
    private String trnCst;
    private String buyNf;
    private String bblTyp;
    private Long timTmp;
    private String encType;
    private String prdTyp;
}
