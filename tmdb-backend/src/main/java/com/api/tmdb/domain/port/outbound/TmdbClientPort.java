package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import reactor.core.publisher.Mono;

/**
 * @deprecated Use {@link TmdbTrendingPort} instead.
 * 
 * This port is too generic and is being replaced by TmdbTrendingPort
 * which provides a cleaner interface following TMDB API grouping.
 * 
 * Migration: Replace with TmdbTrendingPort
 */
@Deprecated
public interface TmdbClientPort {
    Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language);
}