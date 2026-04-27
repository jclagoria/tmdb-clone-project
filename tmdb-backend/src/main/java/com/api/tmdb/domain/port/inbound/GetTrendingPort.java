package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import reactor.core.publisher.Mono;

public interface GetTrendingPort {
    Mono<TrendingResponse>  getTrending(TimeWindow timeWindow, String language);
}
