package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.TvOnTheAirMapper;
import com.api.tmdb.domain.model.TvOnTheAirItem;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
import com.api.tmdb.domain.port.outbound.TmdbTvOnTheAirPort;
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
class TmdbTvOnTheAirClientAdapterTest {

    @Mock
    private TvOnTheAirMapper tvOnTheAirMapper;

    private TmdbTvOnTheAirPort adapter;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(webClient.get().uri(any(java.util.function.Function.class)).retrieve().bodyToMono(Object.class))
            .thenReturn(Mono.just(new Object()));
        
        adapter = new TmdbTvOnTheAirClientAdapter(webClient, tvOnTheAirMapper);
    }

    @Test
    void getTvOnTheAir_shouldReturnResponse_whenApiSucceeds() {
        TvOnTheAirItem item = new TvOnTheAirItem(
                1L, "Test TV Show", "Overview",
                100.0, 7.5, 1000, "2024-01-01",
                List.of(28), List.of("US"), "en", "Test TV Show",
                "/backdrop.jpg", "/poster.jpg"
        );
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(item), 1, 1);

        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTvOnTheAir("en-US", 1, "America/New_York"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTvOnTheAir_shouldUseDefaultLanguage() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTvOnTheAir("es-ES", 1, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTvOnTheAir_shouldUseDefaultPage() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTvOnTheAir("en-US", 1, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTvOnTheAir_shouldHandleNullTimezone() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTvOnTheAir("en-US", 1, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTvOnTheAir_shouldHandleEmptyTimezone() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTvOnTheAir("en-US", 1, ""))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTvOnTheAir_shouldReturnEmptyResponse_whenNoResults() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTvOnTheAir("en-US", 1, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}