package com.example.demo.insert;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MyBatisBatchInserts {

    private final FooMapper fooMapper;
    private final SqlSessionFactory sqlSessionFactory;

    public void withValuesFormat(int total, int batchSize) {
        for (int i = 0; i < total / batchSize; i++) {
            List<FooData> items = new ArrayList<>();

            for (int j = 0; j < batchSize; j++) {
                items.add(new FooData(RandomDataProvider.string(), RandomDataProvider.localDateTime()));
            }

            fooMapper.insertBatch(items);
            System.out.println(true);
        }
    }

    public void withRowBinaryFormat(int total, int batchSize) {
        for (int i = 0; i < total / batchSize; i++) {

            try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
                FooMapper mapper = sqlSession.getMapper(FooMapper.class);

                for (int j = 0; j < batchSize; j++) {
                    FooData data = new FooData(RandomDataProvider.string(), RandomDataProvider.localDateTime());

                    mapper.insert(data);
                }

                sqlSession.commit();
            }
        }
    }

}
