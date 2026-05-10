package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.adapter.outbound.tmdb.mapper.NowPlayingMapper;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TmdbMovieListAdapterTest {

    @Mock
    private NowPlayingMapper nowPlayingMapper;

    @Mock
    private DiscoverMapper discoverMapper;

    private TmdbMovieListPort adapter;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(webClient.get().uri(any(java.util.function.Function.class)).retrieve().bodyToMono(Object.class))
            .thenReturn(Mono.just(new Object()));
        
        adapter = new TmdbMovieListAdapter(webClient, nowPlayingMapper, discoverMapper);
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
                1, List.of(item), 1, 1, new NowPlayingResponse.NowPlayingDates("2024-01-31", "2024-01-01")
        );

        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getNowPlaying_shouldHandleNullRegion() {
        NowPlayingResponse expectedResponse = new NowPlayingResponse(1, List.of(), 0, 0, null);
        when(nowPlayingMapper.mapToNowPlayingResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getNowPlaying("en-US", null, 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getPopular_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Popular Movie", "Popular Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "movie", "en",
                List.of(28), 100.0, "2024-01-01", null, 7.5, 1000,
                null, false, false
        );
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(item), 1);

        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getPopular("en-US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTopRated_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTopRated("en-US", "US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTopRated_shouldHandleNullRegion() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTopRated("en-US", null, 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getUpcoming_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getUpcoming("en-US", 1, "2024-01-01", "2024-12-31"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getUpcoming_shouldHandleNullReleaseDates() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("movie"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getUpcoming("en-US", 1, null, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}