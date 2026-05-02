package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.domain.model.DiscoverParams;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
import com.api.tmdb.domain.port.outbound.TmdbWhatsPopularClientPort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TmdbDiscoverClientAdapterTest {

    @Mock
    private DiscoverMapper discoverMapper;

    private TmdbWhatsPopularClientPort adapter;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(webClient.get().uri(any(java.util.function.Function.class)).retrieve().bodyToMono(Object.class))
            .thenReturn(Mono.just(new Object()));
        
        adapter = new TmdbDiscoverClientAdapter(webClient, discoverMapper);
    }

    @Test
    void discoverMovies_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "movie", "en",
                List.of(28), 100.0, "2024-01-01", null,
                7.5, 1000, List.of("US"), false, false
        );
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(item), 1);

        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.discoverMovies(DiscoverParams.defaultParams()))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void discoverTv_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Test TV Show", "Test TV Show", "Overview",
                "/poster.jpg", "/backdrop.jpg", "tv", "en",
                List.of(28), 100.0, null, "2024-01-01",
                7.5, 1000, List.of("US"), false, false
        );
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(item), 1);

        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("tv"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.discoverTv(DiscoverParams.defaultParams()))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void discoverMovies_shouldUseDefaultParams() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        DiscoverParams params = DiscoverParams.defaultParams();

        StepVerifier.create(adapter.discoverMovies(params))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void discoverTv_shouldUseDefaultParams() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("tv"))).thenReturn(expectedResponse);

        DiscoverParams params = DiscoverParams.defaultParams();

        StepVerifier.create(adapter.discoverTv(params))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void discoverMovies_shouldReturnEmptyResponse_whenNoResults() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.discoverMovies(DiscoverParams.defaultParams()))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void discoverTv_shouldReturnEmptyResponse_whenNoResults() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("tv"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.discoverTv(DiscoverParams.defaultParams()))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}