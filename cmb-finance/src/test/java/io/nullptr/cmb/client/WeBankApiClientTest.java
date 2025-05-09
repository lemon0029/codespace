package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.response.WeBankWealthProductListDTO;
import io.nullptr.cmb.client.dto.response.WeBankWealthProductYieldDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WeBankApiClientTest {

    @Autowired
    private WeBankApiClient weBankApiClient;

    @Test
    void queryProductByCode() {
        List<String> productWatchlist = List.of("25135011A", "ZGN2460034", "2501240018", "A32060", "ZGN2460033", "YJ01240951A", "A32052", "YJ01240793A");
        List<WeBankWealthProductListDTO> productListDTOS = weBankApiClient.queryProductByCode(productWatchlist);
        assertNotNull(productListDTOS);
        assertFalse(productListDTOS.isEmpty());
    }

    @Test
    void queryProductYield() {
        String productCode = "25135011A";
        LocalDate endDate = LocalDate.parse("2025-05-08");
        LocalDate startDate = endDate.minusYears(1);

        List<WeBankWealthProductYieldDTO> productYieldDTOS = weBankApiClient.queryProductYield(productCode, startDate, endDate);
        assertNotNull(productYieldDTOS);
        assertFalse(productYieldDTOS.isEmpty());
    }
}