package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.TrendingResponse;
import com.api.tmdb.domain.model.enums.TimeWindow;

import java.util.List;

public record TrendingResponseDTO(
        Integer page,
        List<TrendingItemDTO> results,
        Integer totalPages,
        Integer totalResults
) {
    public static TrendingResponseDTO fromDomain(TrendingResponse response) {
        return new TrendingResponseDTO(
                response.page(),
                response.results() != null
                        ? response.results().stream().map(TrendingItemDTO::fromDomain).toList()
                        : List.of(),
                response.totalPages(),
                response.totalResults()
        );
    }
}
