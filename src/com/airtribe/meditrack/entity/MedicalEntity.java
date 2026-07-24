package com.airtribe.meditrack.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Common behavior shared by every domain entity: an immutable identity,
 * a creation timestamp, and an application-wide instance counter.
 */
public abstract class MedicalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private static int totalEntitiesCreated;

    static {
        totalEntitiesCreated = 0;
    }

    private final String id;
    private final LocalDateTime createdAt;

    protected MedicalEntity(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        totalEntitiesCreated++;
    }

    public static int getTotalEntitiesCreated() {
        return totalEntitiesCreated;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public abstract String describe();
}
