package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.contracts.Searchable;
import com.airtribe.meditrack.exception.InvalidDataException;

import java.util.ArrayList;
import java.util.List;

/**
 * Cloneable is implemented deliberately: {@link #clone()} performs a deep
 * copy of the mutable {@code medicalHistory} list so a cloned patient never
 * shares history entries with the original (see Design_Decisions.md).
 */
public class Patient extends Person implements Cloneable, Searchable<Patient> {

    private static final long serialVersionUID = 1L;

    private String bloodGroup;
    private List<String> medicalHistory;

    public Patient(String id, String name, int age, String gender, String contactNumber, String email,
                   String bloodGroup) throws InvalidDataException {
        super(id, name, age, gender, contactNumber, email);
        this.bloodGroup = bloodGroup;
        this.medicalHistory = new ArrayList<>();
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public List<String> getMedicalHistory() {
        return medicalHistory;
    }

    public void addMedicalHistoryEntry(String entry) {
        medicalHistory.add(entry);
    }

    @Override
    public String getRole() {
        return "Patient";
    }

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) {
            return false;
        }
        String q = query.trim().toLowerCase();
        return getName().toLowerCase().contains(q) || getId().equalsIgnoreCase(query.trim());
    }

    /**
     * Deep copy: the returned Patient has its own independent medicalHistory
     * list, so mutating the clone's history never affects the original.
     */
    @Override
    public Patient clone() {
        try {
            Patient cloned = (Patient) super.clone();
            cloned.medicalHistory = new ArrayList<>(this.medicalHistory);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Patient implements Cloneable; this cannot happen", e);
        }
    }

    @Override
    public String toString() {
        return describe() + ", Blood Group: " + bloodGroup + ", History entries: " + medicalHistory.size();
    }
}
