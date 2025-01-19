package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan
@SpringBootApplication
public class ClickhouseDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseDemoApplication.class, args);
    }

}
