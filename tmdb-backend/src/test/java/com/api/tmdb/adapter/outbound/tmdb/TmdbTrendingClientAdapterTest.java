package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TrendingMapper;
import com.api.tmdb.domain.model.TrendingItem;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import com.api.tmdb.domain.port.outbound.TmdbClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TmdbTrendingClientAdapterTest {

    @Mock
    private TrendingMapper trendingMapper;

    private TmdbClientPort adapter;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        
        when(webClient.get().uri(any(java.util.function.Function.class)).retrieve().bodyToMono(Object.class))
            .thenReturn(Mono.just(new Object()));
        
        adapter = new TmdbTrendingClientAdapter(webClient, trendingMapper);
    }

    @Test
    void getTrending_shouldReturnResponse_whenApiSucceeds() {
        TrendingItem item = new TrendingItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", null, "en",
                List.of(28), 100.0, "2024-01-01", false,
                false, 7.5, 1000
        );
        TrendingResponse expectedResponse = new TrendingResponse(1, List.of(item), 1, 1);

        when(trendingMapper.mapToTrendingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTrending(TimeWindow.DAY, "en-US"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTrending_shouldUseTimeWindowDay() {
        TrendingResponse expectedResponse = new TrendingResponse(1, List.of(), 0, 0);
        when(trendingMapper.mapToTrendingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTrending(TimeWindow.DAY, "en-US"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTrending_shouldUseTimeWindowWeek() {
        TrendingResponse expectedResponse = new TrendingResponse(1, List.of(), 0, 0);
        when(trendingMapper.mapToTrendingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTrending(TimeWindow.WEEK, "en-US"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTrending_shouldPassLanguageParameter() {
        TrendingResponse expectedResponse = new TrendingResponse(1, List.of(), 0, 0);
        when(trendingMapper.mapToTrendingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTrending(TimeWindow.DAY, "es-ES"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTrending_shouldReturnEmptyResponse_whenNoResults() {
        TrendingResponse expectedResponse = new TrendingResponse(1, List.of(), 0, 0);
        when(trendingMapper.mapToTrendingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTrending(TimeWindow.DAY, "en-US"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}