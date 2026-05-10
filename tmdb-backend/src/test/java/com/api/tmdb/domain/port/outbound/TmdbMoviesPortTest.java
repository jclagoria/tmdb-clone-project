package com.api.tmdb.domain.port.outbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TmdbMoviesPortTest {

    @Test
    void tmdbMoviesPort_shouldHave9Methods() {
        var methods = TmdbMoviesPort.class.getDeclaredMethods();
        assertEquals(9, methods.length);
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetMostPopularMoviesMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getMostPopularMovies", String.class, Integer.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetUpcomingMoviesMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getUpcomingMovies", String.class, Integer.class, String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetNowPlayingMoviesMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getNowPlayingMovies", String.class, Integer.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetMovieVideosMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getMovieVideos", Integer.class, String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
    }

    @Test
    void tmdbMoviesPort_shouldHaveDiscoverMoviesMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "discoverMovies", String.class, Integer.class, String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMoviesPort_shouldHaveDiscoverTvShowsMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "discoverTvShows", String.class, Integer.class, String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetTvDetailsMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getTvDetails", Integer.class, String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetTvSeasonVideosMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getTvSeasonVideos", Integer.class, Integer.class, String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
    }

    @Test
    void tmdbMoviesPort_shouldHaveGetTvEpisodeVideosMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getTvEpisodeVideos", Integer.class, Integer.class, Integer.class, String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
    }
}