package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.client.ClickHouseResponse;
import com.example.demo.insert.JdbcBatchInserts;
import com.example.demo.insert.MyBatisBatchInserts;
import com.example.demo.insert.MyBatisPlusBatchInserts;
import com.example.demo.insert.JdbcNativeBatchInserts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

@Slf4j
@SpringBootTest
class ClickhouseDemoApplicationTests {

    @Autowired
    private ClickHouseNode clickHouseNode;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcBatchInserts jdbcBatchInserts;

    @Autowired
    private JdbcNativeBatchInserts jdbcNativeBatchInserts;

    @Autowired
    private MyBatisBatchInserts myBatisBatchInserts;

    @Autowired
    private MyBatisPlusBatchInserts myBatisPlusBatchInserts;

    @Test
    void testClient() {
        try (var client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP)) {

            boolean pong = client.ping(clickHouseNode, 10);
            Assertions.assertTrue(pong);

            try (ClickHouseResponse response = client.read(clickHouseNode)
                    .query("select version()")
                    .executeAndWait()) {

                Assertions.assertNotNull(response.getSummary());

                String chServerVersion = response.firstRecord()
                        .getValue(0)
                        .asString();

                logCHServerVersion(chServerVersion);
                Assertions.assertNotNull(chServerVersion);

            } catch (Exception e) {
                Assertions.fail(e);
            }

        }
    }

    @Test
    void testDataSource() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select version() as ch_server_version");
            while (resultSet.next()) {
                String chServerVersion = resultSet.getString("ch_server_version");
                logCHServerVersion(chServerVersion);

                Assertions.assertNotNull(chServerVersion);
            }

        } catch (SQLException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testJdbcBatchInsert() {
        measureTime("Jdbc Batch Insert - RowBinary Format", 10, () -> {
            try {
                jdbcBatchInserts.execute(100000, 10000);
                long count = jdbcBatchInserts.count();
                Assertions.assertTrue(count >= 100000);
            } catch (SQLException e) {
                Assertions.fail(e);
            }
        });
    }

    @Test
    void testMyBatisBatchInsertValuesFormat() {

        measureTime("MyBatis Batch Insert - Values Format", 3, () -> {
            try {
                myBatisBatchInserts.withValuesFormat(100000, 10000);
                long count = jdbcBatchInserts.count();
                Assertions.assertTrue(count >= 100000);
            } catch (SQLException e) {
                Assertions.fail(e);
            }
        });
    }

    @Test
    void testMyBatisBatchInsertRowBinaryFormat() {

        measureTime("MyBatis Batch Insert - RowBinary Format", 3, () -> {
            try {
                myBatisBatchInserts.withRowBinaryFormat(100000, 10000);
                long count = jdbcBatchInserts.count();
                Assertions.assertTrue(count >= 100000);
            } catch (SQLException e) {
                Assertions.fail(e);
            }
        });
    }

    @Test
    void testMyBatisPlusInsertBatch() {
        measureTime("MyBatis Batch Insert - RowBinary Format", 3, () -> {
            try {
                myBatisPlusBatchInserts.execute(100000, 10000);
                long count = jdbcBatchInserts.count();
                Assertions.assertTrue(count >= 100000);
            } catch (Exception e) {
                Assertions.fail(e);
            }
        });
    }

    @Test
    void testJdbcNativeBatchInsert() {
        measureTime("Native JDBC Batch Insert", 10, () -> {
            try {
                jdbcNativeBatchInserts.execute(100000, 10000);
                long count = jdbcBatchInserts.count();
                Assertions.assertTrue(count >= 100000);
            } catch (Exception e) {
                Assertions.fail(e);
            }
        });
    }

    private static void measureTime(String taskName, int times, Runnable runnable) {
        List<Long> timeSpent = new ArrayList<>();

        for (int i = 0; i < times; i++) {
            Long start = System.currentTimeMillis();
            runnable.run();
            Long end = System.currentTimeMillis();

            timeSpent.add(end - start);
        }

        LongSummaryStatistics stat = timeSpent.stream()
                .mapToLong(Long::longValue)
                .summaryStatistics();

        System.out.printf("[%s] Time spent: min %.2fs, avg %.2fs max %.2fs%n",
                taskName, stat.getMin() / 1000.0, stat.getAverage() / 1000.0, stat.getMax() / 1000.0);
    }

    private void logCHServerVersion(String chServerVersion) {
        log.info("Get ClickHouse Server Version: {}", chServerVersion);
    }

}
