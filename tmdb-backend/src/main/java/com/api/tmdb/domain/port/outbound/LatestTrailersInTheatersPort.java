package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated
public interface LatestTrailersInTheatersPort {
    @Deprecated
    Mono<LatestTrailerResponse> getInTheaters(String language);
    @Deprecated
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
}