package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.response.WeBankWealthProductListDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class WeBankMobileClientTest {

    @Autowired
    private WeBankMobileClient weBankMobileClient;

    @Test
    void queryProductByCode() {
        List<String> productWatchlist = List.of("25135011A", "ZGN2460034", "2501240018", "A32060", "ZGN2460033", "YJ01240951A", "A32052", "YJ01240793A");
        List<WeBankWealthProductListDTO> weBankWealthProductListDTOS = weBankMobileClient.queryProductByCode(productWatchlist);
        assertNotNull(weBankWealthProductListDTOS);
        assertFalse(weBankWealthProductListDTOS.isEmpty());
    }
}