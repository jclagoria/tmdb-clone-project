package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbDiscoverPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDiscoverUseCaseTest {

    @Mock
    private TmdbDiscoverPort tmdbDiscoverPort;

    private GetDiscoverUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetDiscoverUseCase(tmdbDiscoverPort);
    }

    @Test
    void getWhatsPopular_shouldCombineMoviesAndTv() {
        WhatsPopularResponse moviesResponse = new WhatsPopularResponse(1, List.of(
                new WhatsPopularItem(1, "Movie 1", "Movie 1", "Overview", "/poster.jpg", 
                        "/backdrop.jpg", "movie", "en", List.of(28), 100.0, "2024-01-01", 
                        null, 7.5, 1000, null, false, false)
        ), 1);
        WhatsPopularResponse tvResponse = new WhatsPopularResponse(1, List.of(
                new WhatsPopularItem(2, "TV 1", "TV 1", "Overview", "/poster.jpg", 
                        "/backdrop.jpg", "tv", "en", List.of(28), 90.0, null, "2024-01-01", 
                        7.5, 1000, null, false, false)
        ), 1);
        
        when(tmdbDiscoverPort.discoverMovies(any())).thenReturn(Mono.just(moviesResponse));
        when(tmdbDiscoverPort.discoverTv(any())).thenReturn(Mono.just(tvResponse));

        StepVerifier.create(useCase.getWhatsPopular("en-US", "US", 1))
            .assertNext(response -> {
                assertNotNull(response);
                assertEquals(2, response.results().size());
            })
            .verifyComplete();
    }

    @Test
    void getForRent_shouldReturnMovies() {
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(
                new WhatsPopularItem(1, "Rent Movie", "Rent Movie", "Overview", "/poster.jpg", 
                        "/backdrop.jpg", "movie", "en", List.of(28), 100.0, "2024-01-01", 
                        null, 7.5, 1000, null, false, false)
        ), 1);
        
        when(tmdbDiscoverPort.discoverMovies(any())).thenReturn(Mono.just(response));

        StepVerifier.create(useCase.getForRent("en-US", "US", 1))
            .assertNext(r -> {
                assertNotNull(r);
                assertEquals(1, r.results().size());
            })
            .verifyComplete();
    }

    @Test
    void discoverMovies_shouldDelegateToPort() {
        DiscoverParams params = new DiscoverParams("popularity.desc", "US", "flatrate", 1, "en-US", false);
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);
        
        when(tmdbDiscoverPort.discoverMovies(params)).thenReturn(Mono.just(response));

        StepVerifier.create(useCase.discoverMovies(params))
            .assertNext(r -> assertNotNull(r))
            .verifyComplete();
    }

    @Test
    void discoverTv_shouldDelegateToPort() {
        DiscoverParams params = new DiscoverParams("popularity.desc", "US", "flatrate", 1, "en-US", false);
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);
        
        when(tmdbDiscoverPort.discoverTv(params)).thenReturn(Mono.just(response));

        StepVerifier.create(useCase.discoverTv(params))
            .assertNext(r -> assertNotNull(r))
            .verifyComplete();
    }
}