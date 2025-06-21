package io.nullptr.cmb.client;

import io.nullptr.cmb.client.dto.response.FundDetailDTO;
import io.nullptr.cmb.client.dto.response.FundNetValueDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EastMoneyMobileApiServiceTest {

    private final EastMoneyMobileApiService eastMoneyMobileApiService = new EastMoneyMobileApiService();

    @Test
    void getFundDetail() {
        FundDetailDTO fundDetail = eastMoneyMobileApiService.getFundDetail("050025");
        Assertions.assertNotNull(fundDetail);
    }

    @Test
    void listFundNetValue() {
        List<FundNetValueDTO> fundNetValueDTOS = eastMoneyMobileApiService.listFundNetValue("050025", "ln");
        Assertions.assertNotNull(fundNetValueDTOS);
    }
}