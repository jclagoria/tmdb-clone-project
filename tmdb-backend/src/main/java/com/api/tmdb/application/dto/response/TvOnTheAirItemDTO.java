package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.TvOnTheAirItem;

import java.util.List;

public record TvOnTheAirItemDTO(
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
        public static TvOnTheAirItemDTO fromDomain(TvOnTheAirItem item) {
            return new TvOnTheAirItemDTO(
                    item.id(),
                    item.name(),
                    item.overview(),
                    item.popularity(),
                    item.voteAverage(),
                    item.voteCount(),
                    item.firstAirDate(),
                    item.genreIds(),
                    item.originCountry(),
                    item.originalLanguage(),
                    item.originalName(),
                    item.backdropPath(),
                    item.posterPath()
            );
        }
}
