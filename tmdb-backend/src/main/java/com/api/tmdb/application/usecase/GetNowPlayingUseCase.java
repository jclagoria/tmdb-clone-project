package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.port.inbound.NowPlayingPort;
import com.api.tmdb.domain.port.outbound.TmdbNowPlayingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GetNowPlayingUseCase implements NowPlayingPort {

    private static final Logger log = LoggerFactory.getLogger(GetNowPlayingUseCase.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(15);

    private final TmdbNowPlayingPort tmdbNowPlayingPort;
    private final CacheService cacheService;

    public GetNowPlayingUseCase(TmdbNowPlayingPort tmdbNowPlayingPort, CacheService cacheService) {
        this.tmdbNowPlayingPort = tmdbNowPlayingPort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        String cacheKey = buildCacheKey(effectiveLanguage, effectiveRegion, effectivePage);

        log.info("Executing GetNowPlayingUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return cacheService.get(cacheKey, NowPlayingResponse.class)
                .flatMap(cached -> {
                    log.info("Cache hit for nowPlaying: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Cache miss for nowPlaying: {}", cacheKey);
                    return tmdbNowPlayingPort.getNowPlaying(effectiveLanguage, effectiveRegion, effectivePage)
                            .flatMap(response -> cacheService.set(cacheKey, response, CACHE_TTL)
                                    .thenReturn(response));
                }))
                .doOnSuccess(response -> log.info("GetNowPlayingUseCase completed: totalResults={}", response.totalResults()))
                .doOnError(error -> log.error("GetNowPlayingUseCase failed: {}", error.getMessage(), error));
    }

    private String buildCacheKey(String language, String region, int page) {
        return "nowPlaying:" + language + ":" + region + ":" + page;
    }
}