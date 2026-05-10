package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.WhatsPopularPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @deprecated Use {@link GetDiscoverUseCase#getForRent(String, String, Integer)} instead.
 */
@Deprecated
@Service
public class GetForRentUseCase implements WhatsPopularPort {

    private static final Logger log = LoggerFactory.getLogger(GetForRentUseCase.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbWhatsPopularClientPort tmdbClientPort;
    private final CacheService cacheService;

    public GetForRentUseCase(TmdbWhatsPopularClientPort tmdbClientPort, CacheService cacheService) {
        this.tmdbClientPort = tmdbClientPort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page) {
        return getForRent(language, region, page);
    }

    @Override
    public Mono<WhatsPopularResponse> getForRent(String language, String region, Integer page) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        String effectiveRegion = UseCaseHelpers.normalizeRegion(region);
        int effectivePage = UseCaseHelpers.normalizePage(page);

        String cacheKey = buildCacheKey(effectiveLanguage, effectiveRegion, effectivePage);

        log.debug("Executing GetForRentUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return cacheService.get(cacheKey, WhatsPopularResponse.class)
                .flatMap(cached -> {
                    log.debug("Cache hit for getForRent: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for getForRent: {}", cacheKey);
                    DiscoverParams params = new DiscoverParams(
                            "popularity.desc",
                            effectiveRegion,
                            "rent",
                            effectivePage,
                            effectiveLanguage,
                            false
                    );

                    return tmdbClientPort.discoverMovies(params)
                            .flatMap(response -> {
                                log.debug("GetForRentUseCase completed: totalResults={}",
                                        response.totalResults());
                                return cacheService.set(cacheKey, response, CACHE_TTL)
                                        .thenReturn(response);
                            })
                            .doOnError(error ->
                                    log.error("GetForRentUseCase failed: {}", error.getMessage(), error));
                }));
    }

    private String buildCacheKey(String language, String region, int page) {
        return "getForRent:" + language + ":" + region + ":" + page;
    }
}
