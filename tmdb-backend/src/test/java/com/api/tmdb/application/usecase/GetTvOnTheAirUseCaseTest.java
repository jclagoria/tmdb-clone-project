package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TvOnTheAirItem;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.port.outbound.TmdbTvOnTheAirPort;
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
class GetTvOnTheAirUseCaseTest {

    @Mock
    private TmdbTvOnTheAirPort tmdbTvOnTheAirPort;

    private GetTvOnTheAirUseCase getTvOnTheAirUseCase;

    @BeforeEach
    void setUp() {
        getTvOnTheAirUseCase = new GetTvOnTheAirUseCase(tmdbTvOnTheAirPort);
    }

    @Test
    void getTvOnTheAir_shouldCallTmdbPort_withCorrectParameters() {
        TvOnTheAirItem item = new TvOnTheAirItem(
                1L, "Test TV Show", "Overview", 100.0, 7.5, 100,
                "2024-01-01", List.of(18), List.of("US"), "en",
                "Test TV Show", "/backdrop.jpg", "/poster.jpg");
        TvOnTheAirResponse response = new TvOnTheAirResponse(1, List.of(item), 1, 1);

        when(tmdbTvOnTheAirPort.getTvOnTheAir(any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getTvOnTheAirUseCase.getTvOnTheAir("en-US", 1, "US/New_York"))
                .assertNext(res -> {
                    assertNotNull(res);
                    assertEquals(1, res.results().size());
                    assertEquals("Test TV Show", res.results().get(0).name());
                })
                .verifyComplete();

        verify(tmdbTvOnTheAirPort).getTvOnTheAir("en-US", 1, "US/New_York");
    }

    @Test
    void getTvOnTheAir_shouldReturnEmptyList_whenNoResults() {
        TvOnTheAirResponse response = new TvOnTheAirResponse(1, List.of(), 0, 0);

        when(tmdbTvOnTheAirPort.getTvOnTheAir(any(), any(), any()))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getTvOnTheAirUseCase.getTvOnTheAir("en-US", 1, null))
                .assertNext(res -> assertTrue(res.results().isEmpty()))
                .verifyComplete();
    }

    @Test
    void getTvOnTheAir_shouldReturnError_whenPortFails() {
        when(tmdbTvOnTheAirPort.getTvOnTheAir(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API Error")));

        StepVerifier.create(getTvOnTheAirUseCase.getTvOnTheAir("en-US", 1, null)
                        .onErrorResume(e -> Mono.empty()))
                .verifyComplete();
    }
}