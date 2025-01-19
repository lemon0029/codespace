package com.example.demo.insert;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FooMapper {

    void insertBatch(@Param("items") List<FooData> items);

    void insert(@Param("data") FooData data);
}
