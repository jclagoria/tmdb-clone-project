package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.WhatsPopularResponseDTO;
import com.api.tmdb.application.usecase.GetDiscoverUseCase;
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
@RequestMapping("/api/v1/free-to-watch")
@Tag(name = "Free to Watch", description = "Free movies and TV shows available to stream")
@RateLimiter(name = "cloneApi")
public class FreeToWatchController {

    private static final Logger logger = LoggerFactory.getLogger(FreeToWatchController.class);
    private final GetDiscoverUseCase getDiscoverUseCase;

    public FreeToWatchController(GetDiscoverUseCase getDiscoverUseCase) {
        this.getDiscoverUseCase = getDiscoverUseCase;
    }

    @GetMapping("/movie")
    @Operation(
            summary = "Get free movies to watch",
            description = "Get popular movies available for free (free monetization type)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved free movies"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<WhatsPopularResponseDTO>> getFreeMovies(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language,
            @Parameter(description = "Region code (e.g., US)")
            @RequestParam(name = "region", defaultValue = "US") String region,
            @Parameter(description = "Page number")
            @RequestParam(name = "page", defaultValue = "1") Integer page) {
        logger.info("getFreeMovies request: language={}, region={}, page={}", language, region, page);

        Mono<WhatsPopularResponseDTO> response = getDiscoverUseCase
                .getFreeMovies(language, region, page)
                .map(WhatsPopularResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tv")
    @Operation(
            summary = "Get free TV shows to watch",
            description = "Get popular TV shows available for free (free monetization type)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved free TV shows"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<WhatsPopularResponseDTO>> getFreeTvShows(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language,
            @Parameter(description = "Region code (e.g., US)")
            @RequestParam(name = "region", defaultValue = "US") String region,
            @Parameter(description = "Page number")
            @RequestParam(name = "page", defaultValue = "1") Integer page) {
        logger.info("getFreeTvShows request: language={}, region={}, page={}", language, region, page);

        Mono<WhatsPopularResponseDTO> response = getDiscoverUseCase
                .getFreeTvShows(language, region, page)
                .map(WhatsPopularResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }
}