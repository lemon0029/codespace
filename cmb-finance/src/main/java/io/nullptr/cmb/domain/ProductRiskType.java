package io.nullptr.cmb.domain;

import lombok.Getter;

@Getter
public enum ProductRiskType {
    STEADY_LOW_VOLATILITY("稳健低波", "A"),
    STEADY_GROWTH("稳健增值", "B"),
    BALANCED_ADVANCE("稳中求进", "C");

    private final String desc;
    private final String code;

    ProductRiskType(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }
}
