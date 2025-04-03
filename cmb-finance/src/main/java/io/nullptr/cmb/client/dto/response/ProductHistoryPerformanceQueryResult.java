package io.nullptr.cmb.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductHistoryPerformanceQueryResult {
    private List<ProductPerformance> list;
    private List<Object> yearList;
    private String yldSwh;
    private String zdfSwh;
    private String nvcShw;

    @Data
    public static class ProductPerformance {
        private String prfTyp;
        private String timeInterval;
        private String netValueChange;
        private String yeaYld;
    }
}
