package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import reactor.core.publisher.Mono;

public interface LatestTrailersStreamingPort {
    Mono<LatestTrailerResponse> getStreaming(String language, String watchRegion);
}