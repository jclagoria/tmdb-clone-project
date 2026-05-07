package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TmdbStreamingPort {

    // Movies
    Mono<LatestTrailerResponse> getStreamingMovies(String language, Integer page, String watchRegion);
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);

    //TV
    default Mono<LatestTrailerResponse> getStreamingTv(String language, Integer page, String watchRegion) {
        return Mono.empty();
    }

    default Mono<List<VideoItem>> getTvVideos(Integer page, String language) {
        return Mono.empty();
    }
}
