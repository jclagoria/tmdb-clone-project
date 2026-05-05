package com.api.tmdb.adapter.inbound;

import com.api.tmdb.application.dto.response.LatestTrailersResponseDTO;
import com.api.tmdb.application.usecase.GetLatestTrailersPopularUseCase;
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
    private final GetLatestTrailersPopularUseCase useCase;

    public LatestTrailersController(GetLatestTrailersPopularUseCase useCase) {
        this.useCase = useCase;
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

        Mono<LatestTrailersResponseDTO> response = useCase.getPopular(language)
                .map(LatestTrailersResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

}
