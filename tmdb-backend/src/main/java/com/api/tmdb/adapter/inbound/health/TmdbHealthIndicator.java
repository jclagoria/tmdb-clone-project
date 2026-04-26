package com.api.tmdb.adapter.inbound.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TmdbHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;

    public TmdbHealthIndicator(WebClient tmdbWebClient) {
        this.webClient = tmdbWebClient;
    }

    @Override
    public Mono<Health> health() {
        return webClient.get()
                .uri("/configuration")
                .retrieve()
                .toBodilessEntity()
                .map(response -> Health.up()
                        .withDetail("status", "TMDB API reachable")
                        .build())
                .onErrorResume(e -> Mono.just(Health.down()
                        .withDetail("error", e.getMessage())
                        .build()));
    }
}