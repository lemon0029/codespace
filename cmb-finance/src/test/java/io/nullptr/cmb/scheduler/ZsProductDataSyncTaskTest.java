package io.nullptr.cmb.scheduler;

import io.nullptr.cmb.appliation.scheduler.ZsProductDataSyncTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("prod")
class ZsProductDataSyncTaskTest {

    @Autowired
    private ZsProductDataSyncTask zsProductDataSyncTask;

    @Test
    void updateDataForZZB() throws InterruptedException {
        zsProductDataSyncTask.updateDataForZZB();
    }

    @Test
    void updateDataForYYB() throws InterruptedException {
        zsProductDataSyncTask.updateDataForYYB();
    }

    @Test
    void updateDataForDQB() throws InterruptedException {
        zsProductDataSyncTask.updateDataForDQB();
    }
}