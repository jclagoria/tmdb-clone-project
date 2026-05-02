package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

@Component
@Primary
public class TmdbDiscoverClientAdapter extends TmdbBaseAdapter implements TmdbWhatsPopularClientPort {

    private final DiscoverMapper discoverMapper;

    public TmdbDiscoverClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            DiscoverMapper discoverMapper) {
        super(webClient);
        this.discoverMapper = discoverMapper;
    }

    @Override
    public Mono<WhatsPopularResponse> discoverMovies(DiscoverParams params) {
        log.debug("Calling TMDB API: /discover/movie?sort_by={}&watch_region={}&with_watch_monetization_types={}&page={}",
                params.sortBy(), params.watchRegion(), params.withWatchMonetizationTypes(), params.page());

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/discover/movie")
                .queryParam("sort_by", params.sortBy())
                .queryParam("watch_region", params.watchRegion())
                .queryParam("with_watch_monetization_types", params.withWatchMonetizationTypes())
                .queryParam("page", params.page())
                .queryParam("language", params.language())
                .queryParam("include_adult", params.includeAdult())
                .build();

        return executeGet(uriConfig, response -> discoverMapper.mapToWhatsPopularResponse(response, "movie"));
    }

    @Override
    public Mono<WhatsPopularResponse> discoverTv(DiscoverParams params) {
        log.debug("Calling TMDB API: /discover/tv?sort_by={}&watch_region={}&with_watch_monetization_types={}&page={}",
                params.sortBy(), params.watchRegion(), params.withWatchMonetizationTypes(), params.page());

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/discover/tv")
                .queryParam("sort_by", params.sortBy())
                .queryParam("watch_region", params.watchRegion())
                .queryParam("with_watch_monetization_types", params.withWatchMonetizationTypes())
                .queryParam("page", params.page())
                .queryParam("language", params.language())
                .queryParam("include_adult", params.includeAdult())
                .build();

        return executeGet(uriConfig, response -> discoverMapper.mapToWhatsPopularResponse(response, "tv"));
    }
}