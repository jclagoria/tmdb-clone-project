package com.api.tmdb.domain.port.outbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TmdbMovieListPortTest {

    @Test
    void tmdbMovieListPort_shouldHave4Methods() {
        var methods = TmdbMovieListPort.class.getDeclaredMethods();
        assertEquals(4, methods.length);
    }

    @Test
    void tmdbMovieListPort_shouldHaveGetNowPlayingMethod() throws NoSuchMethodException {
        Method method = TmdbMovieListPort.class.getDeclaredMethod(
                "getNowPlaying", String.class, String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMovieListPort_shouldHaveGetPopularMethod() throws NoSuchMethodException {
        Method method = TmdbMovieListPort.class.getDeclaredMethod(
                "getPopular", String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMovieListPort_shouldHaveGetTopRatedMethod() throws NoSuchMethodException {
        Method method = TmdbMovieListPort.class.getDeclaredMethod(
                "getTopRated", String.class, String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbMovieListPort_shouldHaveGetUpcomingMethod() throws NoSuchMethodException {
        Method method = TmdbMovieListPort.class.getDeclaredMethod(
                "getUpcoming", String.class, Integer.class, String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }
}