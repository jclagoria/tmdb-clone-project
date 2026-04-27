package com.api.tmdb.domain.model.enums;

public enum MediaType {
    MOVIE("movie"),
    TV("tv"),
    PERSON("person");

    private final String value;

    MediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MediaType fromValue(String value) {
        for (MediaType mediaType : MediaType.values()) {
            if (mediaType.value.equalsIgnoreCase(value)) {
                return mediaType;
            }
        }

        throw new IllegalArgumentException("Unknown media type: " + value);
    }
}
