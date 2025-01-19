package com.example.demo.config;

import com.clickhouse.client.config.ClickHouseClientOption;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "ch")
public class ClickHouseClientProperties {

    private String host;
    private Integer port;
    private String database;
    private String user;
    private String password;

    private Integer socketTimeout;
    private String customSettings;

    public String getJdbcUrl() {
        return "jdbc:ch://%s:%d/%s".formatted(host, port, database);
    }

    public Properties getOptions() {
        Properties properties = new Properties();

        if (StringUtils.hasText(customSettings)) {
            properties.setProperty(ClickHouseClientOption.CUSTOM_SETTINGS.getKey(), customSettings);
        }

        if (socketTimeout != null) {
            properties.setProperty(ClickHouseClientOption.SOCKET_TIMEOUT.getKey(), socketTimeout.toString());
        }

        return properties;
    }
}
