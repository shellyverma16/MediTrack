package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.AppointmentStatus;

import java.time.LocalDateTime;

/**
 * Cloneable is implemented to contrast two copy strategies on the same
 * object: {@link #clone()} deep-copies the {@code patient} (patient records
 * must never alias between an appointment and its clone) but keeps a shared,
 * shallow reference to {@code doctor} (the doctor roster is reference data,
 * not owned by the appointment). See Design_Decisions.md for the rationale.
 */
public class Appointment extends MedicalEntity implements Cloneable {

    private static final long serialVersionUID = 1L;

    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
    private String notes;

    public Appointment(String id, Patient patient, Doctor doctor, LocalDateTime dateTime, String notes) {
        super(id);
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.status = AppointmentStatus.PENDING;
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public Appointment clone() {
        try {
            Appointment cloned = (Appointment) super.clone();
            cloned.patient = this.patient.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Appointment implements Cloneable; this cannot happen", e);
        }
    }

    @Override
    public String describe() {
        return "Appointment [" + getId() + "] " + patient.getName() + " with Dr. " + doctor.getName()
                + " at " + dateTime + " (" + status + ")";
    }

    @Override
    public String toString() {
        return describe();
    }
}
