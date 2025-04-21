package io.nullptr.cmb.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductZsTag {

    // 周周宝 : 7, 月月宝 : A, 季季宝 : B, 半年宝 : C, 多月宝 : E, 定期宝 : H

    ZZB("7", "周周宝"),
    YYB("A", "月月宝"),
    JJB("B", "季季宝"),
    BNB("C", "半年宝"),
    DYB("E", "多月宝"),
    DQB("H", "定期宝");

    private final String code;
    private final String name;

    ProductZsTag(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ProductZsTag fromCode(String code) {
        return Arrays.stream(values())
                .filter(it -> it.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
