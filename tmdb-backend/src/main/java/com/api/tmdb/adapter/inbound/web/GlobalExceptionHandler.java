package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.adapter.outbound.tmdb.TmdbApiErrorResponse;
import com.api.tmdb.domain.exception.TmdbServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Mono<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> body = Map.of(
                "success", false,
                "status_code", 400,
                "status_message", ex.getMessage()
        );
        return ResponseEntity.badRequest().body(Mono.just(body));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Mono<Map<String, Object>>> handleWebClientResponseException(WebClientResponseException ex) {
        try {
            TmdbApiErrorResponse error = objectMapper.readValue(
                    ex.getResponseBodyAsString(),
                    TmdbApiErrorResponse.class
            );

            Map<String, Object> body = Map.of(
                    "success", error.success(),
                    "status_code", error.statusCode(),
                    "status_message", error.statusMessage()
            );

            HttpStatus status = switch (error.statusCode()) {
                case 5 -> HttpStatus.BAD_REQUEST;
                case 7 -> HttpStatus.UNAUTHORIZED;
                case 34 -> HttpStatus.NOT_FOUND;
                default -> HttpStatus.INTERNAL_SERVER_ERROR;
            };

            return ResponseEntity.status(status).body(Mono.just(body));
        } catch (Exception e) {
            Map<String, Object> body = Map.of(
                    "success", false,
                    "status_code", 500,
                    "status_message", "An unexpected error occurred"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(body));
        }
    }

    @ExceptionHandler(TmdbServiceException.class)
    public ResponseEntity<Mono<Map<String, Object>>> handleTmdbServiceException(TmdbServiceException ex) {
        Map<String, Object> body = Map.of(
                "success", false,
                "status_code", ex.getStatusCode(),
                "status_message", ex.getMessage()
        );
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode());
        return ResponseEntity.status(status).body(Mono.just(body));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<Map<String, Object>>> handleGenericException(Exception ex) {
        Map<String, Object> body = Map.of(
                "success", false,
                "status_code", 500,
                "status_message", "An unexpected error occurred: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(body));
    }

}

