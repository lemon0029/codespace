package com.example.demo.config;

import com.clickhouse.client.ClickHouseCredentials;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import com.clickhouse.jdbc.ClickHouseDriver;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ClickHouseClientConfiguration {

    @Bean
    public ClickHouseNode clickHouseServer(ClickHouseClientProperties properties) {
        ClickHouseCredentials credentials = ClickHouseCredentials.fromUserAndPassword(
                properties.getUser(), properties.getPassword());

        return ClickHouseNode.builder()
                .host(properties.getHost())
                .port(ClickHouseProtocol.HTTP, properties.getPort())
                .credentials(credentials)
                .options(Maps.fromProperties(properties.getOptions()))
                .database(properties.getDatabase())
                .build();
    }

    @Bean
    public DataSource clickHouseDataSource(ClickHouseClientProperties properties) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(properties.getUser());
        hikariConfig.setPassword(properties.getPassword());
        hikariConfig.setJdbcUrl(properties.getJdbcUrl());
        hikariConfig.setDriverClassName(ClickHouseDriver.class.getName());

        // ClickHouseClientOptions
        Properties options = properties.getOptions();
        hikariConfig.setDataSourceProperties(options);

        return new HikariDataSource(hikariConfig);
    }

}
