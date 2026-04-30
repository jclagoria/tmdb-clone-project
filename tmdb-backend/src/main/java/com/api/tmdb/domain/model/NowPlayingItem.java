package com.api.tmdb.domain.model;

import java.util.List;

public record NowPlayingItem(
        Integer id,
        String title,
        String originalTitle,
        String overview,
        String posterPath,
        String backdropPath,
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