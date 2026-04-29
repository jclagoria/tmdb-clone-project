package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import reactor.core.publisher.Mono;

public interface TmdbWhatsPopularClientPort {
    Mono<WhatsPopularResponse> discoverMovies(DiscoverParams params);
    Mono<WhatsPopularResponse> discoverTv(DiscoverParams params);
}
