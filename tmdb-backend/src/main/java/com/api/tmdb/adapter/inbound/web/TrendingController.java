package com.api.tmdb.adapter.inbound.web;

import com.api.tmdb.application.dto.response.TrendingResponseDTO;
import com.api.tmdb.application.usecase.GetTrendingUseCase;
import com.api.tmdb.domain.exception.TmdbServiceException;
import com.api.tmdb.domain.model.enums.TimeWindow;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/trending")
@Tag(name = "Trending", description = "Trending movies, TV shows and people")
@RateLimiter(name = "cloneApi")
public class TrendingController {

    private static final Logger log = LoggerFactory.getLogger(TrendingController.class);
    private final GetTrendingUseCase getTrendingUseCase;

    public TrendingController(GetTrendingUseCase getTrendingUseCase) {
        this.getTrendingUseCase = getTrendingUseCase;
    }

    @GetMapping("/{timeWindow}")
    @Operation(
            summary = "Get trending items",
            description = "Get the trending movies, TV shows and people on TMDB",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved trending items"),
                    @ApiResponse(responseCode = "400", description = "Invalid time window parameter"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<Mono<TrendingResponseDTO>> getTrending(
            @Parameter(description = "Time window: day or week", required = true)
            @PathVariable String timeWindow,
            @Parameter(description = "Language code (e.g., en-US)")
            @RequestParam(name = "language", defaultValue = "en-US") String language) {

        log.debug("getTrending request: timeWindow={}, language={}", timeWindow, language);
        
        TimeWindow window = TimeWindow.fromValue(timeWindow);
        Mono<TrendingResponseDTO> response = getTrendingUseCase
                .getTrending(window, language)
                .map(TrendingResponseDTO::fromDomain);

        return ResponseEntity.ok(response);
    }
}
