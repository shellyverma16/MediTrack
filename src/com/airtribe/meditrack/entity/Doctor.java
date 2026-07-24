package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.contracts.Searchable;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.Validator;

public class Doctor extends Person implements Searchable<Doctor> {

    private static final long serialVersionUID = 1L;

    private Specialization specialization;
    private double consultationFee;
    private boolean available;

    public Doctor(String id, String name, int age, String gender, String contactNumber, String email,
                  Specialization specialization, double consultationFee) throws InvalidDataException {
        super(id, name, age, gender, contactNumber, email);
        this.specialization = specialization;
        setConsultationFee(consultationFee);
        this.available = true;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) throws InvalidDataException {
        Validator.validatePositive(consultationFee, "Consultation fee");
        this.consultationFee = consultationFee;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String getRole() {
        return "Doctor";
    }

    @Override
    public boolean matches(String query) {
        if (query == null || query.isBlank()) {
            return false;
        }
        String q = query.trim().toLowerCase();
        return getName().toLowerCase().contains(q)
                || getId().equalsIgnoreCase(query.trim())
                || specialization.name().toLowerCase().contains(q);
    }

    @Override
    public String toString() {
        return describe() + ", Specialization: " + specialization + ", Fee: " + consultationFee
                + ", Available: " + available;
    }
}
