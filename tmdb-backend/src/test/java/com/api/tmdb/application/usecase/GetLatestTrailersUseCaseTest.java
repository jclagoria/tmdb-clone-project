package com.api.tmdb.application.usecase;

import com.api.tmdb.domain.model.LatestTrailerItem;
import com.api.tmdb.domain.model.LatestTrailerResponse;
import com.api.tmdb.domain.model.VideoItem;
import com.api.tmdb.domain.model.enums.MediaType;
import com.api.tmdb.domain.port.outbound.TmdbMoviesPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLatestTrailersUseCaseTest {

    @Mock
    private TmdbMoviesPort tmdbMoviesPort;

    private GetLatestTrailersUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetLatestTrailersUseCase(tmdbMoviesPort);
    }

    @Test
    void getLatestTrailers_shouldCallTmdbMoviesPort() {
        LatestTrailerItem item = new LatestTrailerItem(
                1, "Test Movie", "Overview", null, null, 10.0, 8.0, 100,
                "2024-01-01", "Test", "en", null, MediaType.MOVIE, null,
                null, null, null, null, null, null
        );
        LatestTrailerResponse mockResponse = new LatestTrailerResponse(1, List.of(item), 1);
        when(tmdbMoviesPort.getMostPopularMovies(eq("en-US"), eq(1)))
                .thenReturn(Mono.just(mockResponse));
        when(tmdbMoviesPort.getUpcomingMovies(eq("en-US"), eq(1), any(), any()))
                .thenReturn(Mono.just(mockResponse));
        when(tmdbMoviesPort.getMovieVideos(eq(1), any()))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(useCase.getLatestTrailers("en-US"))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.page());
                })
                .verifyComplete();
    }

    @Test
    void getLatestTrailers_shouldUseDefaultLanguage_whenNull() {
        LatestTrailerResponse mockResponse = new LatestTrailerResponse(1, List.of(), 0);
        when(tmdbMoviesPort.getMostPopularMovies(eq("en-US"), any()))
                .thenReturn(Mono.just(mockResponse));
        when(tmdbMoviesPort.getUpcomingMovies(eq("en-US"), any(), any(), any()))
                .thenReturn(Mono.just(mockResponse));

        StepVerifier.create(useCase.getLatestTrailers(null))
                .verifyComplete();
    }

    @Test
    void getLatestTrailers_shouldEnrichWithVideos() {
        LatestTrailerItem item = new LatestTrailerItem(
                1, "Test Movie", "Overview", null, null, 10.0, 8.0, 100,
                "2024-01-01", "Test", "en", null, MediaType.MOVIE, null,
                null, null, null, null, null, null
        );
        LatestTrailerResponse mockResponse = new LatestTrailerResponse(1, List.of(item), 1);
        VideoItem video = new VideoItem("id123", "abc123", "YouTube", "Trailer", true);
        when(tmdbMoviesPort.getMostPopularMovies(any(), any()))
                .thenReturn(Mono.just(mockResponse));
        when(tmdbMoviesPort.getUpcomingMovies(any(), any(), any(), any()))
                .thenReturn(Mono.just(mockResponse));
        when(tmdbMoviesPort.getMovieVideos(eq(1), any()))
                .thenReturn(Mono.just(List.of(video)));

        StepVerifier.create(useCase.getLatestTrailers("en-US"))
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(1, response.results().size());
                    LatestTrailerItem result = response.results().get(0);
                    assertEquals("abc123", result.videoKey());
                })
                .verifyComplete();
    }

    @Test
    void getLatestTrailers_shouldMergeAndSortByPopularity() {
        LatestTrailerItem item1 = new LatestTrailerItem(
                1, "Popular Movie", "Overview", null, null, 100.0, 8.0, 100,
                "2024-01-01", "Test", "en", null, MediaType.MOVIE, null,
                null, null, null, null, null, null
        );
        LatestTrailerItem item2 = new LatestTrailerItem(
                2, "Less Popular Movie", "Overview", null, null, 50.0, 7.0, 50,
                "2024-01-01", "Test", "en", null, MediaType.MOVIE, null,
                null, null, null, null, null, null
        );
        LatestTrailerResponse popularResponse = new LatestTrailerResponse(1, List.of(item1), 1);
        LatestTrailerResponse upcomingResponse = new LatestTrailerResponse(1, List.of(item2), 1);

        when(tmdbMoviesPort.getMostPopularMovies(any(), any()))
                .thenReturn(Mono.just(popularResponse));
        when(tmdbMoviesPort.getUpcomingMovies(any(), any(), any(), any()))
                .thenReturn(Mono.just(upcomingResponse));
        when(tmdbMoviesPort.getMovieVideos(any(), any()))
                .thenReturn(Mono.just(List.of()));

        StepVerifier.create(useCase.getLatestTrailers("en-US"))
                .assertNext(response -> {
                    assertEquals(2, response.results().size());
                    assertEquals("Popular Movie", response.results().get(0).title());
                    assertEquals("Less Popular Movie", response.results().get(1).title());
                })
                .verifyComplete();
    }
}