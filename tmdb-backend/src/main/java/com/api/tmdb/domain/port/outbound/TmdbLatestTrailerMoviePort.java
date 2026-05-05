package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TmdbLatestTrailerMoviePort {
    Mono<LatestTrailerResponse> getMostPopularMovies(String language, Integer page);
    Mono<LatestTrailerResponse> getUpcomingMovies(String language, Integer page,
                                                  String releaseDateGte, String releaseDateLte);
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
}
