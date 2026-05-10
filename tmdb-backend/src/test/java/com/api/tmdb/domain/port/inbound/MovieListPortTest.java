package com.api.tmdb.domain.port.inbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieListPortTest {

    @Test
    void movieListPort_shouldHave4Methods() {
        var methods = MovieListPort.class.getDeclaredMethods();
        assertEquals(4, methods.length);
    }

    @Test
    void movieListPort_shouldHaveGetNowPlayingMethod() throws NoSuchMethodException {
        Method method = MovieListPort.class.getDeclaredMethod(
                "getNowPlaying", String.class, String.class, Integer.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void movieListPort_shouldHaveGetPopularMethod() throws NoSuchMethodException {
        Method method = MovieListPort.class.getDeclaredMethod(
                "getPopular", String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void movieListPort_shouldHaveGetTopRatedMethod() throws NoSuchMethodException {
        Method method = MovieListPort.class.getDeclaredMethod(
                "getTopRated", String.class, String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void movieListPort_shouldHaveGetUpcomingMethod() throws NoSuchMethodException {
        Method method = MovieListPort.class.getDeclaredMethod(
                "getUpcoming", String.class, Integer.class, String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }
}