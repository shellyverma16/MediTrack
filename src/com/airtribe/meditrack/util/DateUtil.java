package com.airtribe.meditrack.util;

import com.airtribe.meditrack.exception.InvalidDataException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {

    public static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public static final DateTimeFormatter STORAGE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private DateUtil() {
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMAT);
    }

    public static String toStorageString(LocalDateTime dateTime) {
        return dateTime.format(STORAGE_FORMAT);
    }

    public static LocalDateTime fromStorageString(String value) throws InvalidDataException {
        try {
            return LocalDateTime.parse(value, STORAGE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Unparseable date/time: " + value);
        }
    }

    public static void validateFuture(LocalDateTime dateTime) throws InvalidDataException {
        if (dateTime == null || dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Appointment date/time must be in the future");
        }
    }
}
