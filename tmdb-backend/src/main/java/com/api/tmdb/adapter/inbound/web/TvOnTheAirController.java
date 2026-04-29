package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.TvOnTheAirResponseDTO;
import com.api.tmdb.application.usecase.GetTvOnTheAirUseCase;
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
@RequestMapping("/api/v1/whats-popular/tv")
@Tag(name = "TV On The Air", description = "TV shows airing in the next 7 days")
@RateLimiter(name = "cloneApi")
public class TvOnTheAirController {

    private static final Logger logger = LoggerFactory.getLogger(TvOnTheAirController.class);
    private final GetTvOnTheAirUseCase getTvOnTheAirUseCase;

    public TvOnTheAirController(GetTvOnTheAirUseCase getTvOnTheAirUseCase) {
        this.getTvOnTheAirUseCase = getTvOnTheAirUseCase;
    }

    @GetMapping("/on-the-air")
    @Operation(
            summary = "Get TV shows on the air",
            description = "Get a list of TV shows that air in the next 7 days",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved on-the-air TV shows"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<TvOnTheAirResponseDTO>> getTvOnTheAir(
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language,
            @Parameter(description = "Page number")
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @Parameter(description = "Timezone (e.g., US/New_York)")
            @RequestParam(name = "timezone", required = false) String timezone) {

        logger.info("getTvOnTheAir request: language={}, page={}, timezone={}", language, page, timezone);

        Mono<TvOnTheAirResponseDTO> response = getTvOnTheAirUseCase
                .getTvOnTheAir(language, page, timezone)
                .map(TvOnTheAirResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }

}
