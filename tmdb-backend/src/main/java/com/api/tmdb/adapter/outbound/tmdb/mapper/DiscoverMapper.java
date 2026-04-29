package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscoverMapper {

    private final ObjectMapper objectMapper;

    public  DiscoverMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WhatsPopularResponse mapToWhatsPopularResponse(Object json, String mediaTypeStr) {
        try {
            ObjectMapper snakeMapper = new ObjectMapper();
            snakeMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            snakeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String jsonString = objectMapper.writeValueAsString(json);
            TmdbDiscoverResponse response = snakeMapper.readValue(jsonString, TmdbDiscoverResponse.class);

            List<WhatsPopularItem> items = response.results.stream()
                    .map(item -> mapToWhatsPopularItem(item, mediaTypeStr))
                    .toList();

            return new WhatsPopularResponse(response.page, items, items.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JSON to WhatsPopularResponse", e);
        }
    }

    private WhatsPopularItem mapToWhatsPopularItem(TmdbDiscoverItem item, String mediaTypeStr) {
        String title = item.title;
        if (title == null || title.isBlank()) {
            title = item.name;
        }

        String originalTitle = item.originalTitle;
        if (originalTitle == null || originalTitle.isBlank()) {
            originalTitle = item.originalName;
        }

        return new WhatsPopularItem(
                item.id,
                title,
                originalTitle,
                item.overview,
                item.posterPath,
                item.backdropPath,
                mediaTypeStr,
                item.originalLanguage,
                item.genreIds,
                item.popularity,
                "movie".equals(mediaTypeStr) ? item.releaseDate : null,
                "tv".equals(mediaTypeStr) ? item.firstAirDate : null,
                item.voteAverage,
                item.voteCount,
                item.originCountry,
                item.adult,
                item.video
        );
    }

    private static class TmdbDiscoverResponse {
        public Integer page;
        public Integer totalResults;
        public Integer totalPages;
        public List<TmdbDiscoverItem> results;
    }

    private static class TmdbDiscoverItem {
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
        public String firstAirDate;
        public String name;
        public String originalName;
        public Boolean video;
        public Double voteAverage;
        public Integer voteCount;
        public List<String> originCountry;
    }

}
