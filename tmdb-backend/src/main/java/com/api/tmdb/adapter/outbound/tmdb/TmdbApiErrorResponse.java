package com.api.tmdb.adapter.outbound.tmdb;

public record TmdbApiErrorResponse(
        Boolean success,
        Integer statusCode,
        String statusMessage
) {

    public boolean isSuccess() {
        return success != null && success;
    }
}
