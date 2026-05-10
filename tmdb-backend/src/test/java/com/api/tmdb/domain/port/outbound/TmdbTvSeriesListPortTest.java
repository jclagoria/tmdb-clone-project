package com.api.tmdb.domain.port.outbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TmdbTvSeriesListPortTest {

    @Test
    void tmdbTvSeriesListPort_shouldHave4Methods() {
        var methods = TmdbTvSeriesListPort.class.getDeclaredMethods();
        assertEquals(4, methods.length);
    }

    @Test
    void tmdbTvSeriesListPort_shouldHaveGetAiringTodayMethod() throws NoSuchMethodException {
        Method method = TmdbTvSeriesListPort.class.getDeclaredMethod(
                "getAiringToday", String.class, Integer.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbTvSeriesListPort_shouldHaveGetOnTheAirMethod() throws NoSuchMethodException {
        Method method = TmdbTvSeriesListPort.class.getDeclaredMethod(
                "getOnTheAir", String.class, Integer.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbTvSeriesListPort_shouldHaveGetPopularMethod() throws NoSuchMethodException {
        Method method = TmdbTvSeriesListPort.class.getDeclaredMethod(
                "getPopular", String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tmdbTvSeriesListPort_shouldHaveGetTopRatedMethod() throws NoSuchMethodException {
        Method method = TmdbTvSeriesListPort.class.getDeclaredMethod(
                "getTopRated", String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }
}