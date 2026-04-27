package com.api.tmdb.domain.model;

import java.util.List;

public record TrendingResponse(
        Integer page,
        List<TrendingItem> results,
        Integer totalPages,
        Integer totalResults
) {
}
