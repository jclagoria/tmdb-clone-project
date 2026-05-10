package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.TvSeriesDetails;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated
public interface TmdbStreamingPort {
    @Deprecated
    Mono<LatestTrailerResponse> getStreamingMovies(String language, Integer page, String watchRegion);
    @Deprecated
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
    @Deprecated
    Mono<LatestTrailerResponse> getStreamingTv(String language, Integer page, String watchRegion);
    @Deprecated
    Mono<TvSeriesDetails> getTvDetails(Integer tvId, String language);
    @Deprecated
    Mono<List<VideoItem>> getTvSeasonEpisodeVideos(Integer tvId, Integer seasonNumber, Integer episodeNumber, String language);
    @Deprecated
    Mono<List<VideoItem>> getTvSeasonVideos(Integer tvId, Integer seasonNumber, String language);
}