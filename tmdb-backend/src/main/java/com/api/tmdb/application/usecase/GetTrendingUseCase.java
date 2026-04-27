package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.inbound.GetTrendingPort;
import com.api.tmdb.domain.port.outbound.TmdbClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GetTrendingUseCase implements GetTrendingPort {

    private static final Logger log = LoggerFactory.getLogger(GetTrendingUseCase.class);
    private final TmdbClientPort tmdbClientPort;

    public GetTrendingUseCase(TmdbClientPort tmdbClientPort) {
        this.tmdbClientPort = tmdbClientPort;
    }

    @Override
    public Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        
        log.debug("Executing GetTrendingUseCase: timeWindow={}, language={}", timeWindow, effectiveLanguage);
        
        return tmdbClientPort.getTrending(timeWindow, effectiveLanguage)
                .doOnSuccess(response -> log.debug("GetTrendingUseCase completed: page={}, totalResults={}", 
                        response.page(), response.totalResults()))
                .doOnError(error -> log.error("GetTrendingUseCase failed: {}", error.getMessage(), error));
    }
}
