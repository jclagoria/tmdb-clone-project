package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.NowPlayingItem;

import java.util.List;

public record NowPlayingItemDTO(
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
    public static NowPlayingItemDTO fromDomain(NowPlayingItem item) {
        return new NowPlayingItemDTO(
                item.id(),
                item.title(),
                item.originalTitle(),
                item.overview(),
                item.posterPath(),
                item.backdropPath(),
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