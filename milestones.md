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
      Assessment.java       — assessment data (studentId, subjectId, season, name, score, totalItems, date)
      GradingSeason.java    — enum: PRELIM, MIDTERM, PRE_FINAL, FINAL
      ScoreResult.java      — computed grade result (finalGrade, remarks)
      User.java             — login credentials and role
      Attendance.java        — attendance data (studentId, subjectId, date, status)
      AttendanceStatus.java  — enum: PRESENT, ABSENT, LATE, EXCUSED
      Enrollment.java        — enrollment data (studentId, subjectId)
      Role.java             — enum: ADMIN, INSTRUCTOR
    dao/                  — Data Access Objects (JDBC operations)
      DatabaseConnection.java — MySQL connection utility
      StudentDao.java       — student CRUD queries
      SubjectDao.java       — subject CRUD queries
      AssessmentDao.java    — assessment CRUD queries (by season)
      AttendanceDao.java     — attendance queries (saveOrUpdate, getBySubjectAndDate, counts)
      EnrollmentDao.java     — enrollment queries (enroll, unenroll, getBySubjectAndSection)
      UserDao.java          — user authentication queries
      DashboardDao.java     — dashboard summary count queries (uses AVG aggregation)
    service/              — Business logic
      GradeComputer.java    — computes average from assessment lists
    ui/                   — Java Swing forms
      LoginForm.java        — login screen
      DashboardForm.java    — main menu with summary stats
      DashboardStatsPanel.java — dashboard summary statistics display
      StudentForm.java      — student management (CRUD + JTabbedPane by section)
      StudentFilterPanel.java  — subject filter + search field (extracted atom)
      StudentInputPanel.java   — student input fields (extracted atom)
      SectionTablePanel.java   — reusable section table (styled JTable atom)
      SubjectForm.java      — subject management (CRUD + JTable)
      AttendanceForm.java    — attendance management (status dropdowns by section + date)
      AttendanceFilterPanel.java — subject + section + date filters (extracted atom)
      EnrollmentForm.java    — enrollment management (checkbox table by section)
      EnrollmentFilterPanel.java — subject + section dropdown filters (extracted atom)
      GradeForm.java        — grade view (JTabbedPane by season + Final Grade, edit score button)
      GradeFilterPanel.java    — course/section filter + status filter + search field (extracted atom)
      EditAssessmentDialog.java — modal dialog for editing score, total items, and date
      BatchScoreEntryForm.java  — batch score entry (subject + section + assessment → editable score table)
      BatchScoreFilterPanel.java — subject + course/section + season + assessment name filters
      StudentGradeSummaryForm.java — per-student report card with section filter + student list sidebar
    util/                 — Shared utilities
      GradeConstants.java   — passing grade threshold + score bounds + default total items
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

Table: enrollments
  enrollment_id  INT AUTO_INCREMENT PRIMARY KEY
  student_id     VARCHAR(20) NOT NULL  (FK -> students)
  subject_id     INT NOT NULL          (FK -> subjects)
  UNIQUE(student_id, subject_id)

Table: attendance
  attendance_id  INT AUTO_INCREMENT PRIMARY KEY
  student_id     VARCHAR(20) NOT NULL  (FK -> students)
  subject_id     INT NOT NULL          (FK -> subjects)
  date           DATE NOT NULL
  status         ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL
  UNIQUE(student_id, subject_id, date)

Table: assessments
  assessment_id    INT AUTO_INCREMENT PRIMARY KEY
  student_id       VARCHAR(20) NOT NULL  (FK -> students)
  subject_id       INT NOT NULL          (FK -> subjects)
  season           ENUM('Prelim', 'Midterm', 'Pre-Final', 'Final') NOT NULL
  assessment_name  VARCHAR(50) NOT NULL
  score            DOUBLE NOT NULL DEFAULT 0
  total_items      DOUBLE NOT NULL DEFAULT 100
  date             DATE DEFAULT NULL
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

# Upcoming Milestones (Instructor Review Recommendations)

> **Context:** These milestones come from an honest review of the system from the perspective of an instructor who would use it day-to-day. Organized by priority — P0 items are dealbreakers that prevent real-world use, P1 items make the system genuinely useful, P2 items improve daily usability, P3 items are for future growth.

---

## Milestone 10 — Enrollment & Attendance Tracking (P0 — Must Have)

> **Why P0:** A "Class Record" without attendance is only half complete. ACLC requires attendance reporting. Without this, instructors still need a separate system. Enrollment links students to subjects — a prerequisite for meaningful attendance and grade filtering.

* [x] Step 10.1: Design `enrollments` and `attendance` tables in schema.sql — enrollments links students to subjects (unique student_id + subject_id), attendance tracks per-date status (unique student_id + subject_id + date)
* [x] Step 10.2: Create `AttendanceStatus.java` enum (PRESENT, ABSENT, LATE, EXCUSED), `Attendance.java` model (5 fields), and `Enrollment.java` model (3 fields)
* [x] Step 10.3: Create `EnrollmentDao.java` — enroll, unenroll, isEnrolled, getBySubject, getStudentsBySubjectAndSection, getSectionsBySubject
* [x] Step 10.4: Create `AttendanceDao.java` — add, update, saveOrUpdate (upsert), getBySubjectAndDate, getByStudent, countByStatus, countTodayPresent, countTodayTotal
* [x] Step 10.5: Create `EnrollmentForm.java` + `EnrollmentFilterPanel.java` — checkbox table by subject + section, bulk enroll/unenroll buttons, save compares checkbox state to DB and enrolls/unenrolls accordingly
* [x] Step 10.6: Create `AttendanceForm.java` + `AttendanceFilterPanel.java` — subject + section + date filters, status dropdowns per student, "Mark All Present" bulk button, only shows enrolled students
* [x] Step 10.7: Add "Enrollment" and "Attendance" navigation buttons to DashboardForm
* [x] Step 10.8: Update DashboardStatsPanel — added Enrolled count and "Today's Attendance: X / Y" display. Grid layout expanded to 3x2.
* [x] Milestone Complete: Instructor can enroll students in subjects, mark attendance per date per subject (only enrolled students), view attendance on dashboard

## Milestone 11 — Grade Export & Printing (P0 — Must Have)

> **Why P0:** Without export/print, all data entry has no output. Instructors need to submit grades on paper or as files. This is the #1 dealbreaker.

* [x] Step 11.1: Add "Print" button to GradeForm — use `JTable.print()` for quick printing of the current season tab
* [x] Step 11.2: Add "Export CSV" button to GradeForm — export current tab's grades to CSV file (simple, no extra libraries needed)
* [x] Step 11.3: Add a file chooser dialog (JFileChooser) so instructor picks where to save the export
* [x] Step 11.4: CSV exports all columns from the current tab with proper escaping (commas, quotes, newlines handled)
* [~] Step 11.5: (Optional — skipped) Excel export via Apache POI deferred. CSV is sufficient for submission.
* [x] Milestone Complete: Instructor can print or export grades to a file for submission

## Milestone 12 — Final Grade Across Seasons (P1 — Should Have)

> **Why P1:** Right now each season shows its own average, but there's no combined final grade. Instructors need ONE final grade per student per subject that combines all four seasons. In ACLC, seasons are typically weighted differently (e.g., Prelim 20%, Midterm 20%, Pre-Final 20%, Final 40%).

* [x] Step 12.1: Add season weight constants to `GradeConstants.java` — PRELIM_WEIGHT, MIDTERM_WEIGHT, PRE_FINAL_WEIGHT, FINAL_WEIGHT (default: 0.20, 0.20, 0.20, 0.40)
* [x] Step 12.2: Add `computeFinalGrade(Map<GradingSeason, List<Assessment>>)` method to `GradeComputer` — computes weighted average across all seasons
* [x] Step 12.3: Add a "Final Grade" tab or summary panel in GradeForm — shows one row per student-subject pair with: Prelim avg, Midterm avg, Pre-Final avg, Final avg, Weighted Final Grade, Remarks
* [x] Step 12.4: Color-code the final grade (green for passed, red for failed)
* [x] Step 12.5: Update DashboardDao passed/failed counts to use the weighted final grade instead of simple average
* [x] Milestone Complete: Instructor sees a combined final grade per student per subject with configurable season weights

