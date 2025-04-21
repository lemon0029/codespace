package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Data
public class ProductBCDListQueryResult {

    @JsonProperty("yDalCod")
    private String yDalCod;

    @JsonProperty("yPagCnt")
    private String yPagCnt;

    @JsonProperty("yRipCod")
    private String yRipCod;

    @JsonProperty("ySaaCod")
    private String ySaaCod;

    @JsonProperty("timTmp")
    private Long timTmp;

    private List<ProductBCDListDTO> prdList;

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Path.of("test.json");

        ProductBCDListQueryResult productBCDListQueryResult = objectMapper.readValue(Files.readAllBytes(path), ProductBCDListQueryResult.class);
        System.out.println(productBCDListQueryResult);
    }
}
