package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated
public interface LatestTrailersForRentPort {
    @Deprecated
    Mono<LatestTrailerResponse> getForRent(String language, String watchRegion);
    @Deprecated
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
}