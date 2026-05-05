package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.LatestTrailerItem;

import java.util.List;

public record LatestTrailersItemDTO(
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
        String mediaType,
        List<String> originCountry,
        String videoKey,
        String videoSite,
        String videoType,
        Boolean videoOfficial,
        String videoUrl
) {
    public static LatestTrailersItemDTO fromDomain(LatestTrailerItem item) {
        String videoUrl = null;
        if (item.videoKey() != null && "Youtube".equals(item.videoSite())) {
            videoUrl = "https://www.youtube.com/watch?v=" + item.videoKey();
        }

        String mediaTypeStr = item.mediaType() != null
                ? item.mediaType().toString() : null;

        return new LatestTrailersItemDTO(
                item.id(),
                item.title(),
                item.overview(),
                item.posterPath(),
                item.backdropPath(),
                item.popularity(),
                item.voteAverage(),
                item.voteCount(),
                item.releaseDate(),
                item.originalTitle(),
                item.originalLanguage(),
                item.genreIds(),
                mediaTypeStr,
                item.originCountry(),
                item.videoKey(),
                item.videoSite(),
                item.videoType(),
                item.videoOfficial(),
                videoUrl
        );
    }
}
