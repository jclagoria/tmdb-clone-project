package com.api.tmdb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${tmdb.base-url}")
    private String tmdbBaseUrl;

    @Value("${tmdb.access-token}")
    private String accessToken;

    @Bean
    public WebClient tmdbWebClient() {
        return WebClient.builder()
                .baseUrl(tmdbBaseUrl)
                .defaultHeader("content-type", "application/json")
                .defaultHeader("accept", "application/json")
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }
}