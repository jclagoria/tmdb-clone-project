package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.LatestTrailerItem;
import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.inbound.MoviesPort;
import com.api.tmdb.domain.port.outbound.TmdbMoviesPort;
import com.api.tmdb.infrastructure.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class GetLatestTrailersUseCase implements MoviesPort {

    private static final Logger log = LoggerFactory.getLogger(GetLatestTrailersUseCase.class);
    private static final int TAKE = 10;
    private static final int FINAL_LIMIT = 20;

    private final TmdbMoviesPort tmdbMoviesPort;

    public GetLatestTrailersUseCase(TmdbMoviesPort tmdbMoviesPort) {
        this.tmdbMoviesPort = tmdbMoviesPort;
    }

    @Override
    @Cacheable(
            key = "'latestTrailers:' + #language",
            type = LatestTrailerResponse.class,
            ttlMinutes = 30
    )
    public Mono<LatestTrailerResponse> getLatestTrailers(String language) {
        String effectiveLanguage = (language == null || language.isBlank()) ? "en-US" : language;

        log.debug("Executing GetLatestTrailersUseCase: language={}", effectiveLanguage);

        LocalDate today = LocalDate.now();
        LocalDate thursdayGte = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY));
        LocalDate lte = thursdayGte.plusDays(27);

        log.debug("Fetching latest trailers: gte={}, lte={}", thursdayGte, lte);

        Mono<List<LatestTrailerItem>> popularMoviesMono = tmdbMoviesPort
                .getMostPopularMovies(effectiveLanguage, 1)
                .map(response -> processItems(response.results(), MediaType.MOVIE, TAKE));

        Mono<List<LatestTrailerItem>> upcomingMoviesMono = tmdbMoviesPort
                .getUpcomingMovies(effectiveLanguage, 1, thursdayGte.toString(), lte.toString())
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

                    return fetchVideos(sorted, effectiveLanguage);
                })
                .doOnSuccess(response -> log.debug("GetLatestTrailersUseCase completed: totalResults={}",
                        response != null ? response.totalResults() : 0));
    }

    private List<LatestTrailerItem> processItems(
            List<LatestTrailerItem> items,
            MediaType mediaType,
            int limit) {
        return items.stream().limit(limit)
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
        return tmdbMoviesPort.getMovieVideos(item.id(), language)
                .map(videos -> enrichWithVideo(item, videos))
                .onErrorResume(e -> {
                    log.warn("Failed to fetch video for movie {}: {}", item.id(), e.getMessage());
                    return Mono.just(item);
                });
    }

    private LatestTrailerItem enrichWithVideo(LatestTrailerItem item, List<VideoItem> videos) {
        if (videos == null || videos.isEmpty()) {
            return item;
        }

        Optional<VideoItem> bestVideo = videos.stream()
                .filter(v -> "YouTube".equalsIgnoreCase(v.site()))
                .filter(v -> {
                    String type = v.type();
                    return "Trailer".equals(type) || "Teaser".equals(type) || "Featurette".equals(type);
                })
                .sorted(Comparator.comparingInt(v -> {
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