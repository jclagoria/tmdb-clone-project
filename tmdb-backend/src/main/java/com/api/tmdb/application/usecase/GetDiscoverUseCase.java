package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.DiscoverPort;
import com.api.tmdb.domain.port.outbound.TmdbDiscoverPort;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GetDiscoverUseCase implements DiscoverPort {

    private static final Logger log = LoggerFactory.getLogger(GetDiscoverUseCase.class);
    private static final int MAX_ITEMS = 40;

    private final TmdbDiscoverPort tmdbDiscoverPort;

    public GetDiscoverUseCase(TmdbDiscoverPort tmdbDiscoverPort) {
        this.tmdbDiscoverPort = tmdbDiscoverPort;
    }

    @Override
    @Cacheable(
        key = "'whatsPopular:streaming:' + #language + ':' + #region + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 30
    )
    public Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        log.debug("Executing GetDiscoverUseCase.getWhatsPopular: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "flatrate",
                effectivePage,
                effectiveLanguage,
                false
        );

        return fetchAndCombine(effectivePage, params);
    }

    @Override
    @Cacheable(
        key = "'whatsPopular:rent:' + #language + ':' + #region + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 30
    )
    public Mono<WhatsPopularResponse> getForRent(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        log.debug("Executing GetDiscoverUseCase.getForRent: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "rent",
                effectivePage,
                effectiveLanguage,
                false
        );

        return tmdbDiscoverPort.discoverMovies(params)
                .doOnNext(response -> log.debug("getForRent completed: totalResults={}", response.totalResults()));
    }

    @Override
    public Mono<WhatsPopularResponse> discoverMovies(DiscoverParams params) {
        return tmdbDiscoverPort.discoverMovies(params);
    }

    @Override
    public Mono<WhatsPopularResponse> discoverTv(DiscoverParams params) {
        return tmdbDiscoverPort.discoverTv(params);
    }

    @Cacheable(
        key = "'freeMovies:' + #language + ':' + #region + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 60
    )
    public Mono<WhatsPopularResponse> getFreeMovies(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        log.debug("Executing GetDiscoverUseCase.getFreeMovies: language={}, region={}, page={}",
                effectiveLanguage, effectiveRegion, effectivePage);

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "free",
                effectivePage,
                effectiveLanguage,
                false
        );

        return tmdbDiscoverPort.discoverMovies(params)
                .doOnSuccess(response -> log.debug("getFreeMovies completed: totalResults={}",
                        response != null ? response.totalResults() : 0))
                .doOnError(error -> log.error("getFreeMovies failed: {}", error.getMessage(), error));
    }

    @Cacheable(
        key = "'freeTvShows:' + #language + ':' + #region + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 60
    )
    public Mono<WhatsPopularResponse> getFreeTvShows(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        log.debug("Executing GetDiscoverUseCase.getFreeTvShows: language={}, region={}, page={}",
                effectiveLanguage, effectiveRegion, effectivePage);

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "free",
                effectivePage,
                effectiveLanguage,
                false
        );

        return tmdbDiscoverPort.discoverTv(params)
                .doOnSuccess(response -> log.debug("getFreeTvShows completed: totalResults={}",
                        response != null ? response.totalResults() : 0))
                .doOnError(error -> log.error("getFreeTvShows failed: {}", error.getMessage(), error));
    }

    private Mono<WhatsPopularResponse> fetchAndCombine(int page, DiscoverParams params) {
        Mono<WhatsPopularResponse> moviesMono = tmdbDiscoverPort.discoverMovies(params);
        Mono<WhatsPopularResponse> tvMono = tmdbDiscoverPort.discoverTv(params);

        return Mono.zip(moviesMono, tvMono)
                .map(tuple -> {
                    List<WhatsPopularItem> allItems = new ArrayList<>();
                    allItems.addAll(tuple.getT1().results());
                    allItems.addAll(tuple.getT2().results());

                    List<WhatsPopularItem> sorted = allItems.stream()
                            .sorted(Comparator.comparingDouble(WhatsPopularItem::popularity).reversed())
                            .limit(MAX_ITEMS)
                            .toList();

                    log.debug("GetDiscoverUseCase completed: totalItems={}, returned={}",
                            allItems.size(), sorted.size());

                    return new WhatsPopularResponse(page, sorted, sorted.size());
                });
    }
}