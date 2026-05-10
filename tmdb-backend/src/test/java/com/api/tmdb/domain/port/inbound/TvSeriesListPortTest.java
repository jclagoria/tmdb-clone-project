package com.api.tmdb.domain.port.inbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TvSeriesListPortTest {

    @Test
    void tvSeriesListPort_shouldHave4Methods() {
        var methods = TvSeriesListPort.class.getDeclaredMethods();
        assertEquals(4, methods.length);
    }

    @Test
    void tvSeriesListPort_shouldHaveGetAiringTodayMethod() throws NoSuchMethodException {
        Method method = TvSeriesListPort.class.getDeclaredMethod(
                "getAiringToday", String.class, Integer.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tvSeriesListPort_shouldHaveGetOnTheAirMethod() throws NoSuchMethodException {
        Method method = TvSeriesListPort.class.getDeclaredMethod(
                "getOnTheAir", String.class, Integer.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tvSeriesListPort_shouldHaveGetPopularMethod() throws NoSuchMethodException {
        Method method = TvSeriesListPort.class.getDeclaredMethod(
                "getPopular", String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void tvSeriesListPort_shouldHaveGetTopRatedMethod() throws NoSuchMethodException {
        Method method = TvSeriesListPort.class.getDeclaredMethod(
                "getTopRated", String.class, Integer.class);
        assertEquals(Mono.class, method.getReturnType());
    }
}