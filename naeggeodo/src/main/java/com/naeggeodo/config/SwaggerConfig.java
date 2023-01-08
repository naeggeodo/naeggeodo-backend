package com.naeggeodo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi chatRoom() {
        return GroupedOpenApi.builder()
                .group("ChatRoom-API")
                .pathsToMatch("/chat-rooms/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("Naeggeodo API Docs")
                        .description("내꺼도 API 문서입니다.")
                        .version("v1.0.0"));
    }
}
