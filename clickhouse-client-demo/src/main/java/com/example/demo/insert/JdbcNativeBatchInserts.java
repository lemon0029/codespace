package com.example.demo.insert;

import com.github.housepower.jdbc.ClickHouseDriver;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class JdbcNativeBatchInserts {

    public void execute(int total, int batchSize) throws SQLException {

        String jdbcUrl = "jdbc:clickhouse://127.0.0.1:19000/test";
        String user = "root";
        String password = "123456";

        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);

        Driver clickHouseDriver = new ClickHouseDriver();

        String sql = "insert into t_foo (id, created_at) values (?, ?)";

        for (int i = 0; i < total / batchSize; i++) {

            try (var connection = clickHouseDriver.connect(jdbcUrl, properties);
                 var statement = connection.prepareStatement(sql)) {

                for (int j = 0; j < batchSize; j++) {
                    statement.setString(1, RandomDataProvider.string());
                    statement.setObject(2, RandomDataProvider.localDateTime());
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        }
    }

}
