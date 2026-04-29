package com.api.tmdb.domain.model;

import java.util.List;

public record TvOnTheAirResponse(
        int page,
        List<TvOnTheAirItem> results,
        int totalPages,
        int totalResults
) {
}
