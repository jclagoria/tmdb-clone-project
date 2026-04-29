package com.api.tmdb.domain.model;

public record DiscoverParams(
        String sortBy,
        String watchRegion,
        String withWatchMonetizationTypes,
        Integer page,
        String language,
        Boolean includeAdult
) {
    public static DiscoverParams defaultParams() {
        return new DiscoverParams(
                "popularity.desc",
                "US",
                "flatrate",
                1,
                "en-US",
                false
        );
    }
}
