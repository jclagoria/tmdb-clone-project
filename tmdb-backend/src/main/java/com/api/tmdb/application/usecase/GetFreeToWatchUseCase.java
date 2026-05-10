package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.FreeToWatchPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Deprecated
@Service
public class GetFreeToWatchUseCase implements FreeToWatchPort {

    private static final Logger log = LoggerFactory.getLogger(GetFreeToWatchUseCase.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbWhatsPopularClientPort tmdbClientPort;
    private final CacheService cacheService;

    @Deprecated
    public GetFreeToWatchUseCase(TmdbWhatsPopularClientPort tmdbClientPort, CacheService cacheService) {
        this.tmdbClientPort = tmdbClientPort;
        this.cacheService = cacheService;
    }

    @Override
    @Deprecated
    public Mono<WhatsPopularResponse> getFreeToWatch(String language, String region, Integer page) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        String effectiveRegion = UseCaseHelpers.normalizeRegion(region);
        int effectivePage = UseCaseHelpers.normalizePage(page);

        String cacheKey = buildCacheKey(effectiveLanguage, effectiveRegion, effectivePage);

        log.debug("Executing GetFreeToWatchUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return cacheService.get(cacheKey, WhatsPopularResponse.class)
                .flatMap(cached -> {
                    log.debug("Cache hit for getFreeToWatch: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for getFreeToWatch: {}", cacheKey);
                    DiscoverParams params = new DiscoverParams(
                            "popularity.desc",
                            effectiveRegion,
                            "free",
                            effectivePage,
                            effectiveLanguage,
                            false
                    );

                    return tmdbClientPort.discoverMovies(params)
                            .flatMap(response -> {
                                log.debug("GetFreeToWatchUseCase completed: totalResults={}",
                                        response.totalResults());
                                return cacheService.set(cacheKey, response, CACHE_TTL)
                                        .thenReturn(response);
                            })
                            .doOnError(error ->
                                    log.error("GetFreeToWatchUseCase failed: {}", error.getMessage(), error));
                }));
    }

    @Deprecated
    private String buildCacheKey(String language, String region, int page) {
        return "getFreeToWatch:" + language + ":" + region + ":" + page;
    }
}