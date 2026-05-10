package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import reactor.core.publisher.Mono;

public interface MoviesPort {
    
    Mono<LatestTrailerResponse> getLatestTrailers(String language);
    
    Mono<LatestTrailerResponse> getStreaming(String language, String watchRegion);
    
    Mono<LatestTrailerResponse> getForRent(String language, String watchRegion);
    
    Mono<LatestTrailerResponse> getInTheaters(String language);
}