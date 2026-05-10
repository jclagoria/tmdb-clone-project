package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import reactor.core.publisher.Mono;

/**
 * @deprecated Use {@link TrendingPort#getTrending(TimeWindow, String)} instead.
 * 
 * This port is being replaced by TrendingPort which provides a cleaner
 * interface with AOP-based caching.
 * 
 * Migration:
 * - Replace GetTrendingPort with TrendingPort
 * - Replace GetTrendingUseCase with refactored version
 * - Caching is now handled via @Cacheable annotation instead of manual CacheService
 */
@Deprecated
public interface GetTrendingPort {
    Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language);
}