package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.TvOnTheAirResponse;
import reactor.core.publisher.Mono;

public interface TvOnTheAirPort {
    Mono<TvOnTheAirResponse> getTvOnTheAir(String language, Integer page, String timezone);
}
