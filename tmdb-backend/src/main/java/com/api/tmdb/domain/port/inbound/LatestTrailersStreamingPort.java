package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import reactor.core.publisher.Mono;

@Deprecated
public interface LatestTrailersStreamingPort {
    @Deprecated
    Mono<LatestTrailerResponse> getStreaming(String language, String watchRegion);
}