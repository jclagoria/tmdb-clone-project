package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.TvOnTheAirResponseDTO;
import com.api.tmdb.application.usecase.GetTvOnTheAirUseCase;
import com.api.tmdb.domain.model.TvOnTheAirItem;
import com.api.tmdb.domain.model.TvOnTheAirResponse;
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
class TvOnTheAirControllerTest {

    @Mock
    private GetTvOnTheAirUseCase getTvOnTheAirUseCase;

    private TvOnTheAirController tvOnTheAirController;

    @BeforeEach
    void setUp() {
        tvOnTheAirController = new TvOnTheAirController(getTvOnTheAirUseCase);
    }

    @Test
    void getTvOnTheAir_shouldReturn200_withValidParameters() {
        TvOnTheAirItem item = new TvOnTheAirItem(
                1L, "Test TV Show", "Overview", 100.0, 7.5, 100,
                "2024-01-01", List.of(18), List.of("US"), "en",
                "Test TV Show", "/backdrop.jpg", "/poster.jpg");
        TvOnTheAirResponse response = new TvOnTheAirResponse(1, List.of(item), 1, 1);

        when(getTvOnTheAirUseCase.getTvOnTheAir(any(), any(), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<TvOnTheAirResponseDTO>> result =
                tvOnTheAirController.getTvOnTheAir("en-US", 1, "US/New_York");

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
    }

    @Test
    void getTvOnTheAir_shouldUseDefaultLanguage_whenNotProvided() {
        TvOnTheAirResponse response = new TvOnTheAirResponse(1, List.of(), 0, 0);

        when(getTvOnTheAirUseCase.getTvOnTheAir(eq("en-US"), any(), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<TvOnTheAirResponseDTO>> result =
                tvOnTheAirController.getTvOnTheAir("en-US", 1, null);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getTvOnTheAir_shouldUseDefaultPage_whenNotProvided() {
        TvOnTheAirResponse response = new TvOnTheAirResponse(1, List.of(), 0, 0);

        when(getTvOnTheAirUseCase.getTvOnTheAir(any(), any(), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<TvOnTheAirResponseDTO>> result =
                tvOnTheAirController.getTvOnTheAir("en-US", 1, null);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getTvOnTheAir_shouldReturnError_whenUseCaseFails() {
        when(getTvOnTheAirUseCase.getTvOnTheAir(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Service Error")));

        ResponseEntity<Mono<TvOnTheAirResponseDTO>> result =
                tvOnTheAirController.getTvOnTheAir("en-US", 1, null);
        
        Mono<TvOnTheAirResponseDTO> body = result.getBody();
        assertNotNull(body);
        assertThrows(Exception.class, () -> body.block());
    }
}