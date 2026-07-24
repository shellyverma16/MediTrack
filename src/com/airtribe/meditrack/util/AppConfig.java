package com.airtribe.meditrack.util;

/**
 * Eagerly-initialized singleton (Bonus B) holding application configuration.
 * Contrast with {@link IdGenerator}, which is a lazy singleton.
 */
public final class AppConfig {

    public static final AppConfig INSTANCE = new AppConfig();

    private final String appName = "MediTrack";
    private final String version = "1.0.0";

    private AppConfig() {
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }
}
