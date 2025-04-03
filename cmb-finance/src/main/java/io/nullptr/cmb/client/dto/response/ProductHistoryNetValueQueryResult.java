package io.nullptr.cmb.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.*;

@Data
public class ProductHistoryNetValueQueryResult {

    private List<List<String>> nets;

    @JsonProperty("perforPoints")
    private List<Object> performancePoints;
    private List<Object> bonusPoints;
    private List<Object> bpPoints;

    public Map<String, String> getNetValueMap() {
        if (nets == null || nets.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new LinkedHashMap<>();

        for (List<String> item : nets) {
            if (item.size() == 2) {
                result.put(item.get(0), item.get(1));
            }
        }

        return result;
    }
}
