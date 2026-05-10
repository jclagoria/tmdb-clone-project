package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.TvOnTheAirResponse;
import reactor.core.publisher.Mono;

@Deprecated
public interface TmdbTvOnTheAirPort {
    Mono<TvOnTheAirResponse> getTvOnTheAir(String language, Integer page, String timezone);
}