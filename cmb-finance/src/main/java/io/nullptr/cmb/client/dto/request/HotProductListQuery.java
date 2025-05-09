package io.nullptr.cmb.client.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 热销产品查询参数
 */
@Data
@AllArgsConstructor
public class HotProductListQuery {

    /**
     * finance - 理财产品
     * fund - 基金产品？
     */
    @JsonProperty("rettype")
    private String retType;

    private String os;
}
