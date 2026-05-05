package com.api.tmdb.domain.model;

public record VideoItem(
        String id,
        String key,
        String site,
        String type,
        Boolean official
) {
}
