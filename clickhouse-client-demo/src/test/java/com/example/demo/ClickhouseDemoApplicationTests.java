package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseFormat;
import com.example.demo.insert.*;
import com.example.demo.select.ClientV1QueryExecutor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Stream;

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

    @Autowired
    private ClientV1BatchInserts clientV1BatchInserts;

    @Autowired
    private ClientV1QueryExecutor clientV1QueryExecutor;

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

    @Test
    void testClientV1BatchInsert() {
        measureTime("Client Batch Insert", 10, () -> {
            try {
                long writtenRows = clientV1BatchInserts.withRowBinaryFormat(100000, 10000);
                Assertions.assertEquals(100000, writtenRows);
                long count = jdbcBatchInserts.count();
                Assertions.assertTrue(count >= 100000);
            } catch (Exception e) {
                Assertions.fail(e);
            }
        });
    }

    @Test
    void testClientV1Query() {
        try {
            int partSize = 10000;

            for (int i = 0; i < 100; i++) {
                byte[] bytes = clientV1QueryExecutor.readPart(ClickHouseFormat.Native, partSize, i * partSize);
                Path path = Path.of("data/native-format/part-%d.bin".formatted(i));
                Files.write(path, bytes, StandardOpenOption.CREATE_NEW);
            }

            for (int i = 0; i < 100; i++) {
                byte[] bytes = clientV1QueryExecutor.readPart(ClickHouseFormat.RowBinary, partSize, i * partSize);
                Path path = Path.of("data/row-binary-format/part-%d.bin".formatted(i));
                Files.write(path, bytes, StandardOpenOption.CREATE_NEW);
            }

        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testClientV1BatchInsertWriteNativeFormatData() {
        Path path = Path.of("data/native-format");

        measureTime("Write Block Data", 1, () -> {
            try (Stream<Path> files = Files.list(path)) {

                files.forEach(it -> {
                    try {
                        long writtenRows = clientV1BatchInserts.writePart(ClickHouseFormat.Native, it);
                        Assertions.assertEquals(10000, writtenRows);
                    } catch (Exception e) {
                        Assertions.fail(e);
                    }
                });

            } catch (IOException ignored) {
            }
        });
    }

    @Test
    void testClientV1BatchInsertWriteRowBinaryFormatData() {
        Path path = Path.of("data/row-binary-format");

        measureTime("Write RowBinary Data", 1, () -> {
            try (Stream<Path> files = Files.list(path)) {

                files.forEach(it -> {
                    try {
                        long writtenRows = clientV1BatchInserts.writePart(ClickHouseFormat.RowBinary, it);
                        Assertions.assertEquals(10000, writtenRows);
                    } catch (Exception e) {
                        Assertions.fail(e);
                    }
                });

            } catch (IOException ignored) {
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
