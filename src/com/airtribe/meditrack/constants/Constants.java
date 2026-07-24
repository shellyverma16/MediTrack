package com.airtribe.meditrack.constants;

import java.io.File;

/**
 * Application-wide configuration constants.
 * Final class with a private constructor: this is a non-instantiable utility holder.
 */
public final class Constants {

    public static final double TAX_RATE = 0.18;

    public static final String DATA_DIR = "data";
    public static final String PATIENTS_FILE = DATA_DIR + File.separator + "patients.csv";
    public static final String DOCTORS_FILE = DATA_DIR + File.separator + "doctors.csv";
    public static final String APPOINTMENTS_FILE = DATA_DIR + File.separator + "appointments.ser";

    public static final String LOAD_DATA_FLAG = "--loadData";

    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 120;

    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private Constants() {
    }
}
