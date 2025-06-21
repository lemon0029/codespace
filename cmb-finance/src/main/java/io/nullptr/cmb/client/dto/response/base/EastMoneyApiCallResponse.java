package io.nullptr.cmb.client.dto.response.base;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EastMoneyApiCallResponse<T> {

    @JsonProperty("ErrCode")
    private int errCode;

    @JsonProperty("Success")
    private boolean success;

    @JsonProperty("ErrMsg")
    @JsonAlias("firstError")
    private String errMsg;

    @JsonProperty("Datas")
    @JsonAlias("data")
    private T data;

    @JsonProperty("TotalCount")
    private int totalCount;
}