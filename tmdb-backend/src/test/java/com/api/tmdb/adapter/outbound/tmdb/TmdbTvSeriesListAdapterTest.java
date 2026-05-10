package com.api.tmdb.adapter.outbound.tmdb;

import com.api.tmdb.adapter.outbound.tmdb.mapper.DiscoverMapper;
import com.api.tmdb.adapter.outbound.tmdb.mapper.TvOnTheAirMapper;
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
class TmdbTvSeriesListAdapterTest {

    @Mock
    private TvOnTheAirMapper tvOnTheAirMapper;

    @Mock
    private DiscoverMapper discoverMapper;

    private TmdbTvSeriesListPort adapter;

    @BeforeEach
    void setUp() {
        WebClient webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        when(webClient.get().uri(any(java.util.function.Function.class)).retrieve().bodyToMono(Object.class))
            .thenReturn(Mono.just(new Object()));
        
        adapter = new TmdbTvSeriesListAdapter(webClient, tvOnTheAirMapper, discoverMapper);
    }

    @Test
    void getAiringToday_shouldReturnResponse_whenApiSucceeds() {
        TvOnTheAirItem item = new TvOnTheAirItem(
                1L, "Test TV", "Overview", 100.0, 8.5, 1000,
                "2024-01-01", List.of(18), List.of("US"), "en",
                "Test TV", "/backdrop.jpg", "/poster.jpg"
        );
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(item), 10, 1);

        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getAiringToday("en-US", 1, "US/New_York"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getOnTheAir_shouldReturnResponse_whenApiSucceeds() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getOnTheAir("en-US", 1, "US/New_York"))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getOnTheAir_shouldHandleNullTimezone() {
        TvOnTheAirResponse expectedResponse = new TvOnTheAirResponse(1, List.of(), 0, 0);
        when(tvOnTheAirMapper.mapToTvOnTheAirResponse(any())).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getOnTheAir("en-US", 1, null))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getPopular_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Popular TV", "Popular TV", "Overview",
                "/poster.jpg", "/backdrop.jpg", "tv", "en",
                List.of(28), 100.0, null, "2024-01-01", 7.5, 1000,
                List.of("US"), false, false
        );
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(item), 1);

        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("tv"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getPopular("en-US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }

    @Test
    void getTopRated_shouldReturnResponse_whenApiSucceeds() {
        WhatsPopularResponse expectedResponse = new WhatsPopularResponse(1, List.of(), 0);
        when(discoverMapper.mapToWhatsPopularResponse(any(), eq("tv"))).thenReturn(expectedResponse);

        StepVerifier.create(adapter.getTopRated("en-US", 1))
                .expectNext(expectedResponse)
                .verifyComplete();
    }
}