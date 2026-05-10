package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TvOnTheAirItem;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbTvSeriesListPort;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTvOnTheAirUseCaseTest {

    @Mock
    private TmdbTvSeriesListPort tmdbTvSeriesListPort;

    private GetTvOnTheAirUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTvOnTheAirUseCase(tmdbTvSeriesListPort);
    }

    @Test
    void getOnTheAir_shouldCallTmdbTvSeriesListPort() {
        TvOnTheAirItem item = new TvOnTheAirItem(
                1L, "Test TV Show", "Overview", 100.0,
                8.5, 1000, "2024-01-01", List.of(18),
                List.of("US"), "en", "Test TV Show",
                "/backdrop.jpg", "/poster.jpg"
        );
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(item), 10, 1);

        when(tmdbTvSeriesListPort.getOnTheAir(eq("en-US"), eq(1), eq("America/New_York")))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getOnTheAir("en-US", 1, "America/New_York"))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.page());
                    assertEquals(1, response.totalResults());
                    assertEquals("Test TV Show", response.results().get(0).name());
                })
                .verifyComplete();
    }

    @Test
    void getOnTheAir_shouldUseDefaultTimezone_whenNull() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tmdbTvSeriesListPort.getOnTheAir(anyString(), anyInt(), eq("America/New_York")))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getOnTheAir("en-US", 1, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getOnTheAir_shouldUseDefaultPage_whenLessThanOne() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tmdbTvSeriesListPort.getOnTheAir(anyString(), eq(1), anyString()))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getOnTheAir("en-US", 0, "America/New_York"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getOnTheAir_shouldReturnError_whenClientFails() {
        when(tmdbTvSeriesListPort.getOnTheAir(anyString(), anyInt(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("API Error")));

        StepVerifier.create(useCase.getOnTheAir("en-US", 1, "America/New_York"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void getAiringToday_shouldCallTmdbTvSeriesListPort() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tmdbTvSeriesListPort.getAiringToday(eq("en-US"), eq(1), anyString()))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getAiringToday("en-US", 1, "US/New_York"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getPopular_shouldCallTmdbTvSeriesListPort() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Popular TV", "Popular TV", "Overview",
                "/poster.jpg", "/backdrop.jpg", "tv", "en",
                List.of(28), 100.0, null, "2024-01-01", 7.5, 1000,
                List.of("US"), false, false
        );
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(item), 1);

        when(tmdbTvSeriesListPort.getPopular(eq("en-US"), eq(1)))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getPopular("en-US", 1))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.results().size());
                })
                .verifyComplete();
    }

    @Test
    void getTopRated_shouldCallTmdbTvSeriesListPort() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(tmdbTvSeriesListPort.getTopRated(eq("en-US"), eq(1)))
                .thenReturn(Mono.just(expectedResponse));

        StepVerifier.create(useCase.getTopRated("en-US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}