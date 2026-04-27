package com.api.tmdb.domain.model.enums;

public enum TimeWindow {
    DAY("day"),
    WEEK("week");

    private final String value;

    TimeWindow(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TimeWindow fromValue(String value) {
        for (TimeWindow timeWindow : TimeWindow.values()) {
            if (timeWindow.value.equalsIgnoreCase(value)) {
                return timeWindow;
            }
        }

        throw new IllegalArgumentException("Unknown time window: " + value);
    }
}
