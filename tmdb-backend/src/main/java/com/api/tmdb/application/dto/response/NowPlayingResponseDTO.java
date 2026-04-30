package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.NowPlayingResponse;

import java.util.List;

public record NowPlayingResponseDTO(
        Integer page,
        List<NowPlayingItemDTO> results,
        Integer totalResults,
        Integer totalPages,
        NowPlayingDatesDTO dates
) {
    public static NowPlayingResponseDTO fromDomain(NowPlayingResponse response) {
        return new NowPlayingResponseDTO(
                response.page(),
                response.results() != null
                        ? response.results().stream().map(NowPlayingItemDTO::fromDomain).toList()
                        : List.of(),
                response.totalResults(),
                response.totalPages(),
                response.dates() != null
                        ? new NowPlayingDatesDTO(response.dates().maximum(), response.dates().minimum())
                        : null
        );
    }

    public record NowPlayingDatesDTO(String maximum, String minimum) {}
}