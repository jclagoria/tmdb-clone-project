package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.port.inbound.TvOnTheAirPort;
import com.api.tmdb.domain.port.outbound.TmdbTvOnTheAirPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class GetTvOnTheAirUseCase implements TvOnTheAirPort {

    private static final Logger log = LoggerFactory.getLogger(GetTvOnTheAirUseCase.class);
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbTvOnTheAirPort tmdbTvOnTheAirPort;
    private final CacheService cacheService;

    public GetTvOnTheAirUseCase(TmdbTvOnTheAirPort tmdbTvOnTheAirPort, CacheService cacheService) {
        this.tmdbTvOnTheAirPort = tmdbTvOnTheAirPort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<TvOnTheAirResponse> getTvOnTheAir(String language, Integer page, String timezone) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        int effectivePage = UseCaseHelpers.normalizePage(page);
        String effectiveTimezone = UseCaseHelpers.normalizeTimezone(timezone);

        String cacheKey = buildCacheKey(effectiveLanguage, effectivePage, effectiveTimezone);

        log.debug("Executing GetTvOnTheAirUseCase: language={}, page={}, timezone={}",
                effectiveLanguage, effectivePage, effectiveTimezone);

        return cacheService.get(cacheKey, TvOnTheAirResponse.class)
                .flatMap(cached -> {
                    log.debug("Cache hit for getTvOnTheAir: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for getTvOnTheAir: {}", cacheKey);
                    return tmdbTvOnTheAirPort.getTvOnTheAir(effectiveLanguage, effectivePage, effectiveTimezone)
                            .flatMap(response -> {
                                log.debug("GetTvOnTheAirUseCase completed: totalResults={}",
                                        response.totalResults());
                                return cacheService.set(cacheKey, response, CACHE_TTL)
                                        .thenReturn(response);
                            })
                            .doOnError(error ->
                                    log.error("GetTvOnTheAirUseCase failed: {}", error.getMessage(), error));
                }));
    }

    private String buildCacheKey(String language, int page, String timezone) {
        return "getTvOnTheAir:" + language + ":" + page + ":" + timezone;
    }
}
