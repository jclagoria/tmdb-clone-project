package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import reactor.core.publisher.Mono;

/**
 * Outbound port for TMDB Trending API operations.
 * 
 * This port defines the contract for interacting with TMDB's
 * /trending endpoints: /trending/all/{time_window}
 * 
 * Implementations should handle:
 * - HTTP communication with TMDB API
 * - Response mapping to domain models
 * - Error handling and retries
 */
public interface TmdbTrendingPort {
    
    /**
     * Get trending items from TMDB API.
     * 
     * @param timeWindow Time window (day or week)
     * @param language Language code
     * @return Mono emitting TMDB trending response
     */
    Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language);
}