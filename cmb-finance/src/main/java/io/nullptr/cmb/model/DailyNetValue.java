package io.nullptr.cmb.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * 每日净值
 */
@Data
public class DailyNetValue {

    private LocalDate date;

    /**
     * 净值
     */
    private String value;
}
