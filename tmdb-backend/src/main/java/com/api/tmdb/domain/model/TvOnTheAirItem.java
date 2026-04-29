package com.api.tmdb.domain.model;

import java.util.List;

public record TvOnTheAirItem(
        Long id,
        String name,
        String overview,
        Double popularity,
        Double voteAverage,
        Integer voteCount,
        String firstAirDate,
        List<Integer> genreIds,
        List<String> originCountry,
        String originalLanguage,
        String originalName,
        String backdropPath,
        String posterPath
) {
}
