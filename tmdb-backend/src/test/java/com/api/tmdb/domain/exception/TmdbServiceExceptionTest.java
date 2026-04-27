package com.api.tmdb.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TmdbServiceExceptionTest {

    @Test
    void constructor_withMessage_shouldSetDefaultStatusCode() {
        TmdbServiceException exception = new TmdbServiceException("Service unavailable");
        
        assertEquals("Service unavailable", exception.getMessage());
        assertEquals(503, exception.getStatusCode());
    }

    @Test
    void constructor_withMessageAndStatusCode_shouldSetStatusCode() {
        TmdbServiceException exception = new TmdbServiceException("Rate limit exceeded", 429);
        
        assertEquals("Rate limit exceeded", exception.getMessage());
        assertEquals(429, exception.getStatusCode());
    }

    @Test
    void constructor_withMessageAndCause_shouldSetValues() {
        RuntimeException cause = new RuntimeException("Original error");
        TmdbServiceException exception = new TmdbServiceException("Fallback error", cause);
        
        assertEquals("Fallback error", exception.getMessage());
        assertEquals(503, exception.getStatusCode());
        assertSame(cause, exception.getCause());
    }
}