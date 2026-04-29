package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.WhatsPopularResponseDTO;
import com.api.tmdb.application.usecase.GetWhatsPopularUseCase;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/whats-popular")
@Tag(name = "Whats Popular", description = "What's popular streaming content")
@RateLimiter(name = "cloneApi")
public class WhatsPopularController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsPopularController.class);
    private final GetWhatsPopularUseCase getWhatsPopularUseCase;

    public WhatsPopularController(GetWhatsPopularUseCase getWhatsPopularUseCase) {
        this.getWhatsPopularUseCase = getWhatsPopularUseCase;
    }

    @GetMapping("/streaming")
    @Operation(
            summary = "Get what's popular for streaming",
            description = "Get popular movies and TV shows available for streaming (flatrate)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved popular streaming content"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<WhatsPopularResponseDTO>> getWhatsPopular(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language,
            @Parameter(description = "Region code (e.g., US)")
            @RequestParam(name = "region", defaultValue = "US") String region,
            @Parameter(description = "Page number")
            @RequestParam(name = "page", defaultValue = "1") Integer page) {

        logger.info("getWhatsPopular request: language={}, region={}, page={}", language, region, page);

        Mono<WhatsPopularResponseDTO> response = getWhatsPopularUseCase
                .getWhatsPopular(language, region, page)
                .map(WhatsPopularResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

}
