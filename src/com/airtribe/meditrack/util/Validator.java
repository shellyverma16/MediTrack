package com.airtribe.meditrack.util;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.exception.InvalidDataException;

import java.util.regex.Pattern;

/**
 * Centralized validation so entity setters never duplicate rule logic.
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    private Validator() {
    }

    public static void validateNonEmpty(String value, String fieldName) throws InvalidDataException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName + " must not be empty");
        }
    }

    public static void validateAge(int age) throws InvalidDataException {
        if (age < Constants.MIN_AGE || age > Constants.MAX_AGE) {
            throw new InvalidDataException("Age must be between " + Constants.MIN_AGE + " and " + Constants.MAX_AGE);
        }
    }

    public static void validateEmail(String email) throws InvalidDataException {
        validateNonEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidDataException("Email is not a valid address: " + email);
        }
    }

    public static void validatePhone(String phone) throws InvalidDataException {
        validateNonEmpty(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidDataException("Phone number must be exactly 10 digits: " + phone);
        }
    }

    public static void validatePositive(double value, String fieldName) throws InvalidDataException {
        if (value <= 0) {
            throw new InvalidDataException(fieldName + " must be a positive value");
        }
    }
}
