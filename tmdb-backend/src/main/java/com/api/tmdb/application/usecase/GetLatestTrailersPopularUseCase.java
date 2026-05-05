package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.port.outbound.TmdbLatestTrailerMoviePort;
import com.api.tmdb.application.cache.CacheService;
import com.api.tmdb.domain.model.*;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.outbound.LatestTrailersPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class GetLatestTrailersPopularUseCase implements LatestTrailersPort {

    private static final Logger log = LoggerFactory.getLogger(GetLatestTrailersPopularUseCase.class);
    private static final int TAKE = 10;
    private static final int FINAL_LIMIT = 20;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    private final TmdbLatestTrailerMoviePort moviePort;
    private final CacheService cacheService;

    public GetLatestTrailersPopularUseCase(TmdbLatestTrailerMoviePort moviePort,
                                           CacheService cacheService) {
        this.moviePort = moviePort;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<LatestTrailerResponse> getPopular(String language) {
        String lang = (language == null || language.isBlank()) ? "en-US" : language;
        String cacheKey = "latestTrailers:popular:" + lang;

        return cacheService.get(cacheKey, LatestTrailerResponse.class)
                .flatMap(cached -> Mono.just(cached))
                .switchIfEmpty(Mono.defer(() -> fetchAndCache(lang, cacheKey)))
                .doOnError(error -> log.error("GetLatestTrailersPopularUseCase failed: {}", error.getMessage(), error));
    }

    private Mono<LatestTrailerResponse> fetchAndCache(String language, String cacheKey) {
        LocalDate today = LocalDate.now();
        LocalDate thursdayGte = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY));
        LocalDate lte = thursdayGte.plusDays(27);

        log.debug("Fetching latest trailers: gte={}, lte={}", thursdayGte, lte);

        Mono<List<LatestTrailerItem>> popularMoviesMono = moviePort.getMostPopularMovies(language,1)
                .map(response -> processItems(response.results(), MediaType.MOVIE, TAKE));
        Mono<List<LatestTrailerItem>> upcomingMoviesMono = moviePort.getUpcomingMovies(language, 1,
                        thursdayGte.toString(), lte.toString())
                .map(response -> processItems(response.results(), MediaType.MOVIE, TAKE));

        return Mono.zip(popularMoviesMono, upcomingMoviesMono)
                .flatMap(tuple -> {
                    List<LatestTrailerItem> merged = new ArrayList<>();
                    merged.addAll(tuple.getT1());
                    merged.addAll(tuple.getT2());

                    List<LatestTrailerItem> deduplicated = duplicate(merged);
                    List<LatestTrailerItem> sorted = deduplicated.stream()
                            .sorted(Comparator.comparingDouble(LatestTrailerItem::popularity).reversed())
                            .limit(FINAL_LIMIT)
                            .toList();

                    return fetchVideos(sorted,language);
                }).flatMap(response ->
                        cacheService.set(cacheKey, response, CACHE_TTL)
                                .thenReturn(response)
                );
    }

    private List<LatestTrailerItem> processItems(
            List<LatestTrailerItem> items,
            MediaType mediaType,
            int limit) {
        return items.stream().limit(limit)
                .map(
                        item -> new LatestTrailerItem(
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

    private List<LatestTrailerItem> duplicate(List<LatestTrailerItem> items) {
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
                    .toList();
            return new LatestTrailerResponse(1, enriched, enriched.size());
        });
    }

    private Mono<LatestTrailerItem> fetchVideoForItem(LatestTrailerItem item, String language) {
        return moviePort.getMovieVideos(item.id(), language)
                .map(videos -> enrichWithVideo(item, videos))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch video for movie {}: {}", item.id(), e.getMessage());
                    return Mono.just(item);
                });
    }

    private LatestTrailerItem enrichWithVideo(LatestTrailerItem item, List<VideoItem> videos) {
        Optional<VideoItem> bestVideo = videos.stream()
                .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
                .filter(v -> v.official() != null && v.official())
                .filter(v -> {
                    String type = v.type();
                    return "Trailer".equals(type) || "Teaser".equals(type) || "Featurette".equals(type);
                })
                .sorted(Comparator.comparingInt(v ->{
                    String type = v.type();
                    if ("Trailer".equals(type)) return 0;
                    if ("Teaser".equals(type)) return 1;
                    return 2;
                }))
                .findFirst();

        if (bestVideo.isPresent()) {
            VideoItem video = bestVideo.get();
            return new LatestTrailerItem(
                    item.id(), item.title(), item.overview(), item.posterPath(), item.backdropPath(),
                    item.popularity(), item.voteAverage(), item.voteCount(), item.releaseDate(),
                    item.originalTitle(), item.originalLanguage(), item.genreIds(), item.mediaType(),
                    item.originCountry(),
                    video.key(), video.site(), video.type(), video.official(),
                    item.nextSeasonNumber(), item.nextEpisodeNumber()
            );
        }

        return item;
    }

}
