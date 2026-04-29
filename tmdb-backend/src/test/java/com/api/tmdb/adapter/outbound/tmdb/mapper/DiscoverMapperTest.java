package com.api.tmdb.adapter.outbound.tmdb.mapper;

import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DiscoverMapperTest {

    private DiscoverMapper discoverMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        discoverMapper = new DiscoverMapper(objectMapper);
    }

    @Test
    void mapToWhatsPopularResponse_shouldMapMovieResponseCorrectly() {
        Map<String, Object> tmdbResponse = new HashMap<>();
        tmdbResponse.put("page", 1);
        
        List<Map<String, Object>> items = createMovieItemsList();
        tmdbResponse.put("results", items);

        WhatsPopularResponse result = discoverMapper.mapToWhatsPopularResponse(tmdbResponse, "movie");

        assertNotNull(result);
        assertEquals(1, result.page());
        assertEquals(1, result.results().size());
        
        WhatsPopularItem itemResult = result.results().get(0);
        assertEquals(1, itemResult.id());
        assertEquals("Test Movie", itemResult.title());
        assertEquals("movie", itemResult.mediaType());
        assertEquals("2024-01-01", itemResult.releaseDate());
        assertNull(itemResult.firstAirDate());
    }

    @Test
    void mapToWhatsPopularResponse_shouldMapTvResponseCorrectly() {
        Map<String, Object> tmdbResponse = new HashMap<>();
        tmdbResponse.put("page", 1);
        
        List<Map<String, Object>> items = createTvItemsList();
        tmdbResponse.put("results", items);

        WhatsPopularResponse result = discoverMapper.mapToWhatsPopularResponse(tmdbResponse, "tv");

        assertNotNull(result);
        assertEquals(1, result.results().size());
        
        WhatsPopularItem itemResult = result.results().get(0);
        assertEquals(2, itemResult.id());
        assertEquals("Test TV Show", itemResult.title());
        assertEquals("tv", itemResult.mediaType());
        assertEquals("2024-01-01", itemResult.firstAirDate());
        assertNull(itemResult.releaseDate());
    }

    @Test
    void mapToWhatsPopularResponse_shouldHandleMultipleItems() {
        Map<String, Object> tmdbResponse = new HashMap<>();
        tmdbResponse.put("page", 1);
        
        List<Map<String, Object>> items = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("title", "Movie 1");
        item1.put("popularity", 100.0);
        items.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("title", "Movie 2");
        item2.put("popularity", 200.0);
        items.add(item2);
        
        tmdbResponse.put("results", items);

        WhatsPopularResponse result = discoverMapper.mapToWhatsPopularResponse(tmdbResponse, "movie");

        assertEquals(2, result.results().size());
    }

    @Test
    void mapToWhatsPopularResponse_shouldReturnEmptyList_whenNoResults() {
        Map<String, Object> tmdbResponse = new HashMap<>();
        tmdbResponse.put("page", 1);
        tmdbResponse.put("results", new ArrayList<>());

        WhatsPopularResponse result = discoverMapper.mapToWhatsPopularResponse(tmdbResponse, "movie");

        assertNotNull(result);
        assertTrue(result.results().isEmpty());
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> createMovieItemsList() {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("title", "Test Movie");
        item.put("original_title", "Test Movie");
        item.put("overview", "Overview");
        item.put("poster_path", "/poster.jpg");
        item.put("backdrop_path", "/backdrop.jpg");
        item.put("original_language", "en");
        
        List<Integer> genreIds = new ArrayList<>();
        genreIds.add(28);
        item.put("genre_ids", genreIds);
        
        item.put("popularity", 100.0);
        item.put("release_date", "2024-01-01");
        item.put("vote_average", 7.5);
        item.put("vote_count", 1000);
        item.put("adult", false);
        items.add(item);
        return items;
    }
    
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> createTvItemsList() {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 2);
        item.put("name", "Test TV Show");
        item.put("original_name", "Test TV Show");
        item.put("overview", "Overview");
        item.put("poster_path", "/poster.jpg");
        item.put("backdrop_path", "/backdrop.jpg");
        item.put("original_language", "en");
        
        List<Integer> genreIds = new ArrayList<>();
        genreIds.add(18);
        item.put("genre_ids", genreIds);
        
        item.put("popularity", 200.0);
        item.put("first_air_date", "2024-01-01");
        item.put("vote_average", 8.0);
        item.put("vote_count", 500);
        
        List<String> originCountry = new ArrayList<>();
        originCountry.add("US");
        item.put("origin_country", originCountry);
        
        item.put("adult", false);
        items.add(item);
        return items;
    }
}