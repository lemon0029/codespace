package com.example.demo.insert;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FooMapper extends BaseMapper<FooData> {

    void customInsertBatch(@Param("items") List<FooData> items);
}
