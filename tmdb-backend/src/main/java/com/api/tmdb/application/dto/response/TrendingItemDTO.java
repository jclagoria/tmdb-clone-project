package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.TrendingItem;

import java.util.List;

public record TrendingItemDTO(
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
        Boolean adult,
        Boolean video,
        Double voteAverage,
        Integer voteCount
) {

    public static TrendingItemDTO fromDomain(TrendingItem item) {
        return new TrendingItemDTO(
                item.id(),
                item.title(),
                item.originalTitle(),
                item.overview(),
                item.posterPath(),
                item.backdropPath(),
                item.mediaType() != null ? item.mediaType().getValue() : null,
                item.originalLanguage(),
                item.genreIds(),
                item.popularity(),
                item.releaseDate(),
                item.adult(),
                item.video(),
                item.voteAverage(),
                item.voteCount()
        );
    }

}
