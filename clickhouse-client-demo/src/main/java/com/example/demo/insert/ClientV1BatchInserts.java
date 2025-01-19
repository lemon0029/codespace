package com.example.demo.insert;

import com.clickhouse.client.*;
import com.clickhouse.data.ClickHouseDataStreamFactory;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.ClickHousePipedOutputStream;
import com.clickhouse.data.format.BinaryStreamUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TimeZone;

@Component
@RequiredArgsConstructor
public class ClientV1BatchInserts {

    private final ClickHouseNode clickHouseNode;

    public long writePart(ClickHouseFormat format, Path path) throws IOException, ClickHouseException {

        try (ClickHouseClient client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP);
             BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path))) {

            ClickHouseRequest.Mutation request = client.write(clickHouseNode)
                    .table("t_foo")
                    .format(format)
                    .data(inputStream);

            try (ClickHouseResponse response = request.executeAndWait()) {
                return response.getSummary().getWrittenRows();
            }
        }
    }

    public long withRowBinaryFormat(int total, int batchSize) throws IOException, ClickHouseException {

        long writtenRows = 0;

        for (int i = 0; i < total / batchSize; i++) {

            try (ClickHouseClient client = ClickHouseClient.newInstance(ClickHouseProtocol.HTTP)) {
                ClickHouseRequest.Mutation request = client.write(clickHouseNode)
                        .table("t_foo")
                        .format(ClickHouseFormat.RowBinary);

                try (ClickHousePipedOutputStream stream = ClickHouseDataStreamFactory.getInstance()
                        .createPipedOutputStream(request.getConfig(), (Runnable) null)) {

                    request.data(stream.getInputStream());

                    for (int j = 0; j < batchSize; j++) {
                        BinaryStreamUtils.writeString(stream, RandomDataProvider.string());
                        BinaryStreamUtils.writeDateTime64(stream, RandomDataProvider.localDateTime(), TimeZone.getDefault());
                    }

                    stream.flush();
                }

                try (ClickHouseResponse response = request.executeAndWait()) {
                    writtenRows += response.getSummary().getWrittenRows();
                }
            }
        }

        return writtenRows;
    }

}
