package com.airtribe.meditrack.util;

/**
 * Lazily-initialized singleton (Bonus B) responsible for generating
 * human-readable, monotonically increasing IDs for each entity type.
 * Contrast with {@link AppConfig}, which is an eager singleton.
 */
public final class IdGenerator {

    private static IdGenerator instance;

    private int patientCounter;
    private int doctorCounter;
    private int appointmentCounter;
    private int billCounter;

    private IdGenerator() {
        patientCounter = 0;
        doctorCounter = 0;
        appointmentCounter = 0;
        billCounter = 0;
    }

    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    public synchronized String nextPatientId() {
        patientCounter++;
        return String.format("PAT%04d", patientCounter);
    }

    public synchronized String nextDoctorId() {
        doctorCounter++;
        return String.format("DOC%04d", doctorCounter);
    }

    public synchronized String nextAppointmentId() {
        appointmentCounter++;
        return String.format("APT%04d", appointmentCounter);
    }

    public synchronized String nextBillId() {
        billCounter++;
        return String.format("BIL%04d", billCounter);
    }

    /**
     * Fast-forwards a counter after persisted data is loaded, so freshly
     * generated IDs never collide with IDs restored from disk.
     */
    public synchronized void fastForward(String prefix, int highestSeen) {
        switch (prefix) {
            case "PAT" -> patientCounter = Math.max(patientCounter, highestSeen);
            case "DOC" -> doctorCounter = Math.max(doctorCounter, highestSeen);
            case "APT" -> appointmentCounter = Math.max(appointmentCounter, highestSeen);
            case "BIL" -> billCounter = Math.max(billCounter, highestSeen);
            default -> throw new IllegalArgumentException("Unknown ID prefix: " + prefix);
        }
    }
}
