package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.CSVUtil;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoctorService {

    private final DataStore<Doctor> dataStore = new DataStore<>();
    private final IdGenerator idGenerator = IdGenerator.getInstance();

    public Doctor addDoctor(String name, int age, String gender, String contactNumber, String email,
                             Specialization specialization, double consultationFee) throws InvalidDataException {
        String id = idGenerator.nextDoctorId();
        Doctor doctor = new Doctor(id, name, age, gender, contactNumber, email, specialization, consultationFee);
        dataStore.save(id, doctor);
        return doctor;
    }

    public boolean deleteDoctor(String id) {
        return dataStore.delete(id);
    }

    public List<Doctor> listAll() {
        return dataStore.findAll();
    }

    public Doctor searchDoctor(String id) {
        return dataStore.findById(id);
    }

    public List<Doctor> searchDoctor(String namePart, boolean partialMatch) {
        List<Doctor> results = new ArrayList<>();
        String q = namePart.toLowerCase();
        for (Doctor doctor : dataStore.findAll()) {
            boolean match = partialMatch
                    ? doctor.getName().toLowerCase().contains(q)
                    : doctor.getName().equalsIgnoreCase(namePart);
            if (match) {
                results.add(doctor);
            }
        }
        return results;
    }

    public List<Doctor> searchDoctor(Specialization specialization) {
        List<Doctor> results = new ArrayList<>();
        for (Doctor doctor : dataStore.findAll()) {
            if (doctor.getSpecialization() == specialization) {
                results.add(doctor);
            }
        }
        return results;
    }

    public void saveToCsv() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Doctor d : dataStore.findAll()) {
            lines.add(String.join(",",
                    d.getId(), d.getName(), String.valueOf(d.getAge()), d.getGender(),
                    d.getContactNumber(), d.getEmail(), d.getSpecialization().name(),
                    String.valueOf(d.getConsultationFee()), String.valueOf(d.isAvailable())));
        }
        CSVUtil.writeLines(Constants.DOCTORS_FILE, lines);
    }

    public int loadFromCsv() throws IOException, InvalidDataException {
        int loaded = 0;
        int highestId = 0;
        for (String line : CSVUtil.readLines(Constants.DOCTORS_FILE)) {
            String[] f = CSVUtil.parseLine(line);
            Doctor doctor = new Doctor(f[0], f[1], Integer.parseInt(f[2]), f[3], f[4], f[5],
                    Specialization.valueOf(f[6]), Double.parseDouble(f[7]));
            doctor.setAvailable(Boolean.parseBoolean(f[8]));
            dataStore.save(doctor.getId(), doctor);
            highestId = Math.max(highestId, Integer.parseInt(doctor.getId().substring(3)));
            loaded++;
        }
        if (highestId > 0) {
            idGenerator.fastForward("DOC", highestId);
        }
        return loaded;
    }
}
