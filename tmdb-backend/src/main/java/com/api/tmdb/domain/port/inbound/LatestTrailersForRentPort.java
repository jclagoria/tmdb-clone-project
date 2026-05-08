package com.api.tmdb.domain.port.inbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LatestTrailersForRentPort {
    Mono<LatestTrailerResponse> getForRent(String language, String watchRegion);
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
}