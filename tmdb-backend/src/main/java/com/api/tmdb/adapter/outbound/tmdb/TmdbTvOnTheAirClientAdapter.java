package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TvOnTheAirMapper;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.port.outbound.TmdbTvOnTheAirPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

@Deprecated
@Component
public class TmdbTvOnTheAirClientAdapter extends TmdbBaseAdapter implements TmdbTvOnTheAirPort {

    private final TvOnTheAirMapper tvOnTheAirMapper;

    public TmdbTvOnTheAirClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            TvOnTheAirMapper tvOnTheAirMapper) {
        super(webClient);
        this.tvOnTheAirMapper = tvOnTheAirMapper;
    }

    @Override
    public Mono<TvOnTheAirResponse> getTvOnTheAir(String language, Integer page, String timezone) {
        log.debug("Calling TMDB API: /tv/on_the_air?language={}&page={}&timezone={}", language, page, timezone);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/tv/on_the_air")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("timezone", Optional.ofNullable(timezone))
                .build();

        return executeGet(uriConfig, tvOnTheAirMapper::mapToTvOnTheAirResponse);
    }
}