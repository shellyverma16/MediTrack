package com.airtribe.meditrack.exception;

/**
 * Thrown by Validator and entity setters when data fails validation rules.
 */
public class InvalidDataException extends Exception {

    public InvalidDataException(String message) {
        super(message);
    }
}
