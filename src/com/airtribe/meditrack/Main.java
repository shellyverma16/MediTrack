package com.airtribe.meditrack;

import com.airtribe.meditrack.constants.Constants;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.BillType;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.BillFactory;
import com.airtribe.meditrack.service.ConsoleReminderObserver;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.AppConfig;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DoctorService doctorService = new DoctorService();
    private static final PatientService patientService = new PatientService();
    private static final AppointmentService appointmentService = new AppointmentService();
    private static final List<Bill> bills = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== " + AppConfig.INSTANCE.getAppName() + " v" + AppConfig.INSTANCE.getVersion() + " ===");
        appointmentService.addObserver(new ConsoleReminderObserver());

        boolean loadRequested = false;
        for (String arg : args) {
            if (Constants.LOAD_DATA_FLAG.equals(arg)) {
                loadRequested = true;
            }
        }
        if (loadRequested) {
            loadPersistedData();
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1 -> doctorMenu();
                case 2 -> patientMenu();
                case 3 -> appointmentMenu();
                case 4 -> billingMenu();
                case 5 -> {
                    savePersistedData();
                    running = false;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Doctor Management");
        System.out.println("2. Patient Management");
        System.out.println("3. Appointment Management");
        System.out.println("4. Billing");
        System.out.println("5. Save & Exit");
    }

    // ---------------- Doctor Menu ----------------

    private static void doctorMenu() {
        System.out.println("\n-- Doctor Management --");
        System.out.println("1. Add Doctor");
        System.out.println("2. List Doctors");
        System.out.println("3. Search Doctor by ID");
        System.out.println("4. Search Doctors by Name");
        System.out.println("5. Search Doctors by Specialization");
        System.out.println("6. Delete Doctor");
        int choice = readInt("Choose an option: ");
        try {
            switch (choice) {
                case 1 -> {
                    String name = readString("Name: ");
                    int age = readInt("Age: ");
                    String gender = readString("Gender: ");
                    String contact = readString("Contact number (10 digits): ");
                    String email = readString("Email: ");
                    Specialization spec = readSpecialization();
                    double fee = readDouble("Consultation fee: ");
                    Doctor doctor = doctorService.addDoctor(name, age, gender, contact, email, spec, fee);
                    System.out.println("Added: " + doctor);
                }
                case 2 -> doctorService.listAll().forEach(System.out::println);
                case 3 -> {
                    String id = readString("Doctor ID: ");
                    Doctor doctor = doctorService.searchDoctor(id);
                    System.out.println(doctor != null ? doctor : "No doctor found with that ID.");
                }
                case 4 -> {
                    String name = readString("Name contains: ");
                    doctorService.searchDoctor(name, true).forEach(System.out::println);
                }
                case 5 -> {
                    Specialization spec = readSpecialization();
                    doctorService.searchDoctor(spec).forEach(System.out::println);
                }
                case 6 -> {
                    String id = readString("Doctor ID to delete: ");
                    System.out.println(doctorService.deleteDoctor(id) ? "Deleted." : "Not found.");
                }
                default -> System.out.println("Invalid option.");
            }
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Patient Menu ----------------

    private static void patientMenu() {
        System.out.println("\n-- Patient Management --");
        System.out.println("1. Add Patient");
        System.out.println("2. List Patients");
        System.out.println("3. Search Patient by ID");
        System.out.println("4. Search Patients by Name");
        System.out.println("5. Search Patients by Age");
        System.out.println("6. Delete Patient");
        int choice = readInt("Choose an option: ");
        try {
            switch (choice) {
                case 1 -> {
                    String name = readString("Name: ");
                    int age = readInt("Age: ");
                    String gender = readString("Gender: ");
                    String contact = readString("Contact number (10 digits): ");
                    String email = readString("Email: ");
                    String bloodGroup = readString("Blood group: ");
                    Patient patient = patientService.addPatient(name, age, gender, contact, email, bloodGroup);
                    System.out.println("Added: " + patient);
                }
                case 2 -> patientService.listAll().forEach(System.out::println);
                case 3 -> {
                    String id = readString("Patient ID: ");
                    Patient patient = patientService.searchPatient(id);
                    System.out.println(patient != null ? patient : "No patient found with that ID.");
                }
                case 4 -> {
                    String name = readString("Name contains: ");
                    patientService.searchPatient(name, true).forEach(System.out::println);
                }
                case 5 -> {
                    int age = readInt("Age: ");
                    patientService.searchPatient(age).forEach(System.out::println);
                }
                case 6 -> {
                    String id = readString("Patient ID to delete: ");
                    System.out.println(patientService.deletePatient(id) ? "Deleted." : "Not found.");
                }
                default -> System.out.println("Invalid option.");
            }
        } catch (InvalidDataException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Appointment Menu ----------------

    private static void appointmentMenu() {
        System.out.println("\n-- Appointment Management --");
        System.out.println("1. Create Appointment");
        System.out.println("2. View Appointment");
        System.out.println("3. Confirm Appointment");
        System.out.println("4. Cancel Appointment");
        System.out.println("5. Complete Appointment");
        System.out.println("6. List Appointments");
        int choice = readInt("Choose an option: ");
        try {
            switch (choice) {
                case 1 -> {
                    String patientId = readString("Patient ID: ");
                    String doctorId = readString("Doctor ID: ");
                    Patient patient = patientService.searchPatient(patientId);
                    Doctor doctor = doctorService.searchDoctor(doctorId);
                    if (patient == null || doctor == null) {
                        System.out.println("Patient or doctor not found.");
                        return;
                    }
                    String dateTimeStr = readString("Date/time (yyyy-MM-ddTHH:mm), e.g. 2026-08-01T10:30: ");
                    LocalDateTime dateTime = DateUtil.fromStorageString(dateTimeStr);
                    String notes = readString("Notes: ");
                    Appointment appointment = appointmentService.createAppointment(patient, doctor, dateTime, notes);
                    System.out.println("Created: " + appointment);
                }
                case 2 -> {
                    String id = readString("Appointment ID: ");
                    System.out.println(appointmentService.viewAppointment(id));
                }
                case 3 -> appointmentService.confirmAppointment(readString("Appointment ID: "));
                case 4 -> appointmentService.cancelAppointment(readString("Appointment ID: "));
                case 5 -> appointmentService.completeAppointment(readString("Appointment ID: "));
                case 6 -> appointmentService.listAll().forEach(System.out::println);
                default -> System.out.println("Invalid option.");
            }
        } catch (InvalidDataException | AppointmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Billing Menu ----------------

    private static void billingMenu() {
        System.out.println("\n-- Billing --");
        System.out.println("1. Generate Bill for Appointment");
        System.out.println("2. List Bills");
        int choice = readInt("Choose an option: ");
        try {
            switch (choice) {
                case 1 -> {
                    String appointmentId = readString("Appointment ID: ");
                    Appointment appointment = appointmentService.viewAppointment(appointmentId);
                    if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
                        System.out.println("Note: appointment is not marked COMPLETED yet.");
                    }
                    System.out.println("Bill type: 1) Consultation  2) Procedure");
                    int typeChoice = readInt("Choose: ");
                    BillType type = typeChoice == 2 ? BillType.PROCEDURE : BillType.CONSULTATION;
                    double baseAmount = appointment.getDoctor().getConsultationFee();
                    double procedureCharge = type == BillType.PROCEDURE
                            ? readDouble("Procedure charge: ")
                            : 0.0;
                    String billId = IdGenerator.getInstance().nextBillId();
                    Bill bill = BillFactory.createBill(type, billId, appointment, baseAmount, procedureCharge);
                    bills.add(bill);
                    BillSummary summary = new BillSummary(bill.getId(), appointment.getPatient().getName(),
                            appointment.getDoctor().getName(), bill.generateBill(), bill.getCreatedAt());
                    System.out.println("Generated: " + summary);
                }
                case 2 -> bills.forEach(System.out::println);
                default -> System.out.println("Invalid option.");
            }
        } catch (AppointmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------- Persistence ----------------

    private static void loadPersistedData() {
        try {
            int doctors = doctorService.loadFromCsv();
            int patients = patientService.loadFromCsv();
            int appointments = appointmentService.loadFromFile();
            System.out.println("Loaded " + doctors + " doctors, " + patients + " patients, "
                    + appointments + " appointments from disk.");
        } catch (Exception e) {
            System.out.println("Could not load persisted data: " + e.getMessage());
        }
    }

    private static void savePersistedData() {
        try {
            doctorService.saveToCsv();
            patientService.saveToCsv();
            appointmentService.saveToFile();
            System.out.println("Data saved to '" + Constants.DATA_DIR + "'.");
        } catch (Exception e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // ---------------- Input Helpers ----------------

    private static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static Specialization readSpecialization() {
        System.out.println("Specializations: " + java.util.Arrays.toString(Specialization.values()));
        while (true) {
            try {
                return Specialization.valueOf(readString("Specialization: ").toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter one of the listed specializations.");
            }
        }
    }
}
