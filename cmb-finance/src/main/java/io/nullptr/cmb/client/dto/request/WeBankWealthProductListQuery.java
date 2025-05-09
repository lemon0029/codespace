package io.nullptr.cmb.client.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class WeBankWealthProductListQuery {

    private List<Item> productInfoList = new ArrayList<>();

    public void addQueryProduct(String code) {
        boolean exists = productInfoList.stream()
                .anyMatch(it -> Objects.equals(code, it.name));

        if (exists) {
            return;
        }

        Item item = new Item();
        item.setName(code);
        item.setSign("");

        productInfoList.add(item);
    }

    @Data
    public static class Item {
        private String name;
        private String sign;
    }
}
