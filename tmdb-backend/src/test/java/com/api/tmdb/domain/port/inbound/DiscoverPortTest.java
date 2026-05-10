package com.api.tmdb.domain.port.inbound;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class DiscoverPortTest {
    
    @Test
    void discoverPort_shouldHaveCorrectMethodSignatures() {
        var methods = DiscoverPort.class.getDeclaredMethods();
        assertEquals(4, methods.length);
    }

    @Test
    void discoverPort_shouldHaveDiscoverMoviesMethod() throws NoSuchMethodException {
        Method method = DiscoverPort.class.getMethod("discoverMovies", com.api.tmdb.domain.model.DiscoverParams.class);
        assertNotNull(method);
    }

    @Test
    void discoverPort_shouldHaveDiscoverTvMethod() throws NoSuchMethodException {
        Method method = DiscoverPort.class.getMethod("discoverTv", com.api.tmdb.domain.model.DiscoverParams.class);
        assertNotNull(method);
    }

    @Test
    void discoverPort_shouldHaveGetWhatsPopularMethod() throws NoSuchMethodException {
        Method method = DiscoverPort.class.getMethod("getWhatsPopular", String.class, String.class, Integer.class);
        assertNotNull(method);
    }

    @Test
    void discoverPort_shouldHaveGetForRentMethod() throws NoSuchMethodException {
        Method method = DiscoverPort.class.getMethod("getForRent", String.class, String.class, Integer.class);
        assertNotNull(method);
    }
}