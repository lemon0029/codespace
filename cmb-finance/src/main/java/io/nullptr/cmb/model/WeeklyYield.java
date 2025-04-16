package io.nullptr.cmb.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * 每周收益
 */
@Data
public class WeeklyYield {

    /**
     * 日期（每周）
     */
    private LocalDate range;

    /**
     * 每一万元的收益
     */
    private String value;
}
