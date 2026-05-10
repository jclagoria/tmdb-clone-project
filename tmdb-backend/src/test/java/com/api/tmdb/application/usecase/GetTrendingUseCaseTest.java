package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.TrendingItem;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.outbound.TmdbTrendingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTrendingUseCaseTest {

    @Mock
    private TmdbTrendingPort tmdbTrendingPort;

    private GetTrendingUseCase getTrendingUseCase;

    @BeforeEach
    void setUp() {
        getTrendingUseCase = new GetTrendingUseCase(tmdbTrendingPort);
    }

    @Test
    void getTrending_shouldCallTmdbTrendingPort() {
        TrendingItem item = new TrendingItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", null, "en",
                List.of(28), 100.0, "2024-01-01", false,
                false, 7.5, 1000
        );
        TrendingResponse response = new TrendingResponse(1, List.of(item), 1, 1);

        when(tmdbTrendingPort.getTrending(eq(TimeWindow.DAY), eq("en-US")))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getTrendingUseCase.getTrending(TimeWindow.DAY, "en-US"))
                .expectNext(response)
                .verifyComplete();

        verify(tmdbTrendingPort).getTrending(TimeWindow.DAY, "en-US");
    }

    @Test
    void getTrending_shouldUseDefaultLanguage_whenNull() {
        TrendingResponse response = new TrendingResponse(1, List.of(), 0, 0);

        when(tmdbTrendingPort.getTrending(any(TimeWindow.class), eq("en-US")))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getTrendingUseCase.getTrending(TimeWindow.WEEK, null))
                .expectNext(response)
                .verifyComplete();

        verify(tmdbTrendingPort).getTrending(TimeWindow.WEEK, "en-US");
    }

    @Test
    void getTrending_shouldUseDefaultLanguage_whenBlank() {
        TrendingResponse response = new TrendingResponse(1, List.of(), 0, 0);

        when(tmdbTrendingPort.getTrending(any(TimeWindow.class), eq("en-US")))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getTrendingUseCase.getTrending(TimeWindow.WEEK, "   "))
                .expectNext(response)
                .verifyComplete();

        verify(tmdbTrendingPort).getTrending(TimeWindow.WEEK, "en-US");
    }

    @Test
    void getTrending_shouldUseDefaultTimeWindow_whenNull() {
        TrendingResponse response = new TrendingResponse(1, List.of(), 0, 0);

        when(tmdbTrendingPort.getTrending(eq(TimeWindow.WEEK), eq("en-US")))
                .thenReturn(Mono.just(response));

        StepVerifier.create(getTrendingUseCase.getTrending(null, "en-US"))
                .expectNext(response)
                .verifyComplete();

        verify(tmdbTrendingPort).getTrending(TimeWindow.WEEK, "en-US");
    }

    @Test
    void getTrending_shouldReturnError_whenClientFails() {
        when(tmdbTrendingPort.getTrending(any(), any()))
                .thenReturn(Mono.error(new RuntimeException("API Error")));

        StepVerifier.create(getTrendingUseCase.getTrending(TimeWindow.DAY, "en-US"))
                .expectError(RuntimeException.class)
                .verify();
    }
}