package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.TrendingItem;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.MediaType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrendingMapper {

    private final ObjectMapper objectMapper;

    public TrendingMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TrendingResponse mapToTrendingResponse(Object json) {
        try {
            // Configure to ignore unknown properties
            ObjectMapper snakeMapper = new ObjectMapper();
            snakeMapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
            snakeMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            String jsonString = objectMapper.writeValueAsString(json);
            TmdbResponse response = snakeMapper.readValue(jsonString, TmdbResponse.class);

            List<TrendingItem> items = response.results.stream()
                    .map(this::mapToTrendingItem).toList();

            return new TrendingResponse(
                    response.page,
                    items,
                    response.totalPages,
                    response.totalResults
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JSON to TrendingResponse", e);
        }
    }

    private TrendingItem mapToTrendingItem(TmdbItem item) {
        MediaType mediaType = null;
        if (item.mediaType != null) {
            mediaType = MediaType.fromValue(item.mediaType);
        }

        String title = item.title;
        if (title == null || title.isBlank()) {
            title = item.name;
        }

        String originalTitle = item.originalTitle;
        if (originalTitle == null || originalTitle.isBlank()) {
            originalTitle = item.originalName;
        }

        return new TrendingItem(
                item.id,
                title,
                originalTitle,
                item.overview,
                item.posterPath,
                item.backdropPath,
                mediaType,
                item.originalLanguage,
                item.genreIds,
                item.popularity,
                item.releaseDate,
                item.adult,
                item.video,
                item.voteAverage,
                item.voteCount
        );
    }

    private static class TmdbResponse {
        public Integer page;
        public Integer totalResults;
        public Integer totalPages;
        public List<TmdbItem> results;
    }

    private static class TmdbItem {
        public Boolean adult;
        public String backdropPath;
        public Integer id;
        public String title;
        public String originalTitle;
        public String overview;
        public String posterPath;
        public String mediaType;
        public String originalLanguage;
        public List<Integer> genreIds;
        public Double popularity;
        public String releaseDate;
        public String name;
        public String originalName;
        public Boolean video;
        public Double voteAverage;
        public Integer voteCount;
    }
}