package com.api.tmdb.domain.model;

import com.api.tmdb.domain.model.enums.MediaType;

import java.util.List;

public record LatestTrailerItem(
        Integer id,
        String title,
        String overview,
        String posterPath,
        String backdropPath,
        Double popularity,
        Double voteAverage,
        Integer voteCount,
        String releaseDate,
        String originalTitle,
        String originalLanguage,
        List<Integer> genreIds,
        MediaType mediaType,
        List<String> originCountry,
        String videoKey,
        String videoSite,
        String videoType,
        Boolean videoOfficial,
        // Internal fields (not exposed in response)
        Integer nextSeasonNumber,
        Integer nextEpisodeNumber
) {
}
