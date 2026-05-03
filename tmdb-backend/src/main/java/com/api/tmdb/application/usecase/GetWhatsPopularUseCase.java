package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.WhatsPopularPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GetWhatsPopularUseCase implements WhatsPopularPort {

    private static final Logger log = LoggerFactory.getLogger(GetWhatsPopularUseCase.class);
    private static final int MAX_ITEMS = 40;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbWhatsPopularClientPort tmdbClientPort;
    private final CacheService cacheService;

    public GetWhatsPopularUseCase(TmdbWhatsPopularClientPort tmdbClientPort, CacheService cacheService) {
        this.tmdbClientPort = tmdbClientPort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        String cacheKey = buildCacheKey(effectiveLanguage, effectiveRegion, effectivePage);

        log.debug("Executing GetWhatsPopularUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return cacheService.get(cacheKey, WhatsPopularResponse.class)
                .flatMap(cached -> {
                    log.debug("Cache hit for whatsPopular: {}", cacheKey);
                    return Mono.just(cached);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache miss for whatsPopular: {}", cacheKey);
                    return fetchAndCache(effectiveLanguage, effectiveRegion, effectivePage, cacheKey);
                }))
                .doOnError(error -> log.error("GetWhatsPopularUseCase failed: {}", error.getMessage(), error));
    }

    private Mono<WhatsPopularResponse> fetchAndCache(String language, String region, int page, String cacheKey) {
        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                region,
                "flatrate",
                page,
                region,
                false);

        Mono<WhatsPopularResponse> moviesMono = tmdbClientPort.discoverMovies(params);
        Mono<WhatsPopularResponse> tvMono = tmdbClientPort.discoverTv(params);

        return Mono.zip(moviesMono, tvMono)
                .map(tuple -> {
                    List<WhatsPopularItem> allItems = new ArrayList<>();
                    allItems.addAll(tuple.getT1().results());
                    allItems.addAll(tuple.getT2().results());

                    List<WhatsPopularItem> sorted = allItems.stream()
                            .sorted(Comparator.comparingDouble(WhatsPopularItem::popularity).reversed())
                            .limit(MAX_ITEMS)
                            .toList();

                    log.debug("GetWhatsPopularUseCase completed: totalItems={}, returned={}",
                            allItems.size(), sorted.size());

                    return new WhatsPopularResponse(page, sorted, sorted.size());
                })
                .flatMap(response -> cacheService.set(cacheKey, response, CACHE_TTL).thenReturn(response));
    }

    private String buildCacheKey(String language, String region, int page) {
        return "whatsPopular:" + language + ":" + region + ":" + page;
    }

    private String buildCacheKey(String language, String region, String variant, int page) {
        return "whatsPopular:" + variant + ":" + language + ":" + region + ":" + page;
    }

    @Override
    public Mono<WhatsPopularResponse> getForRent(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        String cacheKey = buildCacheKey(effectiveLanguage, effectiveRegion, "rent", effectivePage);

        log.debug("Executing GetWhatsPopularUseCase.getForRent: region={}, language={}, page={}",
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
                                log.debug("GetWhatsPopularUseCase.getForRent completed: totalResults={}",
                                        response.totalResults());
                                return cacheService.set(cacheKey, response, CACHE_TTL)
                                        .thenReturn(response);
                            })
                            .doOnError(error -> log.error("GetWhatsPopularUseCase.getForRent failed: {}",
                                    error.getMessage(), error));
                }));
    }
}
