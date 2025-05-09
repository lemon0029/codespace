package io.nullptr.cmb.client.dto.response.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeBankApiResponse<T> {
    @JsonProperty("ret_code")
    private String retCode;

    @JsonProperty("ret_msg")
    private String retMsg;

    @JsonProperty("token_status")
    private String tokenStatus;

    @JsonProperty("biz_no")
    private String bizNo;

    @JsonProperty("process_type")
    private String processType;

    @JsonProperty("ret_data")
    private T retData;

    @JsonProperty("system_time")
    private String systemTime;

    @JsonProperty("hj_biz_no")
    private String hjBizNo;
}
