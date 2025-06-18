package io.nullptr.cmb.client.dto.request;

import lombok.Data;

@Data
public class FundHistoryNetValueQuery {

    /**
     * 近 1 年：Y001
     * 近 1 月：M001
     * 近 3 月：M003
     * 成立以来：X000
     */
    private String expressCode;
    private String fundCode;
}
