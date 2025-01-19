package com.example.demo.select;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientV1QueryExecutor {

    private final ClickHouseNode clickHouseNode;

    public byte[] readPart(ClickHouseFormat format, int limit, int offset) throws Exception {
        try (ClickHouseClient client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP)) {
            try (ClickHouseResponse response = client.read(clickHouseNode)
                    .query("select id, created_at from t_foo order by id desc limit %d offset %d".formatted(limit, offset))
                    .format(format)
                    .compressServerResponse(false)
                    .executeAndWait()) {
                return response.getInputStream().readAllBytes();
            }
        }
    }

}
