package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import reactor.core.publisher.Mono;

import java.time.Duration;

public final class UseCaseHelpers {

    private UseCaseHelpers() {}

    public static String normalizeLanguage(String language) {
        return (language == null || language.isBlank()) ? "en-US" : language;
    }

    public static String normalizeRegion(String region) {
        return (region == null || region.isBlank()) ? "US" : region;
    }

    public static int normalizePage(Integer page) {
        return (page == null || page < 1) ? 1 : page;
    }

    public static String normalizeTimezone(String timezone) {
        return (timezone == null || timezone.isBlank()) ? "America/New_York" : timezone;
    }

    public static <T> Mono<T> executeWithCache(
            CacheService cacheService,
            String cacheKey,
            Class<T> clazz,
            Duration ttl,
            Mono<T> apiCall) {
        return cacheService.get(cacheKey, clazz)
                .flatMap(cached -> Mono.just(cached))
                .switchIfEmpty(Mono.defer(() ->
                        apiCall.flatMap(response ->
                                cacheService.set(cacheKey, response, ttl).thenReturn(response))));
    }
}