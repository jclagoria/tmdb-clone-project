package com.api.tmdb.application.usecase;

import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.*;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.inbound.LatestTrailersStreamingPort;
import com.api.tmdb.domain.port.outbound.TmdbStreamingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;

@Service
public class GetLatestTrailersStreamingUseCase implements LatestTrailersStreamingPort {

    private static final Logger log = LoggerFactory.getLogger(GetLatestTrailersStreamingUseCase.class);
    private static final int MOVIE_LIMIT = 20;
    private static final int TV_LIMIT = 20;
    private static final int FINAL_LIMIT = 20;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbStreamingPort streamingPort;
    private final CacheService cacheService;

    public GetLatestTrailersStreamingUseCase(TmdbStreamingPort streamingPort, CacheService cacheService) {
        this.streamingPort = streamingPort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<LatestTrailerResponse> getStreaming(String language, String watchRegion) {
        String lang = (language == null || language.isBlank()) ? "en-US" : language;
        String region = (watchRegion == null || watchRegion.isBlank()) ? "US" : watchRegion;
        String cacheKey = "latestTrailers:streaming:" + lang + ":" + region;

        return cacheService.get(cacheKey, LatestTrailerResponse.class)
                .flatMap(cached -> Mono.just(cached))
                .switchIfEmpty(Mono.defer(() -> fetchAndCache(lang, region, cacheKey)))
                .doOnError(error -> log.error("GetLatestTrailersStreamingUseCase failed: {}", error.getMessage(), error));
    }

    private Mono<LatestTrailerResponse> fetchAndCache(String language, String watchRegion, String cacheKey) {
        log.debug("Fetching streaming content: language={}, watchRegion={}", language, watchRegion);

        Mono<List<LatestTrailerItem>> moviesMono = streamingPort.getStreamingMovies(language, 1, watchRegion)
                .map(response -> processItems(response.results(), MediaType.MOVIE, MOVIE_LIMIT));

        Mono<List<LatestTrailerItem>> tvShowsMono = streamingPort.getStreamingTv(language, 1, watchRegion)
                .map(response -> processItems(response.results(), MediaType.TV, TV_LIMIT));

        return Mono.zip(moviesMono, tvShowsMono)
                .flatMap(tuple -> {
                    List<LatestTrailerItem> merged = new ArrayList<>();
                    merged.addAll(tuple.getT1());
                    merged.addAll(tuple.getT2());

                    List<LatestTrailerItem> deduplicated = deduplicate(merged);
                    List<LatestTrailerItem> sorted = deduplicated.stream()
                            .sorted(Comparator.comparingDouble(LatestTrailerItem::popularity).reversed())
                            .limit(FINAL_LIMIT)
                            .toList();

                    return fetchVideos(sorted, language);
                })
                .flatMap(response ->
                        cacheService.set(cacheKey, response, CACHE_TTL)
                                .thenReturn(response)
                );
    }

    private List<LatestTrailerItem> processItems(List<LatestTrailerItem> items, MediaType mediaType, int limit) {
        return items.stream()
                .limit(limit)
                .map(item -> new LatestTrailerItem(
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
                        mediaType,
                        item.originCountry(),
                        null, null, null, null, null, null
                ))
                .toList();
    }

    private List<LatestTrailerItem> deduplicate(List<LatestTrailerItem> items) {
        Set<Integer> seenIds = new HashSet<>();
        return items.stream()
                .filter(item -> seenIds.add(item.id()))
                .toList();
    }

    private Mono<LatestTrailerResponse> fetchVideos(List<LatestTrailerItem> items, String language) {
        List<Mono<LatestTrailerItem>> videoRequests = items.stream()
                .map(item -> fetchVideoForItem(item, language))
                .toList();

        return Mono.zip(videoRequests, results -> {
            List<LatestTrailerItem> enriched = Arrays.stream(results)
                    .map(LatestTrailerItem.class::cast)
                    .sorted(Comparator.comparingDouble(LatestTrailerItem::popularity).reversed())
                    .limit(FINAL_LIMIT)
                    .toList();
            return new LatestTrailerResponse(1, enriched, enriched.size());
        });
    }

    private Mono<LatestTrailerItem> fetchVideoForItem(LatestTrailerItem item, String language) {
        if (item.mediaType() == MediaType.MOVIE) {
            return fetchMovieVideo(item, language);
        } else {
            return fetchTvVideo(item, language);
        }
    }

    private Mono<LatestTrailerItem> fetchMovieVideo(LatestTrailerItem item, String language) {
        return streamingPort.getMovieVideos(item.id(), language)
                .map(videos -> enrichWithVideo(item, videos))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch video for movie {}: {}", item.id(), e.getMessage());
                    return Mono.just(item);
                });
    }

    private Mono<LatestTrailerItem> fetchTvVideo(LatestTrailerItem item, String language) {
        return streamingPort.getTvDetails(item.id(), language)
                .flatMap(details -> fetchTvVideosChain(item, details, language))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch TV details for {}: {}", item.id(), e.getMessage());
                    return Mono.just(item);
                });
    }

    private Mono<LatestTrailerItem> fetchTvVideosChain(LatestTrailerItem item, TvSeriesDetails details, String language) {
        EpisodeInfo episodeInfo = getPreferredEpisode(details);

        if (episodeInfo == null) {
            log.debug("No episode info available for TV {}", item.id());
            return Mono.just(item);
        }

        return streamingPort.getTvSeasonEpisodeVideos(
                        item.id(),
                        episodeInfo.seasonNumber(),
                        episodeInfo.episodeNumber(),
                        language)
                .flatMap(videos -> {
                    if (!videos.isEmpty()) {
                        return Mono.just(enrichWithVideo(item, videos));
                    }
                    return fallbackToSeasonVideos(item, episodeInfo.seasonNumber(), language);
                })
                .switchIfEmpty(Mono.defer(() -> fallbackToSeasonVideos(item, episodeInfo.seasonNumber(), language)))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch TV videos for {}: {}", item.id(), e.getMessage());
                    return Mono.just(item);
                });
    }

    private EpisodeInfo getPreferredEpisode(TvSeriesDetails details) {
        if (details.nextEpisodeToAir() != null) {
            return details.nextEpisodeToAir();
        }
        if (details.lastEpisodeToAir() != null) {
            return details.lastEpisodeToAir();
        }
        return null;
    }

    private Mono<LatestTrailerItem> fallbackToSeasonVideos(LatestTrailerItem item, Integer seasonNumber, String language) {
        return streamingPort.getTvSeasonVideos(item.id(), seasonNumber, language)
                .map(videos -> enrichWithVideo(item, videos))
                .switchIfEmpty(Mono.just(item));
    }

    private LatestTrailerItem enrichWithVideo(LatestTrailerItem item, List<VideoItem> videos) {
        Optional<VideoItem> bestVideo = videos.stream()
                .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
                .filter(v -> v.official() != null && v.official())
                .filter(v -> {
                    String type = v.type();
                    return "Trailer".equals(type) || "Teaser".equals(type) || "Featurette".equals(type);
                }).min(Comparator.comparingInt(v -> {
                    String type = v.type();
                    if ("Trailer".equals(type)) return 0;
                    if ("Teaser".equals(type)) return 1;
                    return 2;
                }));

        if (bestVideo.isPresent()) {
            VideoItem video = bestVideo.get();
            return new LatestTrailerItem(
                    item.id(), item.title(), item.overview(), item.posterPath(), item.backdropPath(),
                    item.popularity(), item.voteAverage(), item.voteCount(), item.releaseDate(),
                    item.originalTitle(), item.originalLanguage(), item.genreIds(), item.mediaType(),
                    item.originCountry(),
                    video.key(), video.site(), video.type(), video.official(),
                    null, null
            );
        }

        return item;
    }
}
