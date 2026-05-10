package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.NowPlayingResponse;
import reactor.core.publisher.Mono;

@Deprecated
public interface TmdbNowPlayingPort {
    Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page);
}