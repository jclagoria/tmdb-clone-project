package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TvOnTheAirMapper;
import com.api.tmdb.domain.exception.TmdbServiceException;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.port.outbound.TmdbTvOnTheAirPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class TmdbTvOnTheAirClientAdapter implements TmdbTvOnTheAirPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbTvOnTheAirClientAdapter.class);

    private final WebClient webClient;
    private final TvOnTheAirMapper tvOnTheAirMapper;

    public TmdbTvOnTheAirClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            TvOnTheAirMapper tvOnTheAirMapper) {
        this.webClient = webClient;
        this.tvOnTheAirMapper = tvOnTheAirMapper;
    }

    @Override
    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "tvOnTheAirFallback")
    @RateLimiter(name = "tmdbApi")
    @Retry(name = "tmdbApi")
    public Mono<TvOnTheAirResponse> getTvOnTheAir(String language, Integer page, String timezone) {
        log.info("Calling TMDB API: /tv/on_the_air?language={}&page={}&timezone={}", language, page, timezone);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tv/on_the_air")
                        .queryParam("language", language)
                        .queryParam("page", page)
                        .queryParamIfPresent("timezone", Optional.ofNullable(timezone))
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.debug("TMDB /tv/on_the_air response received"))
                .doOnError(error -> log.error("TMDB /tv/on_the_air call failed: {}", error.getMessage(), error))
                .map(tvOnTheAirMapper::mapToTvOnTheAirResponse);
    }

    private Mono<TvOnTheAirResponse> tvOnTheAirFallback(String language, Integer page, String timezone, Throwable cause) {
        log.warn("Circuit breaker fallback triggered for getTvOnTheAir. Cause: {}", cause.getMessage());
        return Mono.error(new TmdbServiceException("Failed to fetch on-the-air TV shows", cause));
    }
}
