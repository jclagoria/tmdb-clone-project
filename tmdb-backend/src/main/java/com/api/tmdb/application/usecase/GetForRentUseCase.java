package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.WhatsPopularPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetForRentUseCase implements WhatsPopularPort {

    private static final Logger log = LoggerFactory.getLogger(GetForRentUseCase.class);

    private final TmdbWhatsPopularClientPort tmdbClientPort;

    public GetForRentUseCase(TmdbWhatsPopularClientPort tmdbClientPort) {
        this.tmdbClientPort = tmdbClientPort;
    }

    @Override
    public Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page) {
        return null;
    }

    @Override
    public Mono<WhatsPopularResponse> getForRent(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "rent",
                effectivePage,
                effectiveRegion,
                false
        );

        log.info("Executing GetForRentUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return tmdbClientPort.discoverMovies(params)
                .doOnSuccess(response ->
                        log.info("GetForRentUseCase completed: page={}, totalResults={}",
                                response.page(), response.totalResults()))
                .doOnError(error ->
                        log.error("GetForRentUseCase failed: {}", error.getMessage(), error));
    }
}
