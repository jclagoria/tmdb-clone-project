package com.api.tmdb.domain.model;

import java.util.List;

public record LatestTrailerResponse(
        Integer page,
        List<LatestTrailerItem> results,
        Integer totalResults
) {
}
