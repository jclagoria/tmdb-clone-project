package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.TvOnTheAirItem;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TvOnTheAirMapperTest {

    private TvOnTheAirMapper tvOnTheAirMapper;

    @BeforeEach
    void setUp() {
        tvOnTheAirMapper = new TvOnTheAirMapper();
    }

    @Test
    void mapToTvOnTheAirResponse_shouldMapCorrectly() {
        Map<String, Object> response = new HashMap<>();
        response.put("page", 1);
        response.put("total_pages", 10);
        response.put("total_results", 100);

        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("name", "Test TV Show");
        item.put("overview", "Test overview");
        item.put("popularity", 100.0);
        item.put("vote_average", 7.5);
        item.put("vote_count", 100);
        item.put("first_air_date", "2024-01-01");
        item.put("genre_ids", new ArrayList<>(List.of(18, 80)));
        item.put("origin_country", new ArrayList<>(List.of("US")));
        item.put("original_language", "en");
        item.put("original_name", "Test TV Show");
        item.put("backdrop_path", "/backdrop.jpg");
        item.put("poster_path", "/poster.jpg");

        ArrayList<Object> resultsList = new ArrayList<>();
        resultsList.add(item);
        response.put("results", resultsList);

        TvOnTheAirResponse result = tvOnTheAirMapper.mapToTvOnTheAirResponse(response);

        assertEquals(1, result.page());
        assertEquals(10, result.totalPages());
        assertEquals(100, result.totalResults());
        assertEquals(1, result.results().size());
    }

    @Test
    void mapToTvOnTheAirResponse_shouldHandleNullResults() {
        Map<String, Object> response = new HashMap<>();
        response.put("page", 1);
        response.put("total_pages", 0);
        response.put("total_results", 0);
        response.put("results", null);

        TvOnTheAirResponse result = tvOnTheAirMapper.mapToTvOnTheAirResponse(response);

        assertNotNull(result);
        assertTrue(result.results().isEmpty());
    }

    @Test
    void mapToTvOnTheAirItem_shouldMapAllFields() {
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("name", "Test");
        item.put("overview", "Overview");
        item.put("popularity", 100.0);
        item.put("vote_average", 7.5);
        item.put("vote_count", 100);
        item.put("first_air_date", "2024-01-01");
        item.put("genre_ids", new ArrayList<>(List.of(18)));
        item.put("origin_country", new ArrayList<>(List.of("US", "CA")));
        item.put("original_language", "en");
        item.put("original_name", "Original");
        item.put("backdrop_path", "/back.jpg");
        item.put("poster_path", "/poster.jpg");

        Map<String, Object> response = new HashMap<>();
        response.put("page", 1);
        response.put("total_pages", 1);
        response.put("total_results", 1);
        ArrayList<Object> resultsList = new ArrayList<>();
        resultsList.add(item);
        response.put("results", resultsList);

        TvOnTheAirItem result = tvOnTheAirMapper.mapToTvOnTheAirResponse(response).results().get(0);

        assertEquals(1L, result.id());
        assertEquals("Test", result.name());
        assertEquals(List.of(18), result.genreIds());
    }
}