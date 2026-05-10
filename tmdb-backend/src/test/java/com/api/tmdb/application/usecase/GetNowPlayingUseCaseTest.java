package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.NowPlayingItem;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbMovieListPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetNowPlayingUseCaseTest {

    @Mock
    private TmdbMovieListPort tmdbMovieListPort;

    private GetNowPlayingUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetNowPlayingUseCase(tmdbMovieListPort);
    }

    @Test
    void getNowPlaying_shouldCallTmdbMovieListPort_withCorrectParameters() {
        NowPlayingItem item = new NowPlayingItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "en",
                List.of(28), 100.0, "2024-01-01",
                false, false, 7.5, 1000
        );
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(item), 1, 1, new NowPlayingResponse.NowPlayingDates("2024-01-31", "2024-01-01")
        );

        when(tmdbMovieListPort.getNowPlaying(eq("en-US"), eq("US"), eq(1)))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getNowPlaying("en-US", "US", 1))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.page());
                    assertEquals(1, response.totalResults());
                    assertEquals("Test Movie", response.results().get(0).title());
                })
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldUseDefaultLanguage_whenNull() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(1, List.of(), 0, 0, null);
        when(tmdbMovieListPort.getNowPlaying(eq("en-US"), any(), any()))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getNowPlaying(null, "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldUseDefaultRegion_whenNull() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(1, List.of(), 0, 0, null);
        when(tmdbMovieListPort.getNowPlaying(any(), eq("US"), any()))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getNowPlaying("en-US", null, 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldUseDefaultPage_whenLessThanOne() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(1, List.of(), 0, 0, null);
        when(tmdbMovieListPort.getNowPlaying(any(), any(), eq(1)))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getNowPlaying("en-US", "US", 0))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldReturnError_whenClientFails() {
        when(tmdbMovieListPort.getNowPlaying(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API Error")));

        StepVerifier.create(useCase.getNowPlaying("en-US", "US", 1))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getPopular_shouldCallTmdbMovieListPort() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Popular Movie", "Popular Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "movie", "en",
                List.of(28), 100.0, "2024-01-01", null, 7.5, 1000,
                null, false, false
        );
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(item), 1);

        when(tmdbMovieListPort.getPopular(eq("en-US"), eq(1)))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getPopular("en-US", 1))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.results().size());
                })
                .verifyComplete();
    }

    @Test
    void getTopRated_shouldCallTmdbMovieListPort() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);

        when(tmdbMovieListPort.getTopRated(eq("en-US"), eq("US"), eq(1)))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getTopRated("en-US", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getUpcoming_shouldCallTmdbMovieListPort() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);

        when(tmdbMovieListPort.getUpcoming(eq("en-US"), eq(1), any(), any()))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getUpcoming("en-US", 1, "2024-01-01", "2024-12-31"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}