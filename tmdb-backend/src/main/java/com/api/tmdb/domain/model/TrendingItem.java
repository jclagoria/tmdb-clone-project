package com.api.tmdb.domain.model;

import com.api.tmdb.domain.model.enums.MediaType;

import java.util.List;

public record TrendingItem(
        Integer id,
        String title,
        String originalTitle,
        String overview,
        String posterPath,
        String backdropPath,
        MediaType mediaType,
        String originalLanguage,
        List<Integer> genreIds,
        Double popularity,
        String releaseDate,
        Boolean adult,
        Boolean video,
        Double voteAverage,
        Integer voteCount
) {
}
