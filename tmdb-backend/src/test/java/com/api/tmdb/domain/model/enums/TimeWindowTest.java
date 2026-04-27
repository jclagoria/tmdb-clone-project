package com.api.tmdb.domain.model.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TimeWindowTest {

    @ParameterizedTest
    @ValueSource(strings = {"day", "DAY", "Day"})
    void fromValue_shouldReturnDay_forValidInput(String value) {
        TimeWindow result = TimeWindow.fromValue(value);
        assertEquals(TimeWindow.DAY, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"week", "WEEK", "Week"})
    void fromValue_shouldReturnWeek_forValidInput(String value) {
        TimeWindow result = TimeWindow.fromValue(value);
        assertEquals(TimeWindow.WEEK, result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"invalid", "foo", "MONTH"})
    void fromValue_shouldThrowIllegalArgumentException_forInvalidInput(String value) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> TimeWindow.fromValue(value)
        );
        assertTrue(exception.getMessage().contains("Unknown time window"));
    }

    @Test
    void getValue_shouldReturnCorrectValue() {
        assertEquals("day", TimeWindow.DAY.getValue());
        assertEquals("week", TimeWindow.WEEK.getValue());
    }
}