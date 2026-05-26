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
      Assessment.java       — assessment data (studentId, subjectId, season, name, score)
      GradingSeason.java    — enum: PRELIM, MIDTERM, PRE_FINAL, FINAL
      ScoreResult.java      — computed grade result (finalGrade, remarks)
      User.java             — login credentials and role
      Role.java             — enum: ADMIN, INSTRUCTOR
    dao/                  — Data Access Objects (JDBC operations)
      DatabaseConnection.java — MySQL connection utility
      StudentDao.java       — student CRUD queries
      SubjectDao.java       — subject CRUD queries
      AssessmentDao.java    — assessment CRUD queries (by season)
      UserDao.java          — user authentication queries
      DashboardDao.java     — dashboard summary count queries (uses AVG aggregation)
    service/              — Business logic
      GradeComputer.java    — computes average from assessment lists
    ui/                   — Java Swing forms
      LoginForm.java        — login screen
      DashboardForm.java    — main menu with summary stats
      DashboardStatsPanel.java — dashboard summary statistics display
      StudentForm.java      — student management (CRUD + JTabbedPane by section)
      StudentInputPanel.java   — student input fields (extracted atom)
      SectionTablePanel.java   — reusable section table (styled JTable atom)
      SubjectForm.java      — subject management (CRUD + JTable)
      GradeForm.java        — assessment management (CRUD + JTabbedPane by season)
      AssessmentInputPanel.java — assessment input fields + dropdowns (extracted atom)
    util/                 — Shared utilities
      GradeConstants.java   — passing grade threshold + score bounds
      StyleConstants.java   — shared UI styling (fonts, borders, gaps, colors)
  sql/
    schema.sql            — database creation script (assessments table)
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

Table: assessments
  assessment_id    INT AUTO_INCREMENT PRIMARY KEY
  student_id       VARCHAR(20) NOT NULL  (FK -> students)
  subject_id       INT NOT NULL          (FK -> subjects)
  season           ENUM('Prelim', 'Midterm', 'Pre-Final', 'Final') NOT NULL
  assessment_name  VARCHAR(50) NOT NULL
  score            DOUBLE NOT NULL DEFAULT 0
  UNIQUE(student_id, subject_id, season, assessment_name)
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

* [x] Step 5.1: Create `Subject.java` model class
* [x] Step 5.2: Create `SubjectDao.java` with CRUD methods
* [x] Step 5.3: Create `SubjectForm.java` with input fields and JTable
* [x] Step 5.4: Wire all CRUD buttons
* [x] Milestone Complete: Full subject CRUD works end-to-end

## Milestone 6 — Grade Management (Main Feature)

> **Why now:** This is the core deliverable — auto-computed grades.

* [x] Step 6.1: Create `GradeConstants.java` with configurable weights (QUIZ_WEIGHT, ASSIGNMENT_WEIGHT, EXAM_WEIGHT, PASSING_GRADE)
* [x] Step 6.2: Create `Grade.java` model class (+ `ScoreResult.java`)
* [x] Step 6.3: Create `GradeComputer.java` — computes final grade from components using constants
* [x] Step 6.4: Create `GradeDao.java` with CRUD methods (+ `GradeRecord.java`)
* [x] Step 6.5: Create `GradeForm.java` — dropdowns for student/subject, input fields for scores (+ `GradeInputPanel.java`)
* [x] Step 6.6: Wire auto-compute — when scores are entered, final grade and remarks update automatically. Wire all CRUD buttons (Add, Edit, Delete, Search)
* [x] Step 6.7: Display all grades in JTable with student name, subject, scores, final grade, remarks
* [x] Milestone Complete: Grades auto-compute correctly, persist to database, display in table

## Milestone 7 — Dashboard Statistics

> **Why now:** All data exists — now the dashboard can show real numbers.

* [x] Step 7.1: Create `DashboardDao.java` with count queries (total students, total subjects, passed count, failed count)
* [x] Step 7.2: Wire dashboard labels to display live counts from database (via `DashboardStatsPanel`)
* [x] Step 7.3: Auto-refresh stats when dashboard opens (`statsPanel.refresh()` in constructor)
* [x] Milestone Complete: Dashboard shows accurate, live summary statistics

## Milestone 8 — Polish & Final Touches

> **Why now:** Core features are done. Time to clean up for submission.

* [x] Step 8.1: Consistent UI styling via `StyleConstants` (fonts, borders, gaps standardized across all forms)
* [x] Step 8.2: Input validation on all forms (no empty fields, numeric checks — year level 1-4, scores 0-100)
* [x] Step 8.3: Confirmation dialogs for delete operations (already implemented in Milestones 4-6)
* [x] Step 8.4: Error handling for database failures (DashboardDao returns -1, panel shows "Error")
* [x] Milestone Complete: App feels polished and handles edge cases gracefully

## Milestone 9 — Section Organization & Assessment-Based Grading

> **Goal:** Replace the flat grade table with section-organized student views and a flexible assessment-based grading system with grading seasons.

* [x] Step 9.1: Create `GradingSeason` enum (PRELIM, MIDTERM, PRE_FINAL, FINAL) and `Assessment` model class (assessmentId, studentId, subjectId, season, assessmentName, score)
* [x] Step 9.2: Replace `grades` table with `assessments` table in schema.sql — flexible per-assessment scores instead of fixed quiz/assignment/exam columns
* [x] Step 9.3: Create `AssessmentDao.java` with CRUD + getBySeason + search
* [x] Step 9.4: Update `GradeComputer` to compute simple average from assessment lists. Simplify `GradeConstants` to only PASSING_GRADE + score bounds
* [x] Step 9.5: Create `SectionTablePanel` atom — styled JTable with alternating row colors and dark header
* [x] Step 9.6: Refactor `StudentForm` to use JTabbedPane — "All" tab + dynamic tabs per section. Sections extracted from student data automatically
* [x] Step 9.7: Create `AssessmentInputPanel` (season dropdown, assessment name, score) and refactor `GradeForm` with season tabs (Prelim, Midterm, Pre-Final, Final) each showing filtered assessments + season average
* [x] Step 9.8: Update `DashboardDao` to compute passed/failed from `AVG(score)` aggregation on assessments table. Update `StyleConstants` with modern color palette (slate grays, muted blues, alternating row colors)
* [x] Step 9.9: Remove old files (Grade.java, GradeRecord.java, GradeDao.java, GradeInputPanel.java). Update md files.
* [x] Milestone Complete: Students organized by section, grades organized by season with flexible assessment types

---

# Nice-to-Have Milestones (If Time Permits)

## Milestone 10 — Attendance

* [ ] Step 9.1: Create attendance table in database
* [ ] Step 9.2: Create `Attendance.java` model + `AttendanceDao.java`
* [ ] Step 9.3: Create `AttendanceForm.java` — mark Present/Absent/Late per student per date
* [ ] Step 9.4: Show attendance percentage
* [ ] Milestone Complete: Attendance tracking works end-to-end

## Milestone 11 — Print Reports

* [ ] Step 11.1: Add print button to `GradeForm`
* [ ] Step 11.2: Use `JTable.print()` to print class record
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

* [ ] **M12 — Export:** PDF/Excel export using Apache POI / iTextPDF
* [ ] **M13 — Advanced Attendance:** Calendar view, automated late detection

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
* [x] Milestone 5 complete — subject CRUD
* [x] Milestone 6 complete — grade auto-compute
* [x] Milestone 7 complete — dashboard stats
* [x] Milestone 8 complete — polished for submission
* [x] Milestone 9 complete — section tabs + assessment-based grading with seasons

---

**End of roadmap**
