package com.example.demo.insert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class JdbcBatchInserts {

    private final DataSource dataSource;

    public void execute(int total, int batchSize) throws SQLException {

        for (int i = 0; i < total / batchSize; i++) {

            try (var connection = dataSource.getConnection();
                 var statement = connection.prepareStatement("insert into t_foo(id, created_at) values (?, ?)")) {


                for (int j = 0; j < batchSize; j++) {
                    statement.setString(1, RandomDataProvider.string());
                    statement.setObject(2, RandomDataProvider.localDateTime());

                    statement.addBatch();
                }

                statement.executeBatch();
            }

        }
    }

    public long count() throws SQLException {

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select count() from t_foo");
            resultSet.next();

            return resultSet.getLong(1);
        }
    }
}
