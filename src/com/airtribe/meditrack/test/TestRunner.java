package com.airtribe.meditrack.test;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.Specialization;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.service.BillFactory;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.enums.BillType;
import com.airtribe.meditrack.util.CSVUtil;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.IdGenerator;
import com.airtribe.meditrack.util.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Manual test harness (no JUnit). Run with: java -cp out com.airtribe.meditrack.test.TestRunner
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        testValidatorRejectsInvalidData();
        testValidatorAcceptsGoodData();
        testPatientDeepCloneIndependence();
        testAppointmentCloneSharesDoctorButCopiesPatient();
        testSearchPatientOverloads();
        testBillPolymorphism();
        testBillSummaryImmutability();
        testDataStoreCrud();
        testIdGeneratorUniqueness();
        testCsvRoundTrip();

        System.out.println("\n" + passed + " passed, " + failed + " failed.");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(boolean condition, String testName) {
        if (condition) {
            passed++;
            System.out.println("PASS - " + testName);
        } else {
            failed++;
            System.out.println("FAIL - " + testName);
        }
    }

    private static void testValidatorRejectsInvalidData() {
        boolean rejected;
        try {
            Validator.validateEmail("not-an-email");
            rejected = false;
        } catch (InvalidDataException e) {
            rejected = true;
        }
        check(rejected, "Validator rejects malformed email");

        try {
            Validator.validateAge(200);
            rejected = false;
        } catch (InvalidDataException e) {
            rejected = true;
        }
        check(rejected, "Validator rejects out-of-range age");
    }

    private static void testValidatorAcceptsGoodData() throws InvalidDataException {
        Validator.validateEmail("jane.doe@example.com");
        Validator.validatePhone("9876543210");
        Validator.validateAge(30);
        check(true, "Validator accepts well-formed data");
    }

    private static void testPatientDeepCloneIndependence() throws InvalidDataException {
        Patient original = new Patient("PAT-T1", "Alice", 40, "F", "9876543210", "alice@example.com", "O+");
        original.addMedicalHistoryEntry("Flu - 2024");
        Patient clone = original.clone();
        clone.addMedicalHistoryEntry("Allergy - 2026");

        check(original.getMedicalHistory().size() == 1 && clone.getMedicalHistory().size() == 2,
                "Patient.clone() deep-copies medicalHistory (independent lists)");
        check(original != clone, "Patient.clone() returns a distinct object");
    }

    private static void testAppointmentCloneSharesDoctorButCopiesPatient() throws InvalidDataException {
        Patient patient = new Patient("PAT-T2", "Bob", 50, "M", "9876543211", "bob@example.com", "B+");
        Doctor doctor = new Doctor("DOC-T1", "Dr. Smith", 45, "M", "9876543212", "smith@example.com",
                Specialization.CARDIOLOGY, 500);
        Appointment appointment = new Appointment("APT-T1", patient, doctor, LocalDateTime.now().plusDays(1), "checkup");
        Appointment clone = appointment.clone();

        check(clone.getPatient() != appointment.getPatient(), "Appointment.clone() deep-copies patient");
        check(clone.getDoctor() == appointment.getDoctor(), "Appointment.clone() shares doctor reference (shallow)");
    }

    private static void testSearchPatientOverloads() throws InvalidDataException {
        PatientService service = new PatientService();
        service.addPatient("Carol", 25, "F", "9876543213", "carol@example.com", "A+");
        service.addPatient("Caroline", 25, "F", "9876543214", "caroline@example.com", "A-");

        Patient byId = service.searchPatient(service.listAll().get(0).getId());
        List<Patient> byName = service.searchPatient("Carol", true);
        List<Patient> byAge = service.searchPatient(25);

        check(byId != null, "searchPatient(String id) finds a patient");
        check(byName.size() == 2, "searchPatient(String name, boolean) does partial matching");
        check(byAge.size() == 2, "searchPatient(int age) finds all matching ages");
    }

    private static void testBillPolymorphism() throws InvalidDataException {
        Patient patient = new Patient("PAT-T3", "Dan", 35, "M", "9876543215", "dan@example.com", "AB+");
        Doctor doctor = new Doctor("DOC-T2", "Dr. Lee", 50, "F", "9876543216", "lee@example.com",
                Specialization.GENERAL_MEDICINE, 300);
        Appointment appointment = new Appointment("APT-T2", patient, doctor, LocalDateTime.now().plusDays(1), "visit");

        Bill consultation = BillFactory.createBill(BillType.CONSULTATION, "BIL-T1", appointment, 300, 0);
        Bill procedure = BillFactory.createBill(BillType.PROCEDURE, "BIL-T2", appointment, 300, 200);

        check(consultation.generateBill() == 300 * 1.18, "ConsultationBill.generateBill() applies tax to base fee only");
        check(procedure.generateBill() == 500 * 1.18, "ProcedureBill.generateBill() applies tax to base+procedure");
        check(consultation.generateBill() != procedure.generateBill(),
                "generateBill() is overridden differently per Bill subtype (polymorphism)");
    }

    private static void testBillSummaryImmutability() {
        LocalDateTime now = LocalDateTime.now();
        BillSummary summary = new BillSummary("BIL-T3", "Eve", "Dr. Fox", 590.0, now);
        check(summary.getBillId().equals("BIL-T3") && summary.getTotalAmount() == 590.0 && summary.getBilledAt().equals(now),
                "BillSummary retains constructor values (immutable, no setters exist to alter them)");
    }

    private static void testDataStoreCrud() {
        DataStore<String> store = new DataStore<>();
        store.save("1", "one");
        store.save("2", "two");
        boolean crudWorks = store.count() == 2
                && store.findById("1").equals("one")
                && store.delete("1")
                && !store.exists("1")
                && store.count() == 1;
        check(crudWorks, "DataStore<T> supports save/find/delete/exists/count");
    }

    private static void testIdGeneratorUniqueness() {
        IdGenerator gen = IdGenerator.getInstance();
        String a = gen.nextBillId();
        String b = gen.nextBillId();
        check(!a.equals(b), "IdGenerator produces unique, incrementing bill IDs");
    }

    private static void testCsvRoundTrip() throws IOException, InvalidDataException {
        String path = "data/test_doctors_roundtrip.csv";
        DoctorService service = new DoctorService();
        service.addDoctor("Dr. Roundtrip", 38, "F", "9876543217", "roundtrip@example.com",
                Specialization.DERMATOLOGY, 450);

        List<String> lines = new java.util.ArrayList<>();
        for (Doctor d : service.listAll()) {
            lines.add(String.join(",", d.getId(), d.getName(), String.valueOf(d.getAge()), d.getGender(),
                    d.getContactNumber(), d.getEmail(), d.getSpecialization().name(),
                    String.valueOf(d.getConsultationFee()), String.valueOf(d.isAvailable())));
        }
        CSVUtil.writeLines(path, lines);
        List<String> readBack = CSVUtil.readLines(path);

        check(readBack.size() == 1 && readBack.get(0).contains("Dr. Roundtrip"),
                "CSVUtil writes and reads back a doctor row correctly");

        new java.io.File(path).delete();
    }
}
