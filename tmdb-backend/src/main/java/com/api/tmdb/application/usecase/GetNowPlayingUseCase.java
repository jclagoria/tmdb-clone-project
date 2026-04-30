package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.port.inbound.NowPlayingPort;
import com.api.tmdb.domain.port.outbound.TmdbNowPlayingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetNowPlayingUseCase implements NowPlayingPort {

    private static final Logger log = LoggerFactory.getLogger(GetNowPlayingUseCase.class);

    private final TmdbNowPlayingPort tmdbNowPlayingPort;

    public GetNowPlayingUseCase(TmdbNowPlayingPort tmdbNowPlayingPort) {
        this.tmdbNowPlayingPort = tmdbNowPlayingPort;
    }

    @Override
    public Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? null : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        log.info("Executing GetNowPlayingUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return tmdbNowPlayingPort.getNowPlaying(effectiveLanguage, effectiveRegion, effectivePage)
                .doOnSuccess(response -> log.info("GetNowPlayingUseCase completed: totalResults={}", response.totalResults()))
                .doOnError(error -> log.error("GetNowPlayingUseCase failed: {}", error.getMessage(), error));
    }
}