package io.nullptr.cmb.client.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProductHistoryYieldOrNetValueQuery {
    private String prdCode;
    private String saCode;
    private List<String> labelIds;
}
