package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.client.ClickHouseResponse;
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

@Slf4j
@SpringBootTest
class ClickhouseDemoApplicationTests {

    @Autowired
    private ClickHouseNode clickHouseNode;

    @Autowired
    private DataSource dataSource;

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

    private void logCHServerVersion(String chServerVersion) {
        log.info("Get ClickHouse Server Version: {}", chServerVersion);
    }

}
