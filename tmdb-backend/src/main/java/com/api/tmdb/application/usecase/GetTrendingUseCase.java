package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.inbound.TrendingPort;
import com.api.tmdb.domain.port.outbound.TmdbTrendingPort;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Use case for Trending with AOP-based caching.
 * 
 * This use case handles the "Trending" functionality
 * by fetching trending movies, TV shows, and people from TMDB API.
 * 
 * Caching is handled by AOP via @Cacheable annotation - no manual caching code here.
 */
@Service
public class GetTrendingUseCase implements TrendingPort {

    private static final Logger log = LoggerFactory.getLogger(GetTrendingUseCase.class);

    private final TmdbTrendingPort tmdbTrendingPort;

    public GetTrendingUseCase(TmdbTrendingPort tmdbTrendingPort) {
        this.tmdbTrendingPort = tmdbTrendingPort;
    }

    /**
     * Get trending items (movies, TV shows, and people).
     * 
     * Results are cached via AOP for 15 minutes.
     * 
     * @param timeWindow Time window (day or week)
     * @param language Language code (e.g., "en-US")
     * @return Mono emitting trending response
     */
    @Override
    @Cacheable(
        key = "'trending:' + #timeWindow.getValue() + ':' + #language",
        type = TrendingResponse.class,
        ttlMinutes = 15
    )
    public Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language) {
        TimeWindow effectiveTimeWindow = (timeWindow == null) ? TimeWindow.WEEK : timeWindow;
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;

        log.debug("Executing GetTrendingUseCase: timeWindow={}, language={}",
                effectiveTimeWindow.getValue(), effectiveLanguage);

        return tmdbTrendingPort.getTrending(effectiveTimeWindow, effectiveLanguage)
                .doOnSuccess(response -> log.debug("GetTrendingUseCase completed: totalResults={}", 
                        response != null ? response.totalResults() : 0))
                .doOnError(error -> log.error("GetTrendingUseCase failed: {}", error.getMessage(), error));
    }
}