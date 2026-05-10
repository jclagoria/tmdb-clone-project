package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.LatestTrailersResponseDTO;
import com.api.tmdb.application.usecase.GetLatestTrailersForRentUseCase;
import com.api.tmdb.application.usecase.GetLatestTrailersInTheatersUseCase;
import com.api.tmdb.application.usecase.GetLatestTrailersStreamingUseCase;
import com.api.tmdb.application.usecase.GetLatestTrailersUseCase;
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
@RequestMapping("/api/v1/latest-trailers")
@Tag(name = "Latest Trailers", description = "Latest trailers with trailers")
@RateLimiter(name = "cloneApi")
public class LatestTrailersController {

    private static final Logger log = LoggerFactory.getLogger(LatestTrailersController.class);
    private final GetLatestTrailersUseCase getLatestTrailersUseCase;
    private final GetLatestTrailersStreamingUseCase streamingUseCase;
    private final GetLatestTrailersForRentUseCase forRentUseCase;
    private final GetLatestTrailersInTheatersUseCase inTheatersUseCase;

    public LatestTrailersController(
            GetLatestTrailersUseCase getLatestTrailersUseCase,
            GetLatestTrailersStreamingUseCase streamingUseCase,
            GetLatestTrailersForRentUseCase forRentUseCase,
            GetLatestTrailersInTheatersUseCase inTheatersUseCase
    ) {
        this.getLatestTrailersUseCase = getLatestTrailersUseCase;
        this.streamingUseCase = streamingUseCase;
        this.forRentUseCase = forRentUseCase;
        this.inTheatersUseCase = inTheatersUseCase;
    }

    @GetMapping("/popular")
    @Operation(
            summary = "Get popular with latest trailers",
            description = "Get top 20 popular movies and TV shows with trailers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<LatestTrailersResponseDTO>> getPopular(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language) {

        log.info("LatestTrailersPopular request: language={}", language);

        Mono<LatestTrailersResponseDTO> response = getLatestTrailersUseCase
                .getLatestTrailers(language)
                .map(LatestTrailersResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/streaming")
    @Operation(
            summary = "Get streaming latest trailers",
            description = "Get top 20 movies available on streaming platforms with trailers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<LatestTrailersResponseDTO>> getStreaming(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language,
            @Parameter(description = "Watch region ISO code (e.g., US)")
            @RequestParam(name = "watch_region", defaultValue = "US") String watchRegion) {

        log.info("LatestTrailersStreaming request: language={}, watchRegion={}", language, watchRegion);

        Mono<LatestTrailersResponseDTO> response = streamingUseCase.getStreaming(language, watchRegion)
                .map(LatestTrailersResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/for-rent")
    @Operation(
            summary = "Get movies for rent with latest trailers",
            description = "Get top 20 movies available for rent with trailers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<LatestTrailersResponseDTO>> getForRent(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language,
            @Parameter(description = "Watch region ISO code (e.g., US)")
            @RequestParam(name = "watch_region", defaultValue = "US") String watchRegion) {

        log.info("LatestTrailersForRent request: language={}, watchRegion={}", language, watchRegion);

        Mono<LatestTrailersResponseDTO> response = forRentUseCase.getForRent(language, watchRegion)
                .map(LatestTrailersResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/in-theaters")
    @Operation(
            summary = "Get in-theaters movies with latest trailers",
            description = "Get top 20 movies currently in theaters with trailers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<LatestTrailersResponseDTO>> getInTheaters(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language) {

        log.info("LatestTrailersInTheaters request: language={}", language);

        Mono<LatestTrailersResponseDTO> response = inTheatersUseCase.getInTheaters(language)
                .map(LatestTrailersResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

}
