package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.response.FundDetailDTO;
import io.nullptr.cmb.client.dto.response.FundNetValueDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class EastMoneyApiServiceTest {

    private final EastMoneyApiService eastMoneyApiService = new EastMoneyApiService();

    @Test
    void getFundDetail() {
        FundDetailDTO fundDetail = eastMoneyApiService.getFundDetail("015300");
        Assertions.assertNotNull(fundDetail);
    }

    @Test
    void listFundNetValue() {
        List<FundNetValueDTO> fundNetValueDTOS = eastMoneyApiService.listFundNetValue("050025", "ln");
        Assertions.assertNotNull(fundNetValueDTOS);
    }
}