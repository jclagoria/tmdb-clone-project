package com.api.tmdb.domain.port.inbound;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TrendingPortTest {
    
    @Test
    void trendingPort_shouldHaveCorrectMethodSignatures() {
        var methods = TrendingPort.class.getDeclaredMethods();
        assertEquals(1, methods.length);
    }

    @Test
    void trendingPort_shouldHaveGetTrendingMethod() throws NoSuchMethodException {
        Method method = TrendingPort.class.getMethod(
            "getTrending", 
            com.api.tmdb.domain.model.enums.TimeWindow.class, 
            String.class
        );
        assertNotNull(method);
    }
}