package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.domain.exception.TmdbServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public abstract class TmdbBaseAdapter {

    protected static final Logger log = LoggerFactory.getLogger(TmdbBaseAdapter.class);

    protected final WebClient webClient;

    protected TmdbBaseAdapter(@Qualifier("tmdbWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "tmdbApi")
    @Retry(name = "tmdbApi")
    protected <T> Mono<T> executeGet(java.util.function.Function<org.springframework.web.util.UriBuilder, java.net.URI> uriFunction, Function<Object, T> mapper) {
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.debug("TMDB API response received"))
                .doOnError(error -> log.error("TMDB API call failed: {}", error.getMessage(), error))
                .map(mapper);
    }

    protected <T> Mono<T> fallbackMethod(String operation, Throwable cause) {
        log.warn("Circuit breaker fallback triggered for {}. Cause: {}", operation, cause.getMessage());

        String message;
        if (cause instanceof WebClientResponseException ex) {
            message = "TMDB API error: " + ex.getStatusCode();
        } else if (cause instanceof io.github.resilience4j.ratelimiter.RequestNotPermitted) {
            message = "Rate limit exceeded for TMDB API";
        } else {
            message = "Service temporarily unavailable: " + operation;
        }

        return Mono.error(new TmdbServiceException(message, cause));
    }
}