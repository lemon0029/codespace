package io.nullptr.cmb.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("prod")
class ZZBProductDataSyncTaskTest {

    @Autowired
    private ZZBProductDataSyncTask zzbProductDataSyncTask;

    @Test
    void execute() {
        zzbProductDataSyncTask.execute();
    }
}