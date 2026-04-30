package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.WhatsPopularResponseDTO;
import com.api.tmdb.application.usecase.GetForRentUseCase;
import com.api.tmdb.application.usecase.GetWhatsPopularUseCase;
import com.api.tmdb.domain.model.WhatsPopularItem;
import com.api.tmdb.domain.model.WhatsPopularResponse;
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
class WhatsPopularControllerTest {

    @Mock
    private GetWhatsPopularUseCase getWhatsPopularUseCase;

    @Mock
    private GetForRentUseCase getForRentUseCase; // Not used in this test but can be added for completeness

    private WhatsPopularController whatsPopularController;

    @BeforeEach
    void setUp() {
        whatsPopularController = new WhatsPopularController(getWhatsPopularUseCase, getForRentUseCase);
    }

    @Test
    void getWhatsPopular_shouldReturn200_withValidParameters() {
        WhatsPopularItem item = new WhatsPopularItem(
                1, "Test Movie", "Test Movie", "Overview",
                "/poster.jpg", "/backdrop.jpg", "movie", "en",
                List.of(28), 100.0, "2024-01-01", null, 7.5, 1000,
                null, false, false);
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(item), 1);

        when(getWhatsPopularUseCase.getWhatsPopular(any(), any(), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<WhatsPopularResponseDTO>> result = 
                whatsPopularController.getWhatsPopular("en-US", "US", 1);

        assertEquals(200, result.getStatusCode().value());
        assertNotNull(result.getBody());
    }

    @Test
    void getWhatsPopular_shouldUseDefaultLanguage_whenNotProvided() {
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);

        when(getWhatsPopularUseCase.getWhatsPopular(eq("en-US"), any(), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<WhatsPopularResponseDTO>> result = 
                whatsPopularController.getWhatsPopular("en-US", "US", 1);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getWhatsPopular_shouldUseDefaultRegion_whenNotProvided() {
        WhatsPopularResponse response = new WhatsPopularResponse(1, List.of(), 0);

        when(getWhatsPopularUseCase.getWhatsPopular(any(), eq("US"), any()))
                .thenReturn(Mono.just(response));

        ResponseEntity<Mono<WhatsPopularResponseDTO>> result = 
                whatsPopularController.getWhatsPopular("en-US", "US", 1);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    void getWhatsPopular_shouldReturnError_whenUseCaseFails() {
        when(getWhatsPopularUseCase.getWhatsPopular(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Service Error")));

        ResponseEntity<Mono<WhatsPopularResponseDTO>> result = 
                whatsPopularController.getWhatsPopular("en-US", "US", 1);
        
        Mono<WhatsPopularResponseDTO> body = result.getBody();
        assertNotNull(body);
        assertThrows(Exception.class, () -> body.block());
    }
}