package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.TvSeriesDetails;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TmdbStreamingPort {

    // Movies
    Mono<LatestTrailerResponse> getStreamingMovies(String language, Integer page, String watchRegion);
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);

    //TV
    Mono<LatestTrailerResponse> getStreamingTv(String language, Integer page, String watchRegion);
    Mono<TvSeriesDetails> getTvDetails(Integer tvId, String language);
    Mono<List<VideoItem>> getTvSeasonEpisodeVideos(Integer tvId, Integer seasonNumber, Integer episodeNumber, String language);
    Mono<List<VideoItem>> getTvSeasonVideos(Integer tvId, Integer seasonNumber, String language);
}
