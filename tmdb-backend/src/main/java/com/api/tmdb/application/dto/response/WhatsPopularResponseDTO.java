package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.WhatsPopularResponse;

import java.util.List;

public record WhatsPopularResponseDTO(
        Integer page,
        List<WhatsPopularItemDTO> result,
        Integer totalResults
) {
    public static WhatsPopularResponseDTO fromDomain(WhatsPopularResponse response) {
        return new WhatsPopularResponseDTO(
                response.page(),
                response.results() != null
                        ? response.results().stream().map(WhatsPopularItemDTO::fromDomain).toList()
                        : List.of(),
                response.totalResults()
        );
    }
}
