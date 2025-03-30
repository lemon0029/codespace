package io.nullptr.ch_client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JSONResult {

    private List<Meta> meta;
    private List<Map<String, Object>> data;
    private Statistics statistics;
    private Integer rows;

    @JsonProperty("rows_before_limit_at_least")
    private Integer rowsBeforeLimitAtLeast;

    @Data
    public static class Meta {
        private String name;
        private String type;
    }

    @Data
    public static class Statistics {
        private Double elapsed;

        @JsonProperty("rows_read")
        private Integer rowsRead;

        @JsonProperty("bytes_read")
        private Integer bytesRead;
    }
}