## Milestone 13 — Student Grade Summary / Report Card View (P1 — Should Have)

> **Why P1:** Instructors often need to look up ONE student and see all their grades across ALL subjects and ALL seasons in one view — like a report card. Currently you can only see grades organized by season, not by student.

* [x] Step 13.1: Add `getByStudent(studentId)` to AssessmentDao — returns all assessments for one student
* [x] Step 13.2: Create `StudentGradeSummaryForm.java` — student dropdown at top, report card table below (Subject Code, Subject Name, Prelim, Midterm, Pre-Final, Final, Final Grade, Remarks)
* [x] Step 13.3: Color-code Final Grade and Remarks columns (green for PASSED, red for FAILED)
* [x] Step 13.4: Add Print + Export CSV buttons (reuse export pattern from Milestone 11)
* [x] Step 13.5: Add navigation — "Student Summary" button on DashboardForm + "View Grades" button on StudentForm (opens with preselected student)
* [x] Milestone Complete: Instructor can pull up any student and see their complete grade picture across all subjects

## Milestone 14 — UX Improvements (P2 — Nice to Have)

> **Why P2:** These are small usability improvements that reduce daily friction. Each step is independent — implement in any order.

* [x] Step 14.1: **Back to Dashboard button** — Already implemented on all forms during their respective milestones
* [x] Step 14.2: **Success confirmation messages** — Added `showSuccess()` helper + success dialogs after Add/Edit in StudentForm, SubjectForm, GradeForm. EnrollmentForm and AttendanceForm already had success messages.
* [x] Step 14.3: **Column sorting** — Added `setAutoCreateRowSorter(true)` to all JTables (SubjectForm, SectionTablePanel, GradeForm season + final tabs, EnrollmentForm, StudentGradeSummaryForm). Fixed GradeForm parallel list lookup to use `convertRowIndexToModel()`.
* [~] Step 14.4: **Student ID auto-generation** — Skipped. Students have school-assigned IDs that instructors type manually.
* [~] Step 14.5: **Date timestamp on assessments** — Skipped. Low-value for demo; date column adds visual noise without changing functionality.
* [x] Step 14.6: **Confirmation before closing forms with unsaved changes** — Added unsaved changes detection to StudentForm, SubjectForm, GradeForm. Back button and window close (X) both check for unsaved input and confirm before discarding.
* [x] Milestone Complete: Daily workflow is smoother with fewer clicks and clearer feedback

## Milestone 15 — Advanced Filtering (P2 — Nice to Have)

> **Why P2:** The current keyword search works, but instructors often need to filter by specific criteria — "show me all failed students in Section A for this subject."

* [x] Step 15.1: Add a section filter dropdown to GradeForm — filter assessments to show only students from a specific section. Created `GradeFilterPanel` atom (section dropdown + search field). Added `getAllSections()` to StudentDao. In-memory filtering via student-to-section map.
* [x] Step 15.2: Add a pass/fail filter toggle to GradeForm — status dropdown (All Results / Passed Only / Failed Only). Season tabs filter by per-student-subject season average. Final Grade tab filters by weighted final grade.
* [x] Step 15.3: Add a subject filter to StudentForm — "show students enrolled in this subject". Created `StudentFilterPanel` atom (subject dropdown + search field). Added `getStudentsBySubject()` to EnrollmentDao. Section tabs rebuild from filtered student set.
* [x] Step 15.4: Add a date range filter to AttendanceForm — "From" and "To" date fields. Single date = editable entry mode (existing behavior). Date range = read-only summary view with Date column. Added `getBySubjectSectionAndDateRange()` to AttendanceDao.
* [x] Milestone Complete: Instructors can slice data by section, pass/fail status, subject, and date range

## Milestone 16 — Batch Score Entry & Enrollment UX (P2 — Nice to Have)

> **Why P2:** Entering scores one-by-one through GradeForm is slow for a full class. Batch entry lets the instructor pick one assessment and fill in scores for all enrolled students at once. Enrollment UX improved with a select-all checkbox.

