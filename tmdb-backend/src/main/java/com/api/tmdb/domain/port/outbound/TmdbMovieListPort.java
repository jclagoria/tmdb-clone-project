package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import reactor.core.publisher.Mono;

public interface TmdbMovieListPort {
    
    Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page);
    
    Mono<WhatsPopularResponse> getPopular(String language, Integer page);
    
    Mono<WhatsPopularResponse> getTopRated(String language, String region, Integer page);
    
    Mono<WhatsPopularResponse> getUpcoming(String language, Integer page, 
            String releaseDateGte, String releaseDateLte);
}