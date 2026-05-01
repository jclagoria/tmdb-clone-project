package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.FreeToWatchTvPort;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetFreeToWatchTvUseCase implements FreeToWatchTvPort {

    private static final Logger log = LoggerFactory.getLogger(GetFreeToWatchTvUseCase.class);

    private final TmdbWhatsPopularClientPort tmdbClientPort;

    public GetFreeToWatchTvUseCase(TmdbWhatsPopularClientPort tmdbClientPort) {
        this.tmdbClientPort = tmdbClientPort;
    }

    @Override
    public Mono<WhatsPopularResponse> getFreeToWatchTv(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        DiscoverParams params = new DiscoverParams(
                "popularity.desc",
                effectiveRegion,
                "free",
                effectivePage,
                effectiveLanguage,
                false
        );

        log.info("Executing GetFreeToWatchTvUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return tmdbClientPort.discoverTv(params)
                .doOnSuccess(response ->
                        log.info("GetFreeToWatchTvUseCase completed: page={}, totalResults={}",
                                response.page(), response.totalResults()))
                .doOnError(error ->
                        log.error("GetFreeToWatchTvUseCase failed: {}", error.getMessage(), error));
    }
}