package io.nullptr.cmb.client.dto.response.base;

import lombok.Data;

@Data
public class ResponseWrapper<T> {

    private Integer sysCode;
    private String sysMsg;
    private BizResult<T> bizResult;
}
