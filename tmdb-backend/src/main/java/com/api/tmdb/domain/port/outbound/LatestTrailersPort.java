package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import reactor.core.publisher.Mono;

public interface LatestTrailersPort {
    Mono<LatestTrailerResponse> getPopular(String language);
}
