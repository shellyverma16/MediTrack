# Design Decisions

## Package layout deviates from the brief in one place

The spec's tree lists an `interface/` package. `interface` is a reserved Java keyword and cannot
be used as a package (or any) identifier, so that package is named `contracts/` instead. It holds
`Searchable`, `Payable`, and `AppointmentObserver` exactly as specified. Similarly, `Specialization`,
`AppointmentStatus`, and `BillType` live in an `enums/` package (not called out separately in the
brief's tree, but implied by "Enums: Specialization, AppointmentStatus").

## Inheritance chain: MedicalEntity -> Person -> Doctor / Patient

The brief separately asks for "Person -> Doctor, Patient" (Inheritance) and an "abstract class
MedicalEntity for common behavior" (Abstraction). Rather than treating these as unrelated, the
project makes `MedicalEntity` the abstract root (id, createdAt timestamp, a static
`totalEntitiesCreated` counter, and an abstract `describe()`), with `Person` extending it to add
name/age/contact fields, and `Doctor`/`Patient` extending `Person`. This gives a genuine
three-level `super()` chain and lets `Bill`/`Appointment` also extend `MedicalEntity` to reuse the
same identity/timestamp bookkeeping without duplicating it in every entity.

## Deep vs. shallow copy: two different answers on purpose

- `Patient.clone()` deep-copies `medicalHistory` (a mutable `ArrayList<String>`). If it didn't,
  mutating a cloned patient's history would silently corrupt the original's records — exactly the
  bug `Cloneable` is meant to demonstrate avoiding.
- `Appointment.clone()` deep-copies its `patient` (via `Patient.clone()`) but keeps a **shared**,
  shallow reference to `doctor`. This is intentional, not an oversight: a doctor is treated as
  reference/roster data owned by `DoctorService`, not by the appointment, so there's no
  correctness reason to duplicate it, and doing so would let a cloned appointment silently drift
  from doctor availability updates made elsewhere. It's a concrete example of why "deep copy
  everything" is not always the right default — the decision is per-field, driven by ownership.

## Exceptions are checked, not runtime

`InvalidDataException` and `AppointmentNotFoundException` both extend `Exception`. This was a
deliberate choice over `RuntimeException`: the goal of this project includes demonstrating
try/catch usage end-to-end (Validator -> entity setters -> services -> Main's menu handlers), and
checked exceptions force every layer to make an explicit decision about handling or propagating,
which is the point of the exercise.

## CSV format is intentionally simple, with one known limitation

`CSVUtil` uses `String.split(",")` as required by the brief, with no quoting/escaping. This means
a field containing a literal comma would break parsing. `Patient.medicalHistory` is a list, so it
is joined with `;` (not `,`) when written to CSV specifically to avoid colliding with the row
delimiter. Names, notes, etc. are assumed comma-free for this project's scope.

## Singleton: one eager, one lazy, per the bonus spec

- `AppConfig` is eager: `public static final AppConfig INSTANCE = new AppConfig();` — appropriate
  because it's tiny, immutable config that's always needed.
- `IdGenerator` is lazy (`getInstance()` creates it on first call, synchronized): chosen to show
  the alternative pattern. Its counters are fast-forwarded after `--loadData` loads persisted
  entities, so freshly generated IDs never collide with IDs restored from disk.

## Factory: BillFactory returns different Bill subtypes

`BillFactory.createBill(BillType, ...)` returns either a `ConsultationBill` or `ProcedureBill`.
Both override `Bill.generateBill()` with different math (tax-on-fee vs. tax-on-fee-plus-procedure),
which is also the concrete example used for the "overriding" polymorphism requirement.

## Observer: console reminders decoupled from AppointmentService

`AppointmentService` holds a list of `AppointmentObserver`s and notifies them on create / confirm
/ cancel / complete, without knowing what a "reminder" is. `ConsoleReminderObserver` is the one
implementation wired up in `Main`; swapping in an email/SMS observer later would not touch
`AppointmentService`.

## Bonus scope

Of the four optional bonus categories, this project implements:

- **A. File I/O & Persistence** — CSV for Doctor/Patient, Java serialization for Appointment,
  gated behind `--loadData` on startup.
- **B. Design Patterns** — Singleton (eager `AppConfig` + lazy `IdGenerator`), Factory
  (`BillFactory`), Observer (`AppointmentObserver` / `ConsoleReminderObserver`).

The AI rule-based recommendation (C) and Streams/lambdas analytics (D) bonus categories were not
implemented in this pass.
