# JVM Report

A short walkthrough of how core JVM concepts show up concretely in MediTrack's code.

## Class loading

When `java -cp out com.airtribe.meditrack.Main` runs, the JVM's bootstrap/application class
loader loads `Main` first, then lazily loads each class the first time it's referenced (not the
whole package at once). This is why `Constants`' static initializer block (which creates the
`data/` directory) only runs the first time `Constants` is actually touched — e.g. the first call
to `doctorService.saveToCsv()` — not at JVM startup.

`MedicalEntity` has its own static block that resets `totalEntitiesCreated = 0`. Static
initializers for a class run exactly once, in source order, the first time the class is
initialized (on first active use — first instantiation, first static field access, etc.), and
before any instance of that class (or its subclasses) can be constructed. Since `Person`, `Doctor`,
`Patient`, `Appointment`, and `Bill` all extend `MedicalEntity`, constructing any one of them
triggers `MedicalEntity`'s static block exactly once for the whole run.

## Memory: stack vs. heap

- **Stack**: each thread gets its own stack of frames, one per method call, holding local
  variables and parameter references. In `Main.readInt(String prompt)`, the `prompt` reference and
  the parsed `int` live on the stack frame for that call and disappear when it returns.
- **Heap**: every object created with `new` — every `Doctor`, `Patient`, `Appointment`, `Bill`,
  and every `ArrayList` backing a `DataStore` — lives on the heap, shared across the whole
  application. `DataStore<T>`'s internal `LinkedHashMap` is what keeps these heap objects reachable
  for the life of the program (or until `delete`/`clear` is called).

## Garbage collection and cloning

`Patient.clone()` and `Appointment.clone()` each allocate a new heap object via
`super.clone()`. Once a `DoctorService`/`PatientService`/`AppointmentService` no longer holds a
reference to a deleted entity (see `DataStore.delete`), that object becomes unreachable and
eligible for garbage collection on the next GC cycle — nothing in this project holds long-lived
"forgotten" references (no static caches of deleted entities), so deleted data is not leaked.

`Appointment.clone()`'s shallow reference to `doctor` (see Design_Decisions.md) has a GC
consequence worth naming: the cloned appointment keeps the same `Doctor` object alive as the
original, rather than creating a second heap object. That's intentional and cheap — doctors are
comparatively few and long-lived, so sharing the reference avoids needless allocation.

## Primitives vs. references

Fields like `Person.age` (`int`) and `Doctor.consultationFee` (`double`) are primitives stored
inline in the object's memory layout on the heap — no separate allocation, no autoboxing. Fields
like `Person.name` (`String`) and `Patient.medicalHistory` (`List<String>`) are references to
separately-allocated heap objects. This distinction is why `Patient.clone()` needs a manual deep
copy of `medicalHistory`: `Object.clone()`'s default field-by-field copy duplicates the *reference*
to the list, not the list itself, so both patients would otherwise point at one shared `ArrayList`.

## Autoboxing note

`IdGenerator`'s counters (`int`) avoid unnecessary boxing by staying primitive throughout; the
formatted ID strings (`String.format("PAT%04d", ...)`) are the only heap allocations per call.
Contrast with `Specialization.valueOf(...)` in `Doctor`, which returns a reference to one of a
fixed set of enum constants created once at class-load time — calling it repeatedly does not
allocate new objects, since Java enum constants are singletons per JVM.
