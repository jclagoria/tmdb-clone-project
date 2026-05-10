package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.LatestTrailerMapper;
import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.outbound.TmdbMoviesPort;
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
public class TmdbMoviesAdapter extends TmdbBaseAdapter implements TmdbMoviesPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbMoviesAdapter.class);
    private final LatestTrailerMapper latestTrailerMapper;

    public TmdbMoviesAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            LatestTrailerMapper latestTrailerMapper) {
        super(webClient);
        this.latestTrailerMapper = latestTrailerMapper;
    }

    @Override
    public Mono<LatestTrailerResponse> getMostPopularMovies(String language, Integer page) {
        log.debug("Calling TMDB API: /discover/movie?sort_by=popularity.desc&language={}&page={}", language, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/discover/movie")
                .queryParam("sort_by", "popularity.desc")
                .queryParam("include_adult", false)
                .queryParam("include_video", false)
                .queryParam("language", language)
                .queryParam("page", page != null ? page : 1)
                .build();

        return executeGet(uriConfig, response -> latestTrailerMapper.mapToLatestTrailerResponse(response, MediaType.MOVIE));
    }

    @Override
    public Mono<LatestTrailerResponse> getUpcomingMovies(String language, Integer page,
                                                         String releaseDateGte, String releaseDateLte) {
        log.debug("Calling TMDB API: /discover/movie?release_date.gte={}&release_date.lte={}&language={}&page={}", 
                releaseDateGte, releaseDateLte, language, page);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/discover/movie")
                .queryParam("sort_by", "popularity.desc")
                .queryParam("include_adult", false)
                .queryParam("include_video", false)
                .queryParam("language", language)
                .queryParam("page", page != null ? page : 1)
                .queryParam("with_release_type", "2|3")
                .queryParam("release_date.gte", releaseDateGte)
                .queryParam("release_date.lte", releaseDateLte)
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