# Setup Instructions

## Prerequisites

- JDK 17 or later on the PATH (tested with JDK 21). No build tool (Maven/Gradle) is required.

## Compiling

From the project root:

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
```

On Windows PowerShell, replace the `find` line with:

```powershell
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -d out -encoding UTF-8 "@sources.txt"
```

## Running the application

```bash
java -cp out com.airtribe.meditrack.Main
```

To resume from previously persisted data (see below):

```bash
java -cp out com.airtribe.meditrack.Main --loadData
```

## Running the manual tests

```bash
java -cp out com.airtribe.meditrack.test.TestRunner
```

`TestRunner` exits with code `1` if any check fails, so it can be wired into a CI step.

## Data persistence

- Doctors and patients are saved as CSV under `data/doctors.csv` and `data/patients.csv`.
- Appointments are saved via Java serialization to `data/appointments.ser` (they hold object
  references to Patient/Doctor, which round-trip more cleanly through serialization than CSV).
- Data is written when you choose "Save & Exit" from the main menu, and only read back on
  startup if you pass `--loadData`.
- The `data/` directory is created automatically on first save.
