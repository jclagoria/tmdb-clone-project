package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated
public interface TmdbLatestTrailerMoviePort {
    @Deprecated
    Mono<LatestTrailerResponse> getMostPopularMovies(String language, Integer page);
    @Deprecated
    Mono<LatestTrailerResponse> getUpcomingMovies(String language, Integer page,
                                                  String releaseDateGte, String releaseDateLte);
    @Deprecated
    Mono<LatestTrailerResponse> getInTheatersMovies(String language, Integer page,
                                                     String releaseDateGte, String releaseDateLte);
    @Deprecated
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
}