package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.inbound.TvSeriesListPort;
import com.api.tmdb.domain.port.outbound.TmdbTvSeriesListPort;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetTvOnTheAirUseCase implements TvSeriesListPort {

    private static final Logger log = LoggerFactory.getLogger(GetTvOnTheAirUseCase.class);

    private final TmdbTvSeriesListPort tmdbTvSeriesListPort;

    public GetTvOnTheAirUseCase(TmdbTvSeriesListPort tmdbTvSeriesListPort) {
        this.tmdbTvSeriesListPort = tmdbTvSeriesListPort;
    }

    @Override
    @Cacheable(
        key = "'tvOnTheAir:' + #language + ':' + #page + ':' + #timezone",
        type = TvOnTheAirResponse.class,
        ttlMinutes = 30
    )
    public Mono<TvOnTheAirResponse> getOnTheAir(String language, Integer page, String timezone) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        int effectivePage = UseCaseHelpers.normalizePage(page);
        String effectiveTimezone = UseCaseHelpers.normalizeTimezone(timezone);

        log.debug("Executing GetTvOnTheAirUseCase: language={}, page={}, timezone={}",
                effectiveLanguage, effectivePage, effectiveTimezone);

        return tmdbTvSeriesListPort.getOnTheAir(effectiveLanguage, effectivePage, effectiveTimezone)
                .doOnSuccess(response -> log.debug("GetTvOnTheAirUseCase completed: totalResults={}", 
                        response.totalResults()));
    }

    @Override
    @Cacheable(
        key = "'tvAiringToday:' + #language + ':' + #page + ':' + #timezone",
        type = TvOnTheAirResponse.class,
        ttlMinutes = 30
    )
    public Mono<TvOnTheAirResponse> getAiringToday(String language, Integer page, String timezone) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        int effectivePage = UseCaseHelpers.normalizePage(page);
        String effectiveTimezone = UseCaseHelpers.normalizeTimezone(timezone);
        
        return tmdbTvSeriesListPort.getAiringToday(effectiveLanguage, effectivePage, effectiveTimezone);
    }

    @Override
    @Cacheable(
        key = "'tvSeriesList:popular:' + #language + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 60
    )
    public Mono<WhatsPopularResponse> getPopular(String language, Integer page) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        int effectivePage = UseCaseHelpers.normalizePage(page);
        
        return tmdbTvSeriesListPort.getPopular(effectiveLanguage, effectivePage);
    }

    @Override
    @Cacheable(
        key = "'tvSeriesList:topRated:' + #language + ':' + #page",
        type = WhatsPopularResponse.class,
        ttlMinutes = 120
    )
    public Mono<WhatsPopularResponse> getTopRated(String language, Integer page) {
        String effectiveLanguage = UseCaseHelpers.normalizeLanguage(language);
        int effectivePage = UseCaseHelpers.normalizePage(page);
        
        return tmdbTvSeriesListPort.getTopRated(effectiveLanguage, effectivePage);
    }
}