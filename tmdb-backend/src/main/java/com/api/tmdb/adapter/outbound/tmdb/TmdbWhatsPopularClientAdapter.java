package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.domain.exception.TmdbServiceException;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class TmdbWhatsPopularClientAdapter implements TmdbWhatsPopularClientPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbWhatsPopularClientAdapter.class);

    private final WebClient webClient;
    private final DiscoverMapper discoverMapper;

    public TmdbWhatsPopularClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            DiscoverMapper discoverMapper) {
        this.webClient = webClient;
        this.discoverMapper = discoverMapper;
    }

    @Override
    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "discoverMoviesFallback")
    @RateLimiter(name = "tmdbApi")
    @Retry(name = "tmdbApi")
    public Mono<WhatsPopularResponse> discoverMovies(DiscoverParams params) {
        log.info("Calling TMDB API: /discover/movie?sort_by={}&watch_region={}&with_watch_monetization_types={}&page={}",
                params.sortBy(), params.watchRegion(), params.withWatchMonetizationTypes(), params.page());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/movie")
                        .queryParam("sort_by", params.sortBy())
                        .queryParam("watch_region", params.watchRegion())
                        .queryParam("with_watch_monetization_types", params.withWatchMonetizationTypes())
                        .queryParam("page", params.page())
                        .queryParam("language", params.language())
                        .queryParam("include_adult", params.includeAdult())
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.debug("TMDB discover/movie response received"))
                .doOnError(error -> log.error("TMDB discover/movie call failed: {}", error.getMessage(), error))
                .map(response -> discoverMapper.mapToWhatsPopularResponse(response, "movie"));
    }

    @Override
    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "discoverTvFallback")
    @RateLimiter(name = "tmdbApi")
    @Retry(name = "tmdbApi")
    public Mono<WhatsPopularResponse> discoverTv(DiscoverParams params) {
        log.info("Calling TMDB API: /discover/tv?sort_by={}&watch_region={}&with_watch_monetization_types={}&page={}",
                params.sortBy(), params.watchRegion(), params.withWatchMonetizationTypes(), params.page());
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discover/tv")
                        .queryParam("sort_by", params.sortBy())
                        .queryParam("watch_region", params.watchRegion())
                        .queryParam("with_watch_monetization_types", params.withWatchMonetizationTypes())
                        .queryParam("page", params.page())
                        .queryParam("language", params.language())
                        .queryParam("include_adult", params.includeAdult())
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.debug("TMDB discover/tv response received"))
                .doOnError(error -> log.error("TMDB discover/tv call failed: {}", error.getMessage(), error))
                .map(response -> discoverMapper.mapToWhatsPopularResponse(response, "tv"));
    }

    private Mono<WhatsPopularResponse> discoverMoviesFallback(DiscoverParams params, Throwable cause) {
        log.warn("Circuit breaker fallback triggered for discoverMovies. Cause: {}", cause.getMessage());
        return Mono.error(new TmdbServiceException("Failed to fetch popular movies", cause));
    }

    private Mono<WhatsPopularResponse> discoverTvFallback(DiscoverParams params, Throwable cause) {
        log.warn("Circuit breaker fallback triggered for discoverTv. Cause: {}", cause.getMessage());
        return Mono.error(new TmdbServiceException("Failed to fetch popular TV shows", cause));
    }
}
