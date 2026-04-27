package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TrendingMapper;
import com.api.tmdb.domain.exception.TmdbServiceException;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.outbound.TmdbClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class TmdbClientAdapter implements TmdbClientPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbClientAdapter.class);
    
    private final WebClient webClient;
    private final TrendingMapper trendingMapper;

    public TmdbClientAdapter(WebClient webClient, TrendingMapper trendingMapper) {
        this.webClient = webClient;
        this.trendingMapper = trendingMapper;
    }

    @Override
    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "getTrendingFallback")
    @RateLimiter(name = "tmdbApi")
    @Retry(name = "tmdbApi")
    public Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language) {
        String tmdbTimeWindow = timeWindow.getValue();
        
        log.debug("Calling TMDB API: /trending/all/{}?language={}", tmdbTimeWindow, language);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/trending/all/{timeWindow}")
                        .queryParam("language", language)
                        .build(tmdbTimeWindow))
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.debug("TMDB API response received"))
                .doOnError(error -> log.error("TMDB API call failed: {}", error.getMessage(), error))
                .map(trendingMapper::mapToTrendingResponse);
    }

    private Mono<TrendingResponse> getTrendingFallback(TimeWindow timeWindow, String language, Throwable cause) {
        log.warn("Circuit breaker fallback triggered for getTrending. TimeWindow: {}, Language: {}, Cause: {}", 
                timeWindow, language, cause.getMessage());
        
        String message;
        if (cause instanceof WebClientResponseException ex) {
            message = "TMDB API error: " + ex.getStatusCode();
        } else if (cause instanceof io.github.resilience4j.ratelimiter.RequestNotPermitted) {
            message = "Rate limit exceeded for TMDB API";
        } else {
            message = "Service temporarily unavailable";
        }
        
        return Mono.error(new TmdbServiceException(message, cause));
    }
}