package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.WhatsPopularItem;

import java.util.List;

public record WhatsPopularItemDTO(
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
        List<String> originCountry
) {
    public static WhatsPopularItemDTO fromDomain(WhatsPopularItem item) {
        return new WhatsPopularItemDTO(
                item.id(),
                item.title(),
                item.originalTitle(),
                item.overview(),
                item.posterPath(),
                item.backdropPath(),
                item.mediaType(),
                item.originalLanguage(),
                item.genreIds(),
                item.popularity(),
                item.releaseDate(),
                item.firstAirDate(),
                item.voteAverage(),
                item.voteCount(),
                item.originCountry()
        );
    }
}
