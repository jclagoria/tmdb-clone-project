package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import reactor.core.publisher.Mono;

public interface TmdbTvSeriesListPort {
    
    Mono<TvOnTheAirResponse> getAiringToday(String language, Integer page, String timezone);
    
    Mono<TvOnTheAirResponse> getOnTheAir(String language, Integer page, String timezone);
    
    Mono<WhatsPopularResponse> getPopular(String language, Integer page);
    
    Mono<WhatsPopularResponse> getTopRated(String language, Integer page);
}