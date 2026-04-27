package com.api.tmdb.domain.exception;

public class TmdbServiceException extends RuntimeException {

    private final int statusCode;

    public TmdbServiceException(String message) {
        super(message);
        this.statusCode = 503;
    }

    public TmdbServiceException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public TmdbServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 503;
    }

    public int getStatusCode() {
        return statusCode;
    }
}