# MediTrack — Clinic & Appointment Management System

A modular, object-oriented Clinic & Appointment Management System in Core Java. Models patients,
doctors, appointments, and billing behind a menu-driven console UI, built to demonstrate SOLID
OOP design plus Java fundamentals (collections, exceptions, I/O, serialization) and two chosen
bonus tracks (File I/O persistence, and Singleton/Factory/Observer design patterns).

See [docs/Design_Decisions.md](docs/Design_Decisions.md) for the reasoning behind specific choices
(package naming, deep-vs-shallow copy semantics, checked exceptions, etc.), and
[docs/Setup_Instructions.md](docs/Setup_Instructions.md) for compile/run steps.

## Package structure

```
src/com/airtribe/meditrack/
├── Main.java
├── constants/        Constants (tax rate, file paths)
├── entity/           MedicalEntity, Person, Doctor, Patient, Appointment,
│                      Bill, ConsultationBill, ProcedureBill, BillSummary (immutable)
├── enums/            Specialization, AppointmentStatus, BillType
├── service/          DoctorService, PatientService, AppointmentService,
│                      BillFactory, ConsoleReminderObserver
├── util/             Validator, DateUtil, CSVUtil, IdGenerator, AppConfig, DataStore<T>
├── exception/        AppointmentNotFoundException, InvalidDataException
├── contracts/         Searchable<T>, Payable, AppointmentObserver
└── test/             TestRunner (manual tests, no JUnit)
```

> Note: the spec's `interface/` package is named `contracts/` here, since `interface` is a
> reserved Java keyword and cannot be used as a package name. See Design_Decisions.md.

## Setup & running

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out com.airtribe.meditrack.Main
```

Pass `--loadData` to resume from previously saved data:

```bash
java -cp out com.airtribe.meditrack.Main --loadData
```

Run the manual test suite:

```bash
java -cp out com.airtribe.meditrack.test.TestRunner
```

Full details, including Windows/PowerShell commands, are in
[docs/Setup_Instructions.md](docs/Setup_Instructions.md).

## Sample run

```
=== MediTrack v1.0.0 ===

--- Main Menu ---
1. Doctor Management
2. Patient Management
3. Appointment Management
4. Billing
5. Save & Exit
Choose an option: 1

-- Doctor Management --
1. Add Doctor
...
Choose an option: 1
Name: Dr. House
Age: 45
...
Added: Doctor [DOC0001] Dr. House, Age: 45, Specialization: GENERAL_MEDICINE, Fee: 500.0, Available: true

...
Choose an option: 3
-- Appointment Management --
Choose an option: 1
Patient ID: PAT0001
Doctor ID: DOC0001
Date/time (yyyy-MM-ddTHH:mm), e.g. 2026-08-01T10:30: 2027-01-01T10:00
Notes: Routine checkup
[REMINDER] Appointment APT0001 CREATED -> John Patient with Dr. Dr. House at 01-01-2027 10:00
Created: Appointment [APT0001] John Patient with Dr. Dr. House at 2027-01-01T10:00 (PENDING)

...
Choose an option: 4
-- Billing --
Choose an option: 1
Appointment ID: APT0001
Bill type: 1) Consultation  2) Procedure
Choose: 2
Procedure charge: 100
Generated: BillSummary{BIL0001, patient=John Patient, doctor=Dr. House, total=708.0, billedAt=2026-07-05T23:46:55}

Choose an option: 5
Data saved to 'data'.
Goodbye!
```

Manual test output:

```
PASS - Validator rejects malformed email
PASS - Validator rejects out-of-range age
PASS - Validator accepts well-formed data
PASS - Patient.clone() deep-copies medicalHistory (independent lists)
PASS - Patient.clone() returns a distinct object
PASS - Appointment.clone() deep-copies patient
PASS - Appointment.clone() shares doctor reference (shallow)
PASS - searchPatient(String id) finds a patient
PASS - searchPatient(String name, boolean) does partial matching
PASS - searchPatient(int age) finds all matching ages
PASS - ConsultationBill.generateBill() applies tax to base fee only
PASS - ProcedureBill.generateBill() applies tax to base+procedure
PASS - generateBill() is overridden differently per Bill subtype (polymorphism)
PASS - BillSummary retains constructor values (immutable, no setters exist to alter them)
PASS - DataStore<T> supports save/find/delete/exists/count
PASS - IdGenerator produces unique, incrementing bill IDs
PASS - CSVUtil writes and reads back a doctor row correctly

17 passed, 0 failed.
```

## Feature checklist

- **Package structure & Java basics** — access modifiers, static vs. instance scope, static
  init blocks (`Constants`, `MedicalEntity`), primitive/reference distinction.
- **Encapsulation** — private fields, getters/setters, validation centralized in `Validator`.
- **Inheritance** — `MedicalEntity -> Person -> Doctor`/`Patient`, `super`/`this` chaining.
- **Polymorphism** — `PatientService.searchPatient` overloaded by ID / name / age;
  `Bill.generateBill()` overridden per subtype with dynamic dispatch.
- **Abstraction & interfaces** — abstract `MedicalEntity`; `Payable` and `Searchable<T>`
  interfaces with default methods.
- **Deep vs. shallow copy** — `Patient`/`Appointment` implement `Cloneable`; see
  Design_Decisions.md for which fields are deep- vs. shallow-copied and why.
- **Immutable class** — `BillSummary`: final fields, no setters.
- **Enums** — `Specialization`, `AppointmentStatus`, `BillType` replace string constants.
- **CRUD, search, billing, menu-driven UI** — see `service/` and `Main.java`.
- **Bonus A: File I/O & Persistence** — CSV for Doctor/Patient, Java serialization for
  Appointment, gated behind `--loadData`.
- **Bonus B: Design Patterns** — Singleton (`AppConfig` eager, `IdGenerator` lazy), Factory
  (`BillFactory`), Observer (`AppointmentObserver`/`ConsoleReminderObserver`).

Not implemented in this pass: Bonus C (AI rule-based recommendation) and Bonus D
(Streams/lambdas analytics) — two of the four bonus tracks were selected per the assignment's
"choose any two" instruction.
