package com.api.tmdb.domain.model;

import java.util.List;

public record NowPlayingResponse(
        Integer page,
        List<NowPlayingItem> results,
        Integer totalResults,
        Integer totalPages,
        NowPlayingDates dates
) {
    public record NowPlayingDates(String maximum, String minimum) {}
}