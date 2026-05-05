package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.TvSeriesDetails;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TmdbLatestTrailerTvPort {
    Mono<LatestTrailerResponse> getPopularTv(String language, Integer page);
    Mono<TvSeriesDetails> getTvSeriesDetails(Integer seriesId, String language);
    Mono<List<VideoItem>> getTvEpisodeVideos(Integer seriesId, Integer seasonNumber,
                                             Integer episodeNumber, String language);
}
