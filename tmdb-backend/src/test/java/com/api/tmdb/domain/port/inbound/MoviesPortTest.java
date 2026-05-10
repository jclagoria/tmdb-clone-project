package com.api.tmdb.domain.port.inbound;

import reactor.core.publisher.Mono;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoviesPortTest {

    @Test
    void moviesPort_shouldHave1Method() {
        var methods = MoviesPort.class.getDeclaredMethods();
        assertEquals(1, methods.length);
    }

    @Test
    void moviesPort_shouldHaveGetLatestTrailersMethod() throws NoSuchMethodException {
        Method method = MoviesPort.class.getDeclaredMethod(
                "getLatestTrailers", String.class);
        assertTrue(method.getReturnType().getName().contains("Mono"));
        assertEquals(Mono.class, method.getReturnType());
    }
}