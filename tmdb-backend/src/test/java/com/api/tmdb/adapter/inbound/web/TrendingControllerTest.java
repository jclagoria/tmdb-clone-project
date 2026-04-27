package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.TrendingResponseDTO;
import com.api.tmdb.application.usecase.GetTrendingUseCase;
import com.api.tmdb.domain.model.TrendingItem;
import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrendingControllerTest {

    @Mock
    private GetTrendingUseCase getTrendingUseCase;

    private TrendingController trendingController;

    @BeforeEach
    void setUp() {
        trendingController = new TrendingController(getTrendingUseCase);
    }

    @Test
    void getTrending_shouldReturn200_withValidParameters() {
        TrendingResponse response = new TrendingResponse(1, List.of(
                new TrendingItem(1, "Test", "Test", "Overview", null, null, null, "en", 
                        List.of(28), 100.0, "2024-01-01", false, false, 7.5, 1000)
        ), 1, 1);

        when(getTrendingUseCase.getTrending(any(TimeWindow.class), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<TrendingResponseDTO>> result = trendingController.getTrending("day", "en-US");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
    }

    @Test
    void getTrending_shouldThrowException_forInvalidTimeWindow() {
        assertThrows(IllegalArgumentException.class, 
                () -> trendingController.getTrending("invalid", "en-US"));
    }

    @Test
    void getTrending_shouldUseDefaultLanguage_whenNotProvided() {
        TrendingResponse response = new TrendingResponse(1, List.of(), 0, 0);

        when(getTrendingUseCase.getTrending(eq(TimeWindow.WEEK), eq("en-US")))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<TrendingResponseDTO>> result = trendingController.getTrending("week", "en-US");

        assertEquals(200, result.getStatusCode().value());
    }
}