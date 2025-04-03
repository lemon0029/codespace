package io.nullptr.cmb;

import io.nullptr.cmb.service.CmbFinanceService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CmbFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmbFinanceApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(CmbFinanceService cmbFinanceService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(cmbFinanceService)
                .build();
    }
}
