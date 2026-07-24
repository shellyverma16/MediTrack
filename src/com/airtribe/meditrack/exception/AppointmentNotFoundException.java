package com.airtribe.meditrack.exception;

/**
 * Thrown when an appointment lookup by ID fails to find a match.
 */
public class AppointmentNotFoundException extends Exception {

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
