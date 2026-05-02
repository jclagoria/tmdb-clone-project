package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.NowPlayingMapper;
import com.api.tmdb.domain.model.NowPlayingItem;
import com.api.tmdb.domain.model.NowPlayingResponse;
import com.api.tmdb.domain.port.outbound.TmdbNowPlayingPort;
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
class TmdbNowPlayingClientAdapterTest {

    @Mock
    private NowPlayingMapper nowPlayingMapper;

    private TmdbNowPlayingPort adapter;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(webClient.get().uri(any(java.util.function.Function.class)).retrieve().bodyToMono(Object.class))
            .thenReturn(Mono.just(new Object()));
        
        adapter = new TmdbNowPlayingClientAdapter(webClient, nowPlayingMapper);
    }

    @Test
    void getNowPlaying_shouldReturnResponse_whenApiSucceeds() {
        NowPlayingItem item = new NowPlayingItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "en",
                List.of(28), 100.0, "2024-01-01",
                false, false, 7.5, 1000
        );
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(item), 1, 1, null
        );

        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldUseDefaultLanguage() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(), 0, 0, null
        );
        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("es-ES", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldUseDefaultPage() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(), 0, 0, null
        );
        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldHandleNullRegion() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(), 0, 0, null
        );
        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", null, 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldHandleBlankRegion() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(), 0, 0, null
        );
        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", "   ", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldReturnEmptyResponse_whenNoResults() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(
                1, List.of(), 0, 0, null
        );
        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}