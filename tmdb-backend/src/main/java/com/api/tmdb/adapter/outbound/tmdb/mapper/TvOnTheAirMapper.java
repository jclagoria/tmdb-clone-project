package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.TvOnTheAirItem;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TvOnTheAirMapper {
    private static final Logger log = LoggerFactory.getLogger(TvOnTheAirMapper.class);

    @SuppressWarnings("unchecked")
    public TvOnTheAirResponse mapToTvOnTheAirResponse(Object response) {
        try {
            var responseMap = (java.util.Map<String, Object>) response;
            var results = (ArrayList<Object>) responseMap.get("results");

            List<TvOnTheAirItem> items = new ArrayList<>();
            if (results != null) {
                for (Object item : results) {
                    var itemMap = (java.util.Map<String, Object>) item;
                    items.add(mapToTvOnTheAirItem(itemMap));
                }
            }

            return new TvOnTheAirResponse(
                    getIntValue(responseMap, "page"),
                    items,
                    getIntValue(responseMap, "total_pages"),
                    getIntValue(responseMap, "total_results")
            );
        } catch (Exception e) {
            log.error("Error mapping TvOnTheAir response: {}", e.getMessage(), e);
            return new TvOnTheAirResponse(1, List.of(), 0, 0);
        }
    }

    private TvOnTheAirItem mapToTvOnTheAirItem(java.util.Map<String, Object> map) {
        return new TvOnTheAirItem(
                getLongValue(map, "id"),
                getStringValue(map, "name"),
                getStringValue(map, "overview"),
                getDoubleValue(map, "popularity"),
                getDoubleValue(map, "vote_average"),
                getIntValue(map, "vote_count"),
                getStringValue(map, "first_air_date"),
                getIntListValue(map, "genre_ids"),
                getStringListValue(map, "origin_country"),
                getStringValue(map, "original_language"),
                getStringValue(map, "original_name"),
                getStringValue(map, "backdrop_path"),
                getStringValue(map, "poster_path")
        );
    }

    private String getStringValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private Long getLongValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? ((Number) value).longValue() : null;
    }

    private Integer getIntValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? ((Number) value).intValue() : 0;
    }

    private Double getDoubleValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? ((Number) value).doubleValue() : 0.0;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getIntListValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return ((List<Number>) value).stream()
                    .map(Number::intValue)
                    .toList();
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private List<String> getStringListValue(java.util.Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return ((List<String>) value).stream().toList();
        }
        return List.of();
    }
}
