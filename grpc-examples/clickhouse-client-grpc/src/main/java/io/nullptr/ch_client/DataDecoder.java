package io.nullptr.ch_client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import lombok.SneakyThrows;

public class DataDecoder {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static Object decode(ByteString byteString, DataFormat format) {
        byte[] bytes = byteString.toByteArray();

        if (format == DataFormat.JSON) {
            return objectMapper.readValue(bytes, JSONResult.class);
        }

        return new String(bytes);
    }
}
