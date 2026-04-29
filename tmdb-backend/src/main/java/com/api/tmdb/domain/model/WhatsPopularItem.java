package com.api.tmdb.domain.model;

import java.util.List;

public record WhatsPopularItem(
        Integer id,
        String title,
        String originalTitle,
        String overview,
        String posterPath,
        String backdropPath,
        String mediaType,
        String originalLanguage,
        List<Integer> genreIds,
        Double popularity,
        String releaseDate,
        String firstAirDate,
        Double voteAverage,
        Integer voteCount,
        List<String> originCountry,
        Boolean adult,
        Boolean video
) {
}
