package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import reactor.core.publisher.Mono;

public interface DiscoverPort {
    
    Mono<WhatsPopularResponse> getWhatsPopular(String language, String region, Integer page);
    
    Mono<WhatsPopularResponse> getForRent(String language, String region, Integer page);
    
    Mono<WhatsPopularResponse> discoverMovies(DiscoverParams params);
    
    Mono<WhatsPopularResponse> discoverTv(DiscoverParams params);
}