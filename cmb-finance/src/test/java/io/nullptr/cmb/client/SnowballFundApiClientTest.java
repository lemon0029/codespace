package io.nullptr.cmb.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SnowballFundApiClientTest {

    @Autowired
    private SnowballFundApiClient snowballFundApiClient;

    @Test
    void queryFundInfo() {
        snowballFundApiClient.queryFundInfo("050025");
    }
}