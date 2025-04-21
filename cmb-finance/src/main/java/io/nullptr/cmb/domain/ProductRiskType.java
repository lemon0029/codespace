package io.nullptr.cmb.domain;

import lombok.Getter;

@Getter
public enum ProductRiskType {
    STEADY_LOW_VOLATILITY("稳健低波", "B"),
    STEADY_GROWTH("稳健增值", "C"),
    BALANCED_ADVANCE("稳中求进", "D");

    private final String desc;
    private final String code;

    ProductRiskType(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }
}
