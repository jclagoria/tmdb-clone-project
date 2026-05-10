package com.api.tmdb.domain.port.inbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoviesPortTest {

    @Test
    void moviesPort_shouldHave4Methods() {
        var methods = MoviesPort.class.getDeclaredMethods();
        assertEquals(4, methods.length);
    }

    @Test
    void moviesPort_shouldHaveGetLatestTrailersMethod() throws NoSuchMethodException {
        Method method = MoviesPort.class.getDeclaredMethod(
                "getLatestTrailers", String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void moviesPort_shouldHaveGetStreamingMethod() throws NoSuchMethodException {
        Method method = MoviesPort.class.getDeclaredMethod(
                "getStreaming", String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void moviesPort_shouldHaveGetForRentMethod() throws NoSuchMethodException {
        Method method = MoviesPort.class.getDeclaredMethod(
                "getForRent", String.class, String.class);
        assertEquals(Mono.class, method.getReturnType());
    }

    @Test
    void moviesPort_shouldHaveGetInTheatersMethod() throws NoSuchMethodException {
        Method method = MoviesPort.class.getDeclaredMethod(
                "getInTheaters", String.class);
        assertEquals(Mono.class, method.getReturnType());
    }
}