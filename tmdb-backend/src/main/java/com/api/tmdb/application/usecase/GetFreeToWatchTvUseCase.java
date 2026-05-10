package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.FreeToWatchTvPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Deprecated
@Service
public class GetFreeToWatchTvUseCase implements FreeToWatchTvPort {

    private static final Logger log = LoggerFactory.getLogger(GetFreeToWatchTvUseCase.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbWhatsPopularClientPort tmdbClientPort;
    private final CacheService cacheService;

    @Deprecated
    public GetFreeToWatchTvUseCase(TmdbWhatsPopularClientPort tmdbClientPort, CacheService cacheService) {
        this.tmdbClientPort = tmdbClientPort;
        this.cacheService = cacheService;
    }

    @Override
    @Deprecated
    public Mono<WhatsPopularResponse> getFreeToWatchTv(String language, String region, Integer page) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        String effectiveRegion = UseCaseHelpers.normalizeRegion(region);
        int effectivePage = UseCaseHelpers.normalizePage(page);

        String cacheKey = buildCacheKey(effectiveLanguage, effectiveRegion, effectivePage);

        log.debug("Executing GetFreeToWatchTvUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return cacheService.get(cacheKey, WhatsPopularResponse.class)
                .flatMap(cached -> {
                    log.debug("Cache hit for getFreeToWatchTv: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for getFreeToWatchTv: {}", cacheKey);
                    DiscoverParams params = new DiscoverParams(
                            "popularity.desc",
                            effectiveRegion,
                            "free",
                            effectivePage,
                            effectiveLanguage,
                            false
                    );

                    return tmdbClientPort.discoverTv(params)
                            .flatMap(response -> {
                                log.debug("GetFreeToWatchTvUseCase completed: totalResults={}",
                                        response.totalResults());
                                return cacheService.set(cacheKey, response, CACHE_TTL)
                                        .thenReturn(response);
                            })
                            .doOnError(error ->
                                    log.error("GetFreeToWatchTvUseCase failed: {}", error.getMessage(), error));
                }));
    }

    @Deprecated
    private String buildCacheKey(String language, String region, int page) {
        return "getFreeToWatchTv:" + language + ":" + region + ":" + page;
    }
}