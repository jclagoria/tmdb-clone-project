package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.NowPlayingMapper;
import com.api.tmdb.domain.exception.TmdbServiceException;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.port.outbound.TmdbNowPlayingPort;
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
public class TmdbNowPlayingClientAdapter implements TmdbNowPlayingPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbNowPlayingClientAdapter.class);

    private final WebClient webClient;
    private final NowPlayingMapper nowPlayingMapper;

    public TmdbNowPlayingClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            NowPlayingMapper nowPlayingMapper) {
        this.webClient = webClient;
        this.nowPlayingMapper = nowPlayingMapper;
    }

    @Override
    @CircuitBreaker(name = "tmdbApi", fallbackMethod = "getNowPlayingFallback")
    @RateLimiter(name = "tmdbApi")
    @Retry(name = "tmdbApi")
    public Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page) {
        log.info("Calling TMDB API: /movie/now_playing?language={}&page={}&region={}", language, page, region);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/movie/now_playing")
                        .queryParam("language", language)
                        .queryParam("page", page)
                        .queryParamIfPresent("region", region != null && !region.isBlank() ? java.util.Optional.of(region) : java.util.Optional.empty())
                        .build()
                )
                .retrieve()
                .bodyToMono(Object.class)
                .doOnSuccess(response -> log.info("TMDB movie/now_playing response received"))
                .doOnError(error -> log.error("TMDB movie/now_playing call failed: {}", error.getMessage(), error))
                .map(nowPlayingMapper::mapToNowPlayingResponse);
    }

    private Mono<NowPlayingResponse> getNowPlayingFallback(String language, String region, Integer page, Throwable cause) {
        log.warn("Circuit breaker fallback triggered for getNowPlaying. Cause: {}", cause.getMessage());
        return Mono.error(new TmdbServiceException("Failed to fetch now playing movies", cause));
    }
}