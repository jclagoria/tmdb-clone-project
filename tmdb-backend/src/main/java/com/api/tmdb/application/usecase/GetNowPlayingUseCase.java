package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.MovieListPort;
import com.api.tmdb.domain.port.outbound.TmdbMovieListPort;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetNowPlayingUseCase implements MovieListPort {

    private static final Logger log = LoggerFactory.getLogger(GetNowPlayingUseCase.class);

    private final TmdbMovieListPort tmdbMovieListPort;

    public GetNowPlayingUseCase(TmdbMovieListPort tmdbMovieListPort) {
        this.tmdbMovieListPort = tmdbMovieListPort;
    }

    @Override
    @Cacheable(
        key = "'inTheaters:' + #language + ':' + #region + ':' + #page",
        type = NowPlayingResponse.class,
        ttlMinutes = 15
    )
    public Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;

        log.debug("Executing GetNowPlayingUseCase: region={}, language={}, page={}",
                effectiveRegion, effectiveLanguage, effectivePage);

        return tmdbMovieListPort.getNowPlaying(effectiveLanguage, effectiveRegion, effectivePage)
                .doOnSuccess(response -> log.debug("GetNowPlayingUseCase completed: totalResults={}", 
                        response.totalResults()));
    }

    @Override
    @Cacheable(
        key = "'movieList:popular:' + #language + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 60
    )
    public Mono<WhatsPopularResponse> getPopular(String language, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        int effectivePage = (page == null || page < 1) ? 1 : page;
        
        return tmdbMovieListPort.getPopular(effectiveLanguage, effectivePage);
    }

    @Override
    @Cacheable(
        key = "'movieList:topRated:' + #language + ':' + #region + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 120
    )
    public Mono<WhatsPopularResponse> getTopRated(String language, String region, Integer page) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        String effectiveRegion = (region == null || region.isBlank()) ? "US" : region;
        int effectivePage = (page == null || page < 1) ? 1 : page;
        
        return tmdbMovieListPort.getTopRated(effectiveLanguage, effectiveRegion, effectivePage);
    }

    @Override
    @Cacheable(
        key = "'movieList:upcoming:' + #language + ':' + #page + ':' + #releaseDateGte + ':' + #releaseDateLte",
        type = WhatsPopularResponse.class,
        ttlMinutes = 60
    )
    public Mono<WhatsPopularResponse> getUpcoming(String language, Integer page, 
            String releaseDateGte, String releaseDateLte) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;
        int effectivePage = (page == null || page < 1) ? 1 : page;
        
        return tmdbMovieListPort.getUpcoming(effectiveLanguage, effectivePage, releaseDateGte, releaseDateLte);
    }
}