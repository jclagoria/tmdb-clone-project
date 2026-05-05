package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.LatestTrailerResponse;

import java.util.List;

public record LatestTrailersResponseDTO(
        Integer page,
        List<LatestTrailersItemDTO> results,
        Integer totalResults
) {
    public static LatestTrailersResponseDTO fromDomain(LatestTrailerResponse response) {
        return new LatestTrailersResponseDTO(
                response.page(),
                response.results().stream()
                        .map(LatestTrailersItemDTO::fromDomain)
                        .toList(),
                response.totalResults()
        );
    }
}
