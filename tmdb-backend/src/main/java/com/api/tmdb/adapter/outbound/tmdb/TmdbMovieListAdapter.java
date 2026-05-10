package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.adapter.outbound.tmdb.mapper.NowPlayingMapper;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbMovieListPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

@Component
public class TmdbMovieListAdapter extends TmdbBaseAdapter implements TmdbMovieListPort {

    private final NowPlayingMapper nowPlayingMapper;
    private final DiscoverMapper discoverMapper;

    public TmdbMovieListAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            NowPlayingMapper nowPlayingMapper,
            DiscoverMapper discoverMapper) {
        super(webClient);
        this.nowPlayingMapper = nowPlayingMapper;
        this.discoverMapper = discoverMapper;
    }

    @Override
    public Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page) {
        log.debug("Calling TMDB API: /movie/now_playing?language={}&region={}&page={}", 
                language, region, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/movie/now_playing")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("region", Optional.ofNullable(region).filter(r -> !r.isBlank()))
                .build();

        return executeGet(uriConfig, nowPlayingMapper::mapToNowPlayingResponse);
    }

    @Override
    public Mono<WhatsPopularResponse> getPopular(String language, Integer page) {
        log.debug("Calling TMDB API: /movie/popular?language={}&page={}", language, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/movie/popular")
                .queryParam("language", language)
                .queryParam("page", page)
                .build();

        return executeGet(uriConfig, json -> discoverMapper.mapToWhatsPopularResponse(json, "movie"));
    }

    @Override
    public Mono<WhatsPopularResponse> getTopRated(String language, String region, Integer page) {
        log.debug("Calling TMDB API: /movie/top_rated?language={}&region={}&page={}", 
                language, region, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/movie/top_rated")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("region", Optional.ofNullable(region).filter(r -> !r.isBlank()))
                .build();

        return executeGet(uriConfig, json -> discoverMapper.mapToWhatsPopularResponse(json, "movie"));
    }

    @Override
    public Mono<WhatsPopularResponse> getUpcoming(String language, Integer page, 
            String releaseDateGte, String releaseDateLte) {
        log.debug("Calling TMDB API: /movie/upcoming?language={}&page={}", language, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/movie/upcoming")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("release_date.gte", Optional.ofNullable(releaseDateGte).filter(r -> !r.isBlank()))
                .queryParamIfPresent("release_date.lte", Optional.ofNullable(releaseDateLte).filter(r -> !r.isBlank()))
                .build();

        return executeGet(uriConfig, json -> discoverMapper.mapToWhatsPopularResponse(json, "movie"));
    }
}