package com.api.tmdb.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI merchantTransactionsAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Merchant Transactions API")
                        .description("API to manage merchant payment transactions and receivables")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Development Server"));
    }
}