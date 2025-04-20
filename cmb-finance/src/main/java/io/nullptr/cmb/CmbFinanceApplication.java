package io.nullptr.cmb;

import io.nullptr.cmb.service.CmbFinanceMCPToolService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class CmbFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmbFinanceApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(CmbFinanceMCPToolService cmbFinanceMCPToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(cmbFinanceMCPToolService)
                .build();
    }
}
