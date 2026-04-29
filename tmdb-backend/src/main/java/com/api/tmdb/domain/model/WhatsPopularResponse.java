package com.api.tmdb.domain.model;

import java.util.List;

public record WhatsPopularResponse(
        Integer page,
        List<WhatsPopularItem> results,
        Integer totalResults
) {
}
