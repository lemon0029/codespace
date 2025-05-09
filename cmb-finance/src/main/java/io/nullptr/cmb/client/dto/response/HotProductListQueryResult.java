package io.nullptr.cmb.client.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class HotProductListQueryResult {
    private List<HotProductListDTO> ranklist;
}
