# ACLC Class Record System — Development Roadmap

---

# Project Intent & Constraints

## Goals

* [x] Build a functional Class Record System for ACLC as a school project
* [ ] Learn Java Swing GUI development
* [ ] Learn MySQL database integration via JDBC
* [ ] Develop strong understanding of:
  * [ ] Layered architecture (Model / DAO / Service / UI)
  * [ ] Clean code principles in Java
  * [ ] CRUD operations with a relational database

---

## Constraints

* Must be built in **NetBeans IDE** (instructor requirement)
* Must use **Java Swing** for GUI
* Must use **MySQL + XAMPP** for database
* No external frameworks (Spring, Hibernate, etc.) — plain Java + JDBC only
* Grade computation formula must be easy to change (instructor hasn't confirmed exact weights yet)

---

## Technology Choice

### Stack

* [x] Language: **Java**
* [x] GUI Framework: **Java Swing**
* [x] Database: **MySQL** (via XAMPP)
* [x] Database Connectivity: **JDBC**
* [x] IDE: **NetBeans**
* [ ] Build tool: **NetBeans built-in** (Ant)

### Why Java Swing + MySQL

* Instructor-mandated tech stack for the course
* Swing is bundled with Java — no extra dependencies
* MySQL + XAMPP is the standard local dev setup for school projects
* JDBC is the standard Java database API — no ORM complexity

---

## Development Philosophy

### Need-Driven Development

* [ ] Build the first concrete use case (Login -> Dashboard)
* [ ] When a capability is missing -> implement it
* [ ] Never build systems "just in case"

### Iterative Growth Loop

* [ ] Implement feature
* [ ] Hit limitation
* [ ] Improve foundation
* [ ] Resume feature
* [ ] Refactor when needed

---

## Reality Check

* This is a school project, not production software. Scope is bounded by the semester timeline.
* Single-user desktop app — no need for concurrency, multi-tenancy, or web deployment.
* XAMPP runs locally — no cloud infrastructure needed.
* The architecture should be clean enough to impress the instructor but not over-engineered beyond what the features require.

---

## Success Criteria

* [ ] Instructor can log in, add students, add subjects, input grades, and see computed results
* [ ] Grade computation is automatic and correct
* [ ] Dashboard shows summary statistics
* [ ] Data persists in MySQL database across sessions
* [ ] UI is clean, consistent, and professional-looking
* [ ] Code is readable and well-organized

---

# Project Structure

```id="proj-structure"
ACLC-Classrecord/
  src/
    model/                — Plain Java classes (domain objects)
      Student.java          — student data (id, name, course, year, section, gender)
      Subject.java          — subject data (code, name)
      Grade.java            — grade data (quiz, assignment, exam, final grade, remarks)
      User.java             — login credentials and role
    dao/                  — Data Access Objects (JDBC operations)
      DatabaseConnection.java — MySQL connection utility
      StudentDao.java       — student CRUD queries
      SubjectDao.java       — subject CRUD queries
      GradeDao.java         — grade CRUD queries
      UserDao.java          — user authentication queries
    service/              — Business logic
      GradeComputer.java    — grade computation with configurable weights
      AuthService.java      — login validation
    ui/                   — Java Swing forms
      LoginForm.java        — login screen
      DashboardForm.java    — main menu with summary stats
      StudentForm.java      — student management (CRUD + JTable)
      SubjectForm.java      — subject management (CRUD + JTable)
      GradeForm.java        — grade input and auto-computation
    util/                 — Shared utilities
      GradeConstants.java   — configurable grade weights (QUIZ_WEIGHT, etc.)
  sql/
    schema.sql            — database creation script
  README.md
  CLAUDE.md
  milestones.md
  progress.md
  ARCHITECTURE.md

Layer dependency order (top depends on bottom, never reverse):
  ui/ -> service/ -> dao/ -> model/
  util/ is accessible from any layer
```

---

# Database Schema

```sql
Database: aclc_classrecord_db

Table: users
  user_id       INT AUTO_INCREMENT PRIMARY KEY
  username      VARCHAR(50) UNIQUE NOT NULL
  password      VARCHAR(255) NOT NULL
  role          ENUM('Admin', 'Instructor') NOT NULL

Table: students
  student_id    VARCHAR(20) PRIMARY KEY
  firstname     VARCHAR(50) NOT NULL
  lastname      VARCHAR(50) NOT NULL
  course        VARCHAR(50) NOT NULL
  year_level    INT NOT NULL
  section       VARCHAR(10) NOT NULL
  gender        ENUM('Male', 'Female') NOT NULL

Table: subjects
  subject_id    INT AUTO_INCREMENT PRIMARY KEY
  subject_code  VARCHAR(20) UNIQUE NOT NULL
  subject_name  VARCHAR(100) NOT NULL

Table: grades
  grade_id      INT AUTO_INCREMENT PRIMARY KEY
  student_id    VARCHAR(20) NOT NULL  (FK -> students)
  subject_id    INT NOT NULL          (FK -> subjects)
  quiz          DOUBLE DEFAULT 0
  assignment    DOUBLE DEFAULT 0
  exam          DOUBLE DEFAULT 0
  final_grade   DOUBLE DEFAULT 0
  remarks       VARCHAR(10) DEFAULT 'N/A'
  UNIQUE(student_id, subject_id)
```

---

# FOUNDATION (Evolves as Needed)

## Core Systems

* [ ] MySQL database + schema creation script
* [ ] JDBC connection utility (`DatabaseConnection.java`)
* [ ] DAO layer (CRUD operations for each table)

## Utilities

* [ ] Grade computation constants (configurable weights)
* [ ] Common UI helpers (table refresh, input validation)

---

# APPLICATION (Drives Foundation Development)

## Feature: Login System
* [ ] Login form with username/password fields
* [ ] Show/hide password toggle
* [ ] Role-based access (Admin vs. Instructor)
* [ ] Logout functionality

## Feature: Dashboard
* [ ] Summary statistics (total students, subjects, passed, failed)
* [ ] Navigation to other forms

## Feature: Student Management
* [ ] Add / Edit / Delete students
* [ ] Display all students in JTable
* [ ] Search by name or ID

## Feature: Subject Management
* [ ] Add / Edit / Delete subjects
* [ ] Display all subjects in JTable

## Feature: Grade Management (Main Feature)
* [ ] Select student + subject
* [ ] Input quiz, assignment, exam scores
* [ ] Auto-compute final grade using configurable weights
* [ ] Auto-determine Pass/Fail remarks
* [ ] Display grades in JTable

## Feature: Attendance (Nice-to-have)
* [ ] Mark Present / Absent / Late
* [ ] Attendance percentage per student

## Feature: Search & Filter (Nice-to-have)
* [ ] Filter students by section, course
* [ ] Filter grades by subject

## Feature: Print (Nice-to-have)
* [ ] Print class record via JTable.print()

---

# Milestones

## Milestone 1 — Project Setup & Database

> **Goal:** NetBeans project created, MySQL database running, connection verified.

* [x] Step 1.1: Create NetBeans Java Application project
* [x] Step 1.2: Write `schema.sql` to create database and all tables
* [x] Step 1.3: Run SQL script in phpMyAdmin (XAMPP) to create database
* [x] Step 1.4: Add MySQL JDBC driver (Connector/J) to project libraries
* [x] Step 1.5: Create `DatabaseConnection.java` — test that Java can connect to MySQL
* [x] Milestone Complete: Running the app prints "Connected to database successfully!" to console

## Milestone 2 — Login System

> **Why now:** Every other feature requires a logged-in user. This is the entry point.

* [x] Step 2.1: Create `User.java` model class (+ `Role.java` enum)
* [x] Step 2.2: Create `UserDao.java` with `authenticate(username, password)` method
* [x] Step 2.3: Insert a test admin user into the database (already done in `schema.sql`)
* [x] Step 2.4: Create `LoginForm.java` — username field, password field, login button
* [x] Step 2.5: Wire login button to `UserDao.authenticate()` — show success/failure message
* [x] Step 2.6: On successful login, open `DashboardForm` and close `LoginForm`
* [x] Milestone Complete: User can log in with valid credentials and sees the dashboard

## Milestone 3 — Dashboard Shell

> **Why now:** The dashboard is the hub — all other forms launch from here.

* [x] Step 3.1: Create `DashboardForm.java` with navigation buttons (Students, Subjects, Grades)
* [x] Step 3.2: Add placeholder panels/labels for summary stats
* [x] Step 3.3: Add logout button that returns to `LoginForm`
* [x] Milestone Complete: Dashboard opens after login, logout works, navigation buttons exist (even if they don't open forms yet)

## Milestone 4 — Student Management

> **Why now:** Students are the core data — grades and attendance depend on them.

* [x] Step 4.1: Create `Student.java` model class
* [x] Step 4.2: Create `StudentDao.java` with CRUD methods (add, getAll, update, delete, search)
* [x] Step 4.3: Create `StudentForm.java` with input fields and JTable
* [x] Step 4.4: Wire Add button — insert student into DB and refresh table
* [x] Step 4.5: Wire Edit button — select row, populate fields, update DB
* [x] Step 4.6: Wire Delete button — confirm and delete from DB
* [x] Step 4.7: Wire Search — filter JTable by name or ID
* [x] Milestone Complete: Full student CRUD works end-to-end with database persistence

## Milestone 5 — Subject Management

> **Why now:** Grades need both a student and a subject. Subjects come before grades.

* [ ] Step 5.1: Create `Subject.java` model class
* [ ] Step 5.2: Create `SubjectDao.java` with CRUD methods
* [ ] Step 5.3: Create `SubjectForm.java` with input fields and JTable
* [ ] Step 5.4: Wire all CRUD buttons
* [ ] Milestone Complete: Full subject CRUD works end-to-end

## Milestone 6 — Grade Management (Main Feature)

> **Why now:** This is the core deliverable — auto-computed grades.

* [ ] Step 6.1: Create `GradeConstants.java` with configurable weights (QUIZ_WEIGHT, ASSIGNMENT_WEIGHT, EXAM_WEIGHT, PASSING_GRADE)
* [ ] Step 6.2: Create `Grade.java` model class
* [ ] Step 6.3: Create `GradeComputer.java` — computes final grade from components using constants
* [ ] Step 6.4: Create `GradeDao.java` with CRUD methods
* [ ] Step 6.5: Create `GradeForm.java` — dropdowns for student/subject, input fields for scores
* [ ] Step 6.6: Wire auto-compute — when scores are entered, final grade and remarks update automatically
* [ ] Step 6.7: Display all grades in JTable with student name, subject, scores, final grade, remarks
* [ ] Milestone Complete: Grades auto-compute correctly, persist to database, display in table

## Milestone 7 — Dashboard Statistics

> **Why now:** All data exists — now the dashboard can show real numbers.

* [ ] Step 7.1: Add DAO queries for counts (total students, total subjects, passed count, failed count)
* [ ] Step 7.2: Wire dashboard labels to display live counts from database
* [ ] Step 7.3: Auto-refresh stats when dashboard opens
* [ ] Milestone Complete: Dashboard shows accurate, live summary statistics

## Milestone 8 — Polish & Final Touches

> **Why now:** Core features are done. Time to clean up for submission.

* [ ] Step 8.1: Consistent UI styling across all forms (colors, fonts, button sizes)
* [ ] Step 8.2: Input validation on all forms (no empty fields, numeric checks)
* [ ] Step 8.3: Confirmation dialogs for delete operations
* [ ] Step 8.4: Error handling for database failures (user-friendly messages)
* [ ] Milestone Complete: App feels polished and handles edge cases gracefully

---

# Nice-to-Have Milestones (If Time Permits)

## Milestone 9 — Attendance

* [ ] Step 9.1: Create attendance table in database
* [ ] Step 9.2: Create `Attendance.java` model + `AttendanceDao.java`
* [ ] Step 9.3: Create `AttendanceForm.java` — mark Present/Absent/Late per student per date
* [ ] Step 9.4: Show attendance percentage
* [ ] Milestone Complete: Attendance tracking works end-to-end

## Milestone 10 — Print Reports

* [ ] Step 10.1: Add print button to `GradeForm`
* [ ] Step 10.2: Use `JTable.print()` to print class record
* [ ] Milestone Complete: Instructor can print a grade sheet

---

# Common Pitfalls

* [ ] Overengineering too early — build the simplest thing that works first
* [ ] Hardcoding SQL into UI classes — always go through DAO layer
* [ ] Skipping database connection cleanup — always close resources
* [ ] Jumping to advanced features (PDF export, etc.) before core works
* [ ] Building all forms at once instead of one-at-a-time with verification

---

# Long-Term Growth (Future Milestones)

## Architectural Trajectory

```
Current               Near-term                  Long-term
-------               ---------                  ---------
Single user      ->   Multi-instructor login ->   Web-based version
Local MySQL      ->   Same                   ->   Cloud database
Desktop Swing    ->   Same                   ->   Web UI (Spring Boot)
Manual grades    ->   Auto-compute           ->   Import from CSV/Excel
```

## Future Milestones (Beyond School Project)

* [ ] **M11 — Export:** PDF/Excel export using Apache POI / iTextPDF
* [ ] **M12 — Advanced Attendance:** Calendar view, automated late detection
* [ ] **M13 — Multi-term:** Support midterm/finals/general average computation

---

# Notes for Future You

* Always prioritize **small working steps**
* Keep changes incremental
* Prefer simple solutions -> refactor later
* Foundation evolves only when required by the application
* Avoid unnecessary abstraction early
* The grade formula weights are in `GradeConstants.java` — change them there when the instructor confirms

---

# Progress Log

* [x] Project initialized
* [x] Milestone 1 complete — database connected
* [x] Milestone 2 complete — login works
* [x] Milestone 3 complete — dashboard shell
* [x] Milestone 4 complete — student CRUD
* [ ] Milestone 5 complete — subject CRUD
* [ ] Milestone 6 complete — grade auto-compute
* [ ] Milestone 7 complete — dashboard stats
* [ ] Milestone 8 complete — polished for submission

---

**End of roadmap**
