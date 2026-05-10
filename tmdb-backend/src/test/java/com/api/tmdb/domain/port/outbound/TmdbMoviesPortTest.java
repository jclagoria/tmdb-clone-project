package com.api.tmdb.domain.port.outbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TmdbMoviesPortTest {

    @Test
    void tmdbMoviesPort_shouldHave3Methods() {
        var methods = TmdbMoviesPort.class.getDeclaredMethods();
        assertEquals(3, methods.length);
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
    void tmdbMoviesPort_shouldHaveGetMovieVideosMethod() throws NoSuchMethodException {
        Method method = TmdbMoviesPort.class.getDeclaredMethod(
                "getMovieVideos", Integer.class, String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
    }
}