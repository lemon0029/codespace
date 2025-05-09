package io.nullptr.cmb.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class WeBankWealthProductListQueryResult {

    private Integer nextCheckTimeDiv;
    private List<Item> productInfoList;

    @Data
    public static class Item {
        private String name;
        private boolean changed;
        private String content;
    }

}
