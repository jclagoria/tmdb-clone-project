package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.WhatsPopularResponse;
import reactor.core.publisher.Mono;

@Deprecated
public interface FreeToWatchTvPort {
    @Deprecated
    Mono<WhatsPopularResponse> getFreeToWatchTv(String language, String region, Integer page);
}