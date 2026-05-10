package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.adapter.outbound.tmdb.mapper.TvOnTheAirMapper;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbTvSeriesListPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

@Component
public class TmdbTvSeriesListAdapter extends TmdbBaseAdapter implements TmdbTvSeriesListPort {

    private final TvOnTheAirMapper tvOnTheAirMapper;
    private final DiscoverMapper discoverMapper;

    public TmdbTvSeriesListAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            TvOnTheAirMapper tvOnTheAirMapper,
            DiscoverMapper discoverMapper) {
        super(webClient);
        this.tvOnTheAirMapper = tvOnTheAirMapper;
        this.discoverMapper = discoverMapper;
    }

    @Override
    public Mono<TvOnTheAirResponse> getAiringToday(String language, Integer page, String timezone) {
        log.debug("Calling TMDB API: /tv/airing_today?language={}&page={}&timezone={}", 
                language, page, timezone);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/tv/airing_today")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("timezone", Optional.ofNullable(timezone).filter(t -> !t.isBlank()))
                .build();

        return executeGet(uriConfig, tvOnTheAirMapper::mapToTvOnTheAirResponse);
    }

    @Override
    public Mono<TvOnTheAirResponse> getOnTheAir(String language, Integer page, String timezone) {
        log.debug("Calling TMDB API: /tv/on_the_air?language={}&page={}&timezone={}", 
                language, page, timezone);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/tv/on_the_air")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("timezone", Optional.ofNullable(timezone).filter(t -> !t.isBlank()))
                .build();

        return executeGet(uriConfig, tvOnTheAirMapper::mapToTvOnTheAirResponse);
    }

    @Override
    public Mono<WhatsPopularResponse> getPopular(String language, Integer page) {
        log.debug("Calling TMDB API: /tv/popular?language={}&page={}", language, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/tv/popular")
                .queryParam("language", language)
                .queryParam("page", page)
                .build();

        return executeGet(uriConfig, json -> discoverMapper.mapToWhatsPopularResponse(json, "tv"));
    }

    @Override
    public Mono<WhatsPopularResponse> getTopRated(String language, Integer page) {
        log.debug("Calling TMDB API: /tv/top_rated?language={}&page={}", language, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/tv/top_rated")
                .queryParam("language", language)
                .queryParam("page", page)
                .build();

        return executeGet(uriConfig, json -> discoverMapper.mapToWhatsPopularResponse(json, "tv"));
    }
}