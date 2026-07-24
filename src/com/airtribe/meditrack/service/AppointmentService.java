package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.contracts.AppointmentObserver;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.IdGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {

    private final DataStore<Appointment> dataStore = new DataStore<>();
    private final IdGenerator idGenerator = IdGenerator.getInstance();
    private final List<AppointmentObserver> observers = new ArrayList<>();

    public void addObserver(AppointmentObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Appointment appointment, String eventType) {
        for (AppointmentObserver observer : observers) {
            observer.onAppointmentEvent(appointment, eventType);
        }
    }

    public Appointment createAppointment(Patient patient, Doctor doctor, LocalDateTime dateTime, String notes)
            throws InvalidDataException {
        DateUtil.validateFuture(dateTime);
        String id = idGenerator.nextAppointmentId();
        Appointment appointment = new Appointment(id, patient, doctor, dateTime, notes);
        dataStore.save(id, appointment);
        notifyObservers(appointment, "CREATED");
        return appointment;
    }

    public Appointment viewAppointment(String id) throws AppointmentNotFoundException {
        Appointment appointment = dataStore.findById(id);
        if (appointment == null) {
            throw new AppointmentNotFoundException("No appointment found with ID: " + id);
        }
        return appointment;
    }

    public void confirmAppointment(String id) throws AppointmentNotFoundException {
        Appointment appointment = viewAppointment(id);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        notifyObservers(appointment, "CONFIRMED");
    }

    public void cancelAppointment(String id) throws AppointmentNotFoundException {
        Appointment appointment = viewAppointment(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        notifyObservers(appointment, "CANCELLED");
    }

    public void completeAppointment(String id) throws AppointmentNotFoundException {
        Appointment appointment = viewAppointment(id);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        notifyObservers(appointment, "COMPLETED");
    }

    public List<Appointment> listAll() {
        return dataStore.findAll();
    }

    public void saveToFile() throws IOException {
        File file = new File(Constants.APPOINTMENTS_FILE);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(dataStore.findAll()));
        }
    }

    @SuppressWarnings("unchecked")
    public int loadFromFile() throws IOException, ClassNotFoundException {
        File file = new File(Constants.APPOINTMENTS_FILE);
        if (!file.exists()) {
            return 0;
        }
        int highestId = 0;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Appointment> appointments = (List<Appointment>) in.readObject();
            for (Appointment appointment : appointments) {
                dataStore.save(appointment.getId(), appointment);
                highestId = Math.max(highestId, Integer.parseInt(appointment.getId().substring(3)));
            }
            if (highestId > 0) {
                idGenerator.fastForward("APT", highestId);
            }
            return appointments.size();
        }
    }
}
