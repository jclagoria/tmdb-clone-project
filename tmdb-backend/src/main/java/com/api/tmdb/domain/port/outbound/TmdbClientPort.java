package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import reactor.core.publisher.Mono;

public interface TmdbClientPort {
    Mono<TrendingResponse> getTrending(TimeWindow timeWindow, String language);
}
