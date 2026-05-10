package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import reactor.core.publisher.Mono;

@Deprecated
public interface LatestTrailersPort {
    @Deprecated
    Mono<LatestTrailerResponse> getPopular(String language);
}