package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.NowPlayingMapper;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.port.outbound.TmdbNowPlayingPort;
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
public class TmdbNowPlayingClientAdapter extends TmdbBaseAdapter implements TmdbNowPlayingPort {

    private final NowPlayingMapper nowPlayingMapper;

    public TmdbNowPlayingClientAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            NowPlayingMapper nowPlayingMapper) {
        super(webClient);
        this.nowPlayingMapper = nowPlayingMapper;
    }

    @Override
    public Mono<NowPlayingResponse> getNowPlaying(String language, String region, Integer page) {
        log.debug("Calling TMDB API: /movie/now_playing?language={}&page={}&region={}", language, page, region);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/movie/now_playing")
                .queryParam("language", language)
                .queryParam("page", page)
                .queryParamIfPresent("region", Optional.ofNullable(region).filter(r -> !r.isBlank()))
                .build();

        return executeGet(uriConfig, nowPlayingMapper::mapToNowPlayingResponse);
    }
}