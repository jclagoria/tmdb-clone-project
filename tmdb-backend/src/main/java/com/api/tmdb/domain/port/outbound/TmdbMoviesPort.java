package com.api.tmdb.domain.port.outbound;

import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.TvSeriesDetails;
import com.api.tmdb.domain.model.VideoItem;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TmdbMoviesPort {
    
    Mono<LatestTrailerResponse> getMostPopularMovies(String language, Integer page);
    
    Mono<LatestTrailerResponse> getUpcomingMovies(String language, Integer page, 
                                                    String releaseDateGte, String releaseDateLte);
    
    Mono<LatestTrailerResponse> getNowPlayingMovies(String language, Integer page, String region);
    
    Mono<List<VideoItem>> getMovieVideos(Integer movieId, String language);
    
    Mono<LatestTrailerResponse> discoverMovies(String language, Integer page, 
                                                String watchRegion, String monetizationType);
    
    Mono<LatestTrailerResponse> discoverTvShows(String language, Integer page, 
                                                 String watchRegion, String monetizationType);
    
    Mono<TvSeriesDetails> getTvDetails(Integer tvId, String language);
    
    Mono<List<VideoItem>> getTvSeasonVideos(Integer tvId, Integer seasonNumber, String language);
    
    Mono<List<VideoItem>> getTvEpisodeVideos(Integer tvId, Integer seasonNumber, 
                                              Integer episodeNumber, String language);
}