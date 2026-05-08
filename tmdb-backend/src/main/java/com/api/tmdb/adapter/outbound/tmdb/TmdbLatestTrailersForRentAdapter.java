package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.LatestTrailerMapper;
import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.outbound.LatestTrailersForRentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Component
public class TmdbLatestTrailersForRentAdapter extends TmdbBaseAdapter implements LatestTrailersForRentPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbLatestTrailersForRentAdapter.class);
    private final LatestTrailerMapper latestTrailerMapper;

    public TmdbLatestTrailersForRentAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            LatestTrailerMapper latestTrailerMapper) {
        super(webClient);
        this.latestTrailerMapper = latestTrailerMapper;
    }

    @Override
    public Mono<LatestTrailerResponse> getForRent(String language, String watchRegion) {
        log.debug("Calling TMDB API: /discover/movie?with_watch_monetization_types=rent&watch_region={}&language={}", 
                watchRegion, language);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/discover/movie")
                .queryParam("sort_by", "popularity.desc")
                .queryParam("watch_region", watchRegion)
                .queryParam("with_watch_monetization_types", "rent")
                .queryParam("include_adult", false)
                .queryParam("include_video", false)
                .queryParam("language", language)
                .queryParam("page", 1)
                .build();

        return executeGet(uriConfig, response -> latestTrailerMapper.mapToLatestTrailerResponse(response, MediaType.MOVIE));
    }

    @Override
    public Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language) {
        log.debug("Calling TMDB API: /movie/{}/videos?language={}", movieId, language);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/movie/{movieId}/videos")
                .queryParam("language", language)
                .build(movieId);

        return executeGet(uriConfig, latestTrailerMapper::mapToVideosResponse);
    }
}