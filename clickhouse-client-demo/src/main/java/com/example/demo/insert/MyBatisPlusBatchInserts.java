package com.example.demo.insert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MyBatisPlusBatchInserts {

    private final FooMapper fooMapper;

    public void execute(int total, int batchSize) {

        for (int i = 0; i < total / batchSize; i++) {
            List<FooData> items = new ArrayList<>();

            for (int j = 0; j < batchSize; j++) {
                items.add(new FooData(RandomDataProvider.string(), RandomDataProvider.localDateTime()));
            }

            fooMapper.insert(items, batchSize);
        }
    }
}
