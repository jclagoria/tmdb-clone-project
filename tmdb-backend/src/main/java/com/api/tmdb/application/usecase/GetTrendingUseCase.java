package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.inbound.GetTrendingPort;
import com.api.tmdb.domain.port.outbound.TmdbClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GetTrendingUseCase implements GetTrendingPort {

    private static final Logger log = LoggerFactory.getLogger(GetTrendingUseCase.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbClientPort tmdbClientPort;
    private final CacheService cacheService;

    public GetTrendingUseCase(TmdbClientPort tmdbClientPort, CacheService cacheService) {
        this.tmdbClientPort = tmdbClientPort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String cacheKey = buildCacheKey(timeWindow, effectiveLanguage);

        log.debug("Executing GetTrendingUseCase: timeWindow={}, language={}", timeWindow, effectiveLanguage);

        return cacheService.get(cacheKey, TrendingResponse.class)
                .flatMap(cached -> {
                    log.info("Cache hit for trending: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Cache miss for trending: {}", cacheKey);
                    return tmdbClientPort.getTrending(timeWindow, effectiveLanguage)
                            .flatMap(response -> cacheService.set(cacheKey, response, CACHE_TTL)
                                    .thenReturn(response));
                }))
                .doOnSuccess(response -> log.debug("GetTrendingUseCase completed: page={}, totalResults={}",
                        response.page(), response.totalResults()))
                .doOnError(error -> log.error("GetTrendingUseCase failed: {}", error.getMessage(), error));
    }

    private String buildCacheKey(TimeWindow timeWindow, String language) {
        return "trending:" + timeWindow.getValue() + ":" + language;
    }
}
