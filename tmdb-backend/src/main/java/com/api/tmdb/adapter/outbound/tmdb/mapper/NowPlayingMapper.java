package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.NowPlayingItem;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NowPlayingMapper {

    private final ObjectMapper objectMapper;

    public NowPlayingMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NowPlayingResponse mapToNowPlayingResponse(Object json) {
        try {
            ObjectMapper snakeMapper = new ObjectMapper();
            snakeMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            snakeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String jsonString = objectMapper.writeValueAsString(json);
            TmdbNowPlayingResponse response = snakeMapper.readValue(jsonString, TmdbNowPlayingResponse.class);

            List<NowPlayingItem> items = response.results.stream()
                    .map(this::mapToNowPlayingItem)
                    .toList();

            NowPlayingResponse.NowPlayingDates dates = null;
            if (response.dates != null) {
                dates = new NowPlayingResponse.NowPlayingDates(response.dates.maximum, response.dates.minimum);
            }

            return new NowPlayingResponse(response.page, items, response.totalResults, response.totalPages, dates);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JSON to NowPlayingResponse", e);
        }
    }

    private NowPlayingItem mapToNowPlayingItem(TmdbNowPlayingItem item) {
        return new NowPlayingItem(
                item.id,
                item.title,
                item.originalTitle,
                item.overview,
                item.posterPath,
                item.backdropPath,
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

    private static class TmdbNowPlayingResponse {
        public Integer page;
        public Integer totalResults;
        public Integer totalPages;
        public List<TmdbNowPlayingItem> results;
        public TmdbDates dates;
    }

    private static class TmdbDates {
        public String maximum;
        public String minimum;
    }

    private static class TmdbNowPlayingItem {
        public Boolean adult;
        public String backdropPath;
        public Integer id;
        public String title;
        public String originalTitle;
        public String overview;
        public String posterPath;
        public String originalLanguage;
        public List<Integer> genreIds;
        public Double popularity;
        public String releaseDate;
        public Boolean video;
        public Double voteAverage;
        public Integer voteCount;
    }
}