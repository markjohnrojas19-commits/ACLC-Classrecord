# ACLC Class Record System — How It All Works

> This document walks through the project by answering "what happens when...?" for each major scenario. Read it top-to-bottom to understand the full system, or jump to a specific scenario when you need a refresher.
>
> Keep each section short. Prefer narrative prose over diagrams. Update this file whenever a scenario's flow changes, not on a fixed schedule.

---

## What happens when the application starts?

1. **`Main.java`** — The entry point. Sets the Swing look-and-feel, then creates and displays `LoginForm`.
2. **`LoginForm`** — The first screen the user sees. Two text fields (username, password), a login button. Nothing else loads until login succeeds.
3. **No database connection yet** — The connection is created on-demand when the first query runs, not at startup. This avoids blocking the UI if MySQL is slow to respond.

---

## What happens when a user logs in?

1. User types username and password into `LoginForm` and clicks "Login."
2. `LoginForm` calls `UserDao.authenticate(username, password)`.
3. `UserDao` opens a JDBC connection via `DatabaseConnection`, runs a `SELECT` query against the `users` table with a `PreparedStatement`.
4. If a matching row is found, `UserDao` returns a `User` object (with role: Admin or Instructor). If not, returns `null`.
5. Back in `LoginForm`: if `User` is `null`, show an error dialog. If valid, create and show `DashboardForm`, passing the `User` object, and dispose `LoginForm`.

**The test:** `UserDao` knows nothing about Swing. `LoginForm` knows nothing about SQL. They communicate through the `User` model object.

---

## What happens when a student is added?

1. User navigates from `DashboardForm` to `StudentForm`.
2. User fills in fields (Student ID, first name, last name, course, year level, section, gender) and clicks "Add."
3. `StudentForm` creates a `Student` object from the input fields.
4. `StudentForm` calls `StudentDao.add(student)`.
5. `StudentDao` opens a JDBC connection, runs an `INSERT` query with a `PreparedStatement`.
6. On success, `StudentForm` refreshes the JTable by calling `StudentDao.getAll()` and rebuilding the table model.
7. Input fields are cleared, ready for the next entry.

**The same pattern applies to Edit and Delete** — the form calls the appropriate DAO method, then refreshes the table.

---

## What happens when grades are computed?

This is the core workflow of the system.

1. User navigates to `GradeForm`.
2. User selects a student (from a dropdown populated by `StudentDao.getAll()`) and a subject (from a dropdown populated by `SubjectDao.getAll()`).
3. User enters quiz, assignment, and exam scores.
4. `GradeForm` calls `GradeComputer.computeFinalGrade(quiz, assignment, exam)`.
5. `GradeComputer` reads weights from `GradeConstants` (e.g., `QUIZ_WEIGHT = 0.30`, `ASSIGNMENT_WEIGHT = 0.30`, `EXAM_WEIGHT = 0.40`) and computes: `finalGrade = (quiz * QUIZ_WEIGHT) + (assignment * ASSIGNMENT_WEIGHT) + (exam * EXAM_WEIGHT)`.
6. `GradeComputer.determineRemarks(finalGrade)` returns "Passed" if `finalGrade >= PASSING_GRADE` (75), otherwise "Failed."
7. `GradeForm` displays the computed final grade and remarks.
8. User clicks "Save." `GradeForm` creates a `Grade` object and calls `GradeDao.add(grade)`.
9. The grade persists to the database. The JTable refreshes to show all grades.

**Why GradeComputer is separate from GradeDao:** Computation is business logic. Database access is infrastructure. They are different atoms with different reasons to change. If the formula changes, only `GradeComputer` changes. If the database schema changes, only `GradeDao` changes.

**Why GradeConstants is separate from GradeComputer:** The weights are configurable data, not logic. Extracting them makes it trivial to change the formula (e.g., when the instructor confirms the real weights) without touching computation logic.

---

## What happens when the dashboard loads?

1. `DashboardForm` opens (after login or when navigating back).
2. It queries the database for summary counts:
   - `StudentDao.getCount()` — total students
   - `SubjectDao.getCount()` — total subjects
   - `GradeDao.getPassedCount()` — students with final grade >= 75
   - `GradeDao.getFailedCount()` — students with final grade < 75
3. These counts are displayed in labels on the dashboard.
4. Navigation buttons allow the user to open `StudentForm`, `SubjectForm`, or `GradeForm`.

---

## How does the database connection work without forms knowing about JDBC?

`DatabaseConnection` is a utility class with a single static method: `getConnection()`. It returns a `java.sql.Connection` to the MySQL database.

- **Only DAO classes call `DatabaseConnection.getConnection()`.** No form, service, or model class ever touches it.
- **Connection details (URL, username, password) live in one place.** If the database moves, only `DatabaseConnection.java` changes.

**The test:** If you delete `DatabaseConnection.java`, only the DAO classes fail to compile. No form, no model, no service is affected.

---

## What's the difference between Service and DAO?

- **DAO (Data Access Object)** — Knows how to read and write data to/from MySQL. Speaks SQL. One DAO per table. Its job: translate between Java objects and database rows.
- **Service** — Knows business rules. Does NOT speak SQL. Its job: compute grades, validate login credentials, apply domain logic. Calls DAOs when it needs data.

Think of it as: DAO is the librarian (fetches and files books), Service is the teacher (decides what grade a student gets based on the scores the librarian retrieved).

In this project, the service layer is thin (mainly `GradeComputer` and `AuthService`), because the business logic is straightforward. That's fine — don't add abstraction just to have it. The layer exists so that computation logic and database logic don't live in the same class.
