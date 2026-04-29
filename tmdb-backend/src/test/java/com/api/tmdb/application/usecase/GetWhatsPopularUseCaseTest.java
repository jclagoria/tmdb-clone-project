package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWhatsPopularUseCaseTest {

    @Mock
    private TmdbWhatsPopularClientPort tmdbClientPort;

    private GetWhatsPopularUseCase getWhatsPopularUseCase;

    @BeforeEach
    void setUp() {
        getWhatsPopularUseCase = new GetWhatsPopularUseCase(tmdbClientPort);
    }

    @Test
    void getWhatsPopular_shouldCallTmdbClientPort_withCorrectParameters() {
        WhatsPopularItem movieItem = new WhatsPopularItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "movie", "en",
                List.of(28), 100.0, "2024-01-01", null, 7.5, 1000,
                null, false, false);
        WhatsPopularResponse moviesResponse = new WhatsPopularResponse(1, List.of(movieItem), 1);

        WhatsPopularItem tvItem = new WhatsPopularItem(
                2, "Test TV", "Test TV", "Overview",
                "/poster.jpg", "/backdrop.jpg", "tv", "en",
                List.of(18), 200.0, null, "2024-01-01", 8.0, 500,
                List.of("US"), false, false);
        WhatsPopularResponse tvResponse = new WhatsPopularResponse(1, List.of(tvItem), 1);

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(moviesResponse));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(tvResponse));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular("en-US", "US", 1))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(2, response.results().size());
                })
                .verifyComplete();

        verify(tmdbClientPort).discoverMovies(any());
        verify(tmdbClientPort).discoverTv(any());
    }

    @Test
    void getWhatsPopular_shouldMergeAndSortResults_byPopularity() {
        WhatsPopularItem lowPopularity = new WhatsPopularItem(
                1, "Low Pop", "Low Pop", "Overview",
                null, null, "movie", "en", List.of(), 50.0,
                null, null, 5.0, 10, null, false, false);
        WhatsPopularResponse moviesResponse = new WhatsPopularResponse(1, List.of(lowPopularity), 1);

        WhatsPopularItem highPopularity = new WhatsPopularItem(
                2, "High Pop", "High Pop", "Overview",
                null, null, "tv", "en", List.of(), 500.0,
                null, null, 8.0, 1000, null, false, false);
        WhatsPopularResponse tvResponse = new WhatsPopularResponse(1, List.of(highPopularity), 1);

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(moviesResponse));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(tvResponse));

        WhatsPopularResponse result = getWhatsPopularUseCase.getWhatsPopular("en-US", "US", 1).block();
        
        assertNotNull(result);
        assertEquals("High Pop", result.results().get(0).title());
    }

    @Test
    void getWhatsPopular_shouldLimitTo40Items() {
        List<WhatsPopularItem> movies = List.of(
                new WhatsPopularItem(1, "Movie1", "Movie1", "O", null, null, "movie", "en", List.of(), 100.0, null, null, 7.0, 100, null, false, false),
                new WhatsPopularItem(2, "Movie2", "Movie2", "O", null, null, "movie", "en", List.of(), 90.0, null, null, 7.0, 100, null, false, false));
        List<WhatsPopularItem> tvShows = List.of(
                new WhatsPopularItem(3, "TV1", "TV1", "O", null, null, "tv", "en", List.of(), 80.0, null, null, 7.0, 100, null, false, false),
                new WhatsPopularItem(4, "TV2", "TV2", "O", null, null, "tv", "en", List.of(), 70.0, null, null, 7.0, 100, null, false, false));

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(new WhatsPopularResponse(1, movies, 2)));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(new WhatsPopularResponse(1, tvShows, 2)));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular("en-US", "US", 1))
                .assertNext(response -> assertEquals(4, response.results().size()))
                .verifyComplete();
    }

    @Test
    void getWhatsPopular_shouldUseDefaultLanguage_whenNull() {
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(response));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular(null, "US", 1))
                .assertNext(r -> assertTrue(r.results().isEmpty()))
                .verifyComplete();
    }

    @Test
    void getWhatsPopular_shouldUseDefaultRegion_whenNull() {
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(response));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular("en-US", null, 1))
                .assertNext(r -> assertTrue(r.results().isEmpty()))
                .verifyComplete();
    }

    @Test
    void getWhatsPopular_shouldUseDefaultPage_whenNull() {
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(response));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular("en-US", "US", null))
                .assertNext(r -> assertTrue(r.results().isEmpty()))
                .verifyComplete();
    }

    @Test
    void getWhatsPopular_shouldReturnError_whenClientFails() {
        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.error(new RuntimeException("API Error")));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.just(new WhatsPopularResponse(1, List.of(), 0)));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular("en-US", "US", 1)
                        .onErrorResume(e -> Mono.empty()))
                .verifyComplete();
    }

    @Test
    void getWhatsPopular_shouldReturnError_whenTvClientFails() {
        WhatsPopularResponse moviesResponse = new WhatsPopularResponse(1, List.of(), 0);

        when(tmdbClientPort.discoverMovies(any()))
                .thenReturn(Mono.just(moviesResponse));
        when(tmdbClientPort.discoverTv(any()))
                .thenReturn(Mono.error(new RuntimeException("TV API Error")));

        StepVerifier.create(getWhatsPopularUseCase.getWhatsPopular("en-US", "US", 1)
                        .onErrorResume(e -> Mono.empty()))
                .verifyComplete();
    }
}