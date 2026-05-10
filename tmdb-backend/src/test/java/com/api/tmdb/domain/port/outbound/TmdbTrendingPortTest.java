package com.api.tmdb.domain.port.outbound;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TmdbTrendingPortTest {
    
    @Test
    void tmdbTrendingPort_shouldHaveCorrectMethodSignatures() {
        var methods = TmdbTrendingPort.class.getDeclaredMethods();
        assertEquals(1, methods.length);
    }

    @Test
    void tmdbTrendingPort_shouldHaveGetTrendingMethod() throws NoSuchMethodException {
        Method method = TmdbTrendingPort.class.getMethod(
            "getTrending", 
            com.api.tmdb.domain.model.enums.TimeWindow.class, 
            String.class
        );
        assertNotNull(method);
    }
}