package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.TvOnTheAirResponse;

import java.util.List;

public record TvOnTheAirResponseDTO(
        int page,
        List<TvOnTheAirItemDTO> results,
        int totalPages,
        int totalResults
) {
    public static TvOnTheAirResponseDTO fromDomain(TvOnTheAirResponse response) {
        List<TvOnTheAirItemDTO> items = response.results()
                .stream()
                .map(TvOnTheAirItemDTO::fromDomain).toList();
        return new TvOnTheAirResponseDTO(
                response.page(),
                items,
                response.totalPages(),
                response.totalResults()
        );
    }
}
