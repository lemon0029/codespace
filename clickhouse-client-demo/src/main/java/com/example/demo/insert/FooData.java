package com.example.demo.insert;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("t_foo")
public record FooData(
        String id,
        LocalDateTime createdAt
) {
}
