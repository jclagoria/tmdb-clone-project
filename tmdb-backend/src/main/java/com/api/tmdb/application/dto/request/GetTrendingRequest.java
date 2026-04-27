package com.api.tmdb.application.dto.request;

import com.api.tmdb.domain.model.enums.TimeWindow;

public record GetTrendingRequest(
        TimeWindow timeWindow,
        String language
) {
    public GetTrendingRequest {
        if (language == null || language.isBlank()) {
            language = "en-US";
        }
    }

    public static GetTrendingRequest of(TimeWindow timeWindow, String language) {
        return new GetTrendingRequest(timeWindow, language != null ? language : "en-US");
    }
}
