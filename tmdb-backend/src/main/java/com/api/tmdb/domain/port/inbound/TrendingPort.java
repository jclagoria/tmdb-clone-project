package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import reactor.core.publisher.Mono;

/**
 * Inbound port for trending operations.
 * 
 * This port provides methods to get trending content from TMDB:
 * - Trending movies, TV shows, and people
 * 
 * Based on TMDB's /trending endpoint grouping.
 */
public interface TrendingPort {
    
    /**
     * Get trending items (movies, TV shows, and people).
     * 
     * @param timeWindow Time window (day or week)
     * @param language Language code (e.g., "en-US")
     * @return Mono emitting trending response
     */
    Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language);
}