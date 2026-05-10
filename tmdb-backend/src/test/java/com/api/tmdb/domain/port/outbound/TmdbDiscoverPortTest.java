package com.api.tmdb.domain.port.outbound;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TmdbDiscoverPortTest {
    
    @Test
    void tmdbDiscoverPort_shouldHaveCorrectMethodSignatures() {
        var methods = TmdbDiscoverPort.class.getDeclaredMethods();
        assertEquals(2, methods.length);
    }

    @Test
    void tmdbDiscoverPort_shouldHaveDiscoverMoviesMethod() throws NoSuchMethodException {
        Method method = TmdbDiscoverPort.class.getMethod("discoverMovies", com.api.tmdb.domain.model.DiscoverParams.class);
        assertNotNull(method);
    }

    @Test
    void tmdbDiscoverPort_shouldHaveDiscoverTvMethod() throws NoSuchMethodException {
        Method method = TmdbDiscoverPort.class.getMethod("discoverTv", com.api.tmdb.domain.model.DiscoverParams.class);
        assertNotNull(method);
    }
}