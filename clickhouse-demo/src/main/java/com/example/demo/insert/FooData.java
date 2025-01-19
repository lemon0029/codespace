package com.example.demo.insert;

import java.time.LocalDateTime;

public record FooData(
        String id,
        LocalDateTime createdAt
) {
}