* [x] Step 16.1: **Enrollment select-all checkbox** — Added "Select All" checkbox in the Enroll column header. Clicking toggles all row checkboxes on/off. Removed redundant Enroll All / Unenroll All buttons. Split `EnrollmentFilterPanel.addFilterListener()` into `addSubjectListener()` + `addSectionListener()`.
* [x] Step 16.2: **BatchScoreFilterPanel** — Created filter panel with Subject, Section, Season, Assessment Name fields + "Load Students" button.
* [x] Step 16.3: **BatchScoreEntryForm** — Editable table of enrolled students with Score column. Existing scores pre-populated. "Save All" saves via `AssessmentDao.saveOrUpdate()` (upsert by student + subject + season + name). Empty rows skipped. Summary dialog shows saved/skipped counts.
* [x] Step 16.4: **AssessmentDao.saveOrUpdate()** — Checks if assessment exists (by student + subject + season + name), updates if found, inserts if not.
* [x] Step 16.5: **Dashboard navigation** — Added "Batch Score Entry" button. Changed DashboardForm layout from FlowLayout to GridLayout (2x4) to accommodate growing navigation.
* [x] Milestone Complete: Instructor can enter scores for an entire class in one screen instead of adding assessments one-by-one

## Milestone 17 — Security Hardening (P3 — Future)

> **Why P3:** Important for production but not critical for a school project demo. Address before any real deployment.

* [ ] Step 16.1: **Hash passwords** — Use Java's built-in `MessageDigest` (SHA-256) or add bcrypt library. Hash on registration/insert, compare hashes on login. Update UserDao.authenticate() accordingly.
* [ ] Step 16.2: **Externalize database credentials** — Move URL, username, password from hardcoded values in `DatabaseConnection.java` to a `config.properties` file. Load with `Properties` class.
* [ ] Step 16.3: **Session timeout** — If the app is idle for 30 minutes, auto-logout and return to LoginForm. Use a Swing Timer.
* [ ] Step 16.4: **Audit trail** — Create an `audit_log` table (user_id, action, target_table, target_id, timestamp). Log all Add/Edit/Delete operations. Show in an admin-only Audit Log form.
* [ ] Milestone Complete: Passwords are hashed, credentials are externalized, actions are logged

## Milestone 18 — Instructor-Section-Subject Linking (P3 — Future)

> **Why P3:** Only matters if multiple instructors use the system. Currently all data is visible to all users. This milestone restricts each instructor to see only "their" students and subjects.

* [ ] Step 17.1: Create `instructor_subjects` junction table (user_id FK, subject_id FK, section) — links an instructor to the subjects and sections they teach
* [ ] Step 17.2: Create `InstructorSubjectDao.java` — getSubjectsForInstructor, getSectionsForInstructor
* [ ] Step 17.3: Filter GradeForm dropdowns to show only the logged-in instructor's subjects and sections
* [ ] Step 17.4: Filter StudentForm to show only students in the logged-in instructor's sections
* [ ] Step 17.5: Admin role bypasses filters and sees everything
* [ ] Milestone Complete: Each instructor sees only their own classes; admin sees everything

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

* [ ] **M19 — Web Migration:** Migrate to Spring Boot + Thymeleaf for multi-user web deployment
* [ ] **M20 — CSV/Excel Import:** Allow importing student lists and grades from spreadsheets
* [ ] **M21 — Advanced Attendance:** Calendar view, automated late detection, SMS/email notifications
* [ ] **M22 — Database Connection Pooling:** Replace per-query connections with HikariCP connection pool

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
* [x] Milestone 10 complete — enrollment & attendance tracking with section-based workflow
* [x] Milestone 11 — Grade Export & Printing (P0)
* [x] Milestone 12 — Final Grade Across Seasons (P1)
* [x] Milestone 13 — Student Grade Summary / Report Card (P1)
* [x] Milestone 14 — UX Improvements (P2)
* [x] Milestone 15 — Advanced Filtering (P2)
* [x] Milestone 16 — Batch Score Entry & Enrollment UX (P2)
* [x] T5-T7 — Batch defaults, section combobox, course-section labels
* [ ] Milestone 17 — Security Hardening (P3)
* [ ] Milestone 18 — Instructor-Section-Subject Linking (P3)

---

**End of roadmap**
