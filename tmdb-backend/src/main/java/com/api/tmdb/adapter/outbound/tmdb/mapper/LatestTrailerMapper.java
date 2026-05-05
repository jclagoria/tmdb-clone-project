package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.*;
import com.api.tmdb.domain.model.enums.MediaType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LatestTrailerMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LatestTrailerMapper.class);

    private final ObjectMapper objectMapper;

    public LatestTrailerMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public LatestTrailerResponse mapToLatestTrailerResponse(Object json, MediaType mediaType) {
        try {
            ObjectMapper snakeMapper = new ObjectMapper();
            snakeMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            snakeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String jsonString = objectMapper.writeValueAsString(json);
            TmdbResponse response = snakeMapper.readValue(jsonString, TmdbResponse.class);

            List<LatestTrailerItem> items = response.results.stream()
                    .map(item -> mapToLatestTrailerItem(item, mediaType))
                    .toList();

            return new LatestTrailerResponse(response.page, items, items.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to map LatestTrailerResponse", e);
        }
    }

    private LatestTrailerItem mapToLatestTrailerItem(TmdbItem item, MediaType mediaType) {
        String title = item.title != null ? item.title : item.name;
        String originalTitle = item.originalTitle != null ? item.originalTitle : item.originalName;
        String releaseDate = MediaType.MOVIE.equals(mediaType) ? item.releaseDate : item.firstAirDate;

        return new LatestTrailerItem(
                item.id,
                title,
                item.overview,
                item.posterPath,
                item.backdropPath,
                item.popularity,
                item.voteAverage,
                item.voteCount,
                releaseDate,
                originalTitle,
                item.originalLanguage,
                item.genreIds,
                mediaType,
                item.originCountry,
                null, null, null, null, null, null
        );
    }

    public List<VideoItem> mapToVideosResponse(Object json) {
        try {
            ObjectMapper snakeMapper = new ObjectMapper();
            snakeMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            snakeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String jsonString = objectMapper.writeValueAsString(json);
            TmdbVideosResponse response = snakeMapper.readValue(jsonString, TmdbVideosResponse.class);

            return response.results.stream()
                    .map(v -> new VideoItem(v.id, v.key, v.site, v.type, v.official))
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public TvSeriesDetails mapToTvSeriesDetails(Object json) {
        try {
            ObjectMapper snakeMapper = new ObjectMapper();
            snakeMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            snakeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String jsonString = objectMapper.writeValueAsString(json);
            TmdbTvSeriesDetails response = snakeMapper.readValue(jsonString, TmdbTvSeriesDetails.class);

            EpisodeInfo nextEpisode = null;
            if (response.nextEpisodeToAir != null) {
                nextEpisode = new EpisodeInfo(
                        response.nextEpisodeToAir.seasonNumber,
                        response.nextEpisodeToAir.episodeNumber
                );
            }

            EpisodeInfo lastEpisode = null;
            if (response.lastEpisodeToAir != null) {
                lastEpisode = new EpisodeInfo(
                        response.lastEpisodeToAir.seasonNumber,
                        response.lastEpisodeToAir.episodeNumber
                );
            }

            return new TvSeriesDetails(response.id, response.name, nextEpisode, lastEpisode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map TvSeriesDetails", e);
        }
    }

    // Helper classes
    private static class TmdbResponse {
        public Integer page;
        public List<TmdbItem> results;
    }

    private static class TmdbItem {
        public Integer id;
        public String title;
        public String name;
        public String originalTitle;
        public String originalName;
        public String overview;
        public String posterPath;
        public String backdropPath;
        public String originalLanguage;
        public List<Integer> genreIds;
        public Double popularity;
        public String releaseDate;
        public String firstAirDate;
        public Double voteAverage;
        public Integer voteCount;
        public List<String> originCountry;
    }

    private static class TmdbVideosResponse {
        public Integer id;
        public List<TmdbVideo> results;
    }

    private static class TmdbVideo {
        public String id;
        public String key;
        public String site;
        public String type;
        public Boolean official;
    }

    private static class TmdbTvSeriesDetails {
        public Integer id;
        public String name;
        public TmdbEpisodeInfo nextEpisodeToAir;
        public TmdbEpisodeInfo lastEpisodeToAir;
    }

    private static class TmdbEpisodeInfo {
        public Integer seasonNumber;
        public Integer episodeNumber;
    }
}
