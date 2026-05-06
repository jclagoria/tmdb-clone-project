package com.api.tmdb.application.dto.response;

import com.api.tmdb.domain.model.LatestTrailerItem;

import java.util.Collections;
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
        VideosWrapperDTO videos
) {
    public static LatestTrailersItemDTO fromDomain(LatestTrailerItem item) {
        List<VideoResultDTO> results;
        if (item.videoKey() != null && item.videoSite() != null) {
            String videoUrl = "YouTube".equalsIgnoreCase(item.videoSite())
                    ? "https://www.youtube.com/watch?v=" + item.videoKey()
                    : null;
            results = List.of(new VideoResultDTO(
                    item.videoKey(),
                    item.videoSite(),
                    item.videoType(),
                    item.videoOfficial(),
                    videoUrl
            ));
        } else {
            results = Collections.emptyList();
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
                new VideosWrapperDTO(results)
        );
    }

    record VideosWrapperDTO(List<VideoResultDTO> results) {}

    record VideoResultDTO(
            String videoKey,
            String videoSite,
            String videoType,
            Boolean videoOfficial,
            String videoUrl
    ) {}
}
