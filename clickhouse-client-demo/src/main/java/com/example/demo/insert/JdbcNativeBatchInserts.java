package com.example.demo.insert;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class JdbcNativeBatchInserts {

    public void execute(int total, int batchSize) throws SQLException {

        String jdbcUrl = "jdbc:clickhouse://127.0.0.1:19000/test";
        String user = "root";
        String password = "123456";


        for (int i = 0; i < total / batchSize; i++) {
            Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
            PreparedStatement statement = connection.prepareStatement("insert into t_foo (id, created_at) values (?, ?)");

            for (int j = 0; j < batchSize; j++) {
                statement.setString(1, RandomDataProvider.string());
                statement.setObject(2, RandomDataProvider.localDateTime());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

}
