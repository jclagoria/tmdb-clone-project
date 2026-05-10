package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.NowPlayingResponse;
import reactor.core.publisher.Mono;

@Deprecated
public interface NowPlayingPort {
    Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page);
}