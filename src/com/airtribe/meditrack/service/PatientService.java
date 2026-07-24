package com.airtribe.meditrack.service;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.CSVUtil;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    private final DataStore<Patient> dataStore = new DataStore<>();
    private final IdGenerator idGenerator = IdGenerator.getInstance();

    public Patient addPatient(String name, int age, String gender, String contactNumber, String email,
                               String bloodGroup) throws InvalidDataException {
        String id = idGenerator.nextPatientId();
        Patient patient = new Patient(id, name, age, gender, contactNumber, email, bloodGroup);
        dataStore.save(id, patient);
        return patient;
    }

    public boolean deletePatient(String id) {
        return dataStore.delete(id);
    }

    public List<Patient> listAll() {
        return dataStore.findAll();
    }

    /** Overload 1: search by exact patient ID. */
    public Patient searchPatient(String id) {
        return dataStore.findById(id);
    }

    /** Overload 2: search by name, optionally as a partial (contains) match. */
    public List<Patient> searchPatient(String namePart, boolean partialMatch) {
        List<Patient> results = new ArrayList<>();
        String q = namePart.toLowerCase();
        for (Patient patient : dataStore.findAll()) {
            boolean match = partialMatch
                    ? patient.getName().toLowerCase().contains(q)
                    : patient.getName().equalsIgnoreCase(namePart);
            if (match) {
                results.add(patient);
            }
        }
        return results;
    }

    /** Overload 3: search by exact age. */
    public List<Patient> searchPatient(int age) {
        List<Patient> results = new ArrayList<>();
        for (Patient patient : dataStore.findAll()) {
            if (patient.getAge() == age) {
                results.add(patient);
            }
        }
        return results;
    }

    public void saveToCsv() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Patient p : dataStore.findAll()) {
            String history = String.join(";", p.getMedicalHistory());
            lines.add(String.join(",",
                    p.getId(), p.getName(), String.valueOf(p.getAge()), p.getGender(),
                    p.getContactNumber(), p.getEmail(), p.getBloodGroup(), history));
        }
        CSVUtil.writeLines(Constants.PATIENTS_FILE, lines);
    }

    public int loadFromCsv() throws IOException, InvalidDataException {
        int loaded = 0;
        int highestId = 0;
        for (String line : CSVUtil.readLines(Constants.PATIENTS_FILE)) {
            String[] f = CSVUtil.parseLine(line);
            Patient patient = new Patient(f[0], f[1], Integer.parseInt(f[2]), f[3], f[4], f[5], f[6]);
            if (f.length > 7 && !f[7].isEmpty()) {
                for (String entry : f[7].split(";")) {
                    patient.addMedicalHistoryEntry(entry);
                }
            }
            dataStore.save(patient.getId(), patient);
            highestId = Math.max(highestId, Integer.parseInt(patient.getId().substring(3)));
            loaded++;
        }
        if (highestId > 0) {
            idGenerator.fastForward("PAT", highestId);
        }
        return loaded;
    }
}
