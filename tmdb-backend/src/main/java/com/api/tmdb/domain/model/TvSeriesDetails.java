package com.api.tmdb.domain.model;

public record TvSeriesDetails(
        Integer id,
        String name,
        EpisodeInfo nextEpisodeToAir,
        EpisodeInfo lastEpisodeToAir
) {
}
