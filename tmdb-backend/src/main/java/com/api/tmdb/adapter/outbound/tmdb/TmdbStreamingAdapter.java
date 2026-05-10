package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.LatestTrailerMapper;
import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.TvSeriesDetails;
import com.api.tmdb.domain.model.VideoItem;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.outbound.TmdbStreamingPort;
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
@Deprecated
public class TmdbStreamingAdapter extends TmdbBaseAdapter implements TmdbStreamingPort {

    private static final Logger log = LoggerFactory.getLogger(TmdbStreamingAdapter.class);
    private final LatestTrailerMapper latestTrailerMapper;

    public TmdbStreamingAdapter(
            @Qualifier("tmdbWebClient") WebClient webClient,
            LatestTrailerMapper latestTrailerMapper) {
        super(webClient);
        this.latestTrailerMapper = latestTrailerMapper;
    }


    @Override
    public Mono<LatestTrailerResponse> getStreamingMovies(String language, Integer page, String watchRegion) {
        log.debug("Calling TMDB API: /discover/movie?watch_region={}&with_watch_monetization_types=flatrate", watchRegion);

        Function<UriBuilder, URI> uriConfig = builder -> builder
                .path("/discover/movie")
                .queryParam("include_adult", false)
                .queryParam("include_video", false)
                .queryParam("language", language)
                .queryParam("page", page != null ? page : 1)
                .queryParam("sort_by", "popularity.desc")
                .queryParam("watch_region", watchRegion)
                .queryParam("with_watch_monetization_types", "flatrate")
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

    @Override
    public Mono<LatestTrailerResponse> getStreamingTv(String language, Integer page, String watchRegion) {
        log.debug("Calling TMDB API: /discover/tv?watch_region={}&with_watch_monetization_types=flatrate", watchRegion);

        Function<UriBuilder, URI> uriConfig = uriBuilder -> uriBuilder
                .path("/discover/tv")
                .queryParam("include_adult", false)
                .queryParam("include_null_first_air_dates", false)
                .queryParam("language", language)
                .queryParam("page", page != null ? page : 1)
                .queryParam("sort_by", "popularity.desc")
                .queryParam("watch_region", watchRegion)
                .queryParam("with_watch_monetization_types", "flatrate")
                .build();

        return executeGet(uriConfig, response -> latestTrailerMapper
                .mapToLatestTrailerResponse(response, MediaType.MOVIE));
    }

    @Override
    public Mono<TvSeriesDetails> getTvDetails(Integer tvId, String language) {
        log.debug("Calling TMDB API: /tv/{}?language={}", tvId, language);

        Function<UriBuilder, URI> uriConfig = uriBuilder -> uriBuilder
                .path("/tv/{teId}")
                .queryParam("language", language)
                .build(tvId);

        return executeGet(uriConfig, latestTrailerMapper::mapToTvSeriesDetails);
    }

    @Override
    public Mono<List<VideoItem>> getTvSeasonEpisodeVideos(Integer tvId, Integer seasonNumber, Integer episodeNumber, String language) {
        log.debug("Calling TMDB API: /tv/{}/season/{}/episode/{}?language={}",
                tvId, seasonNumber, episodeNumber, language);

        Function<UriBuilder, URI> uriConfig = uriBuilder -> uriBuilder
                .path("/tv/{tvId}/season/{seasonNumber}/episode/{episodeNumber}/videos")
                .queryParam("language", language)
                .build(tvId, seasonNumber, episodeNumber);

        return executeGet(uriConfig, latestTrailerMapper::mapToVideosResponse);
    }

    @Override
    public Mono<List<VideoItem>> getTvSeasonVideos(Integer tvId, Integer seasonNumber, String language) {
        log.debug("Calling TMDB API: /tv/{}/season/{}/videos?language={}", tvId, seasonNumber, language);

        Function<UriBuilder, URI> uriConfig = uriBuilder -> uriBuilder
                .path("/tv/{tvId}/season/{seasonNumber}/videos")
                .queryParam("language", language)
                .build(tvId, seasonNumber);

        return executeGet(uriConfig, latestTrailerMapper::mapToVideosResponse);
    }
}
