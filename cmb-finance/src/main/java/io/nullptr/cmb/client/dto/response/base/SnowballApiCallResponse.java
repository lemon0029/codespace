package io.nullptr.cmb.client.dto.response.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SnowballApiCallResponse<T> {

    @JsonProperty("result_code")
    private int code;

    private T data;
}
