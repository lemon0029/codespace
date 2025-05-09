package io.nullptr.cmb.client.dto.response.base;

import lombok.Data;

@Data
public class BizResult<T> {
    private Integer code;
    private boolean success;
    private String message;
    private T data;
}
