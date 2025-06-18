package io.nullptr.cmb.domain;

import lombok.Getter;

public enum ProductType {

    /**
     * 基金
     */
    FUND("基金"),

    /**
     * 理财
     */
    WEALTH("理财");

    @Getter
    private final String name;

    ProductType(String name) {
        this.name = name;
    }
}
