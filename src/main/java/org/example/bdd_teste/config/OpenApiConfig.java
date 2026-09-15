package org.example.bdd_teste.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Educação Continuada Gamificada — API 3QA")
                        .description("Progresso do aluno: 12 cursos válidos (média ≥ 7) → Premium + 3 moedas")
                        .version("1.0"));
    }
}
