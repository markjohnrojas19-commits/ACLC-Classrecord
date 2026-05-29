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
2. `StudentForm` shows a JTabbedPane with tabs: "All" (all students) plus one tab per section (e.g., "Section A", "Section B"). Each tab contains a `SectionTablePanel` with a styled JTable.
3. User fills in fields (Student ID, first name, last name, course, year level, section, gender) in the `StudentInputPanel` and clicks "Add."
4. `StudentForm` creates a `Student` object from the input fields.
5. `StudentForm` calls `StudentDao.add(student)`.
6. `StudentDao` opens a JDBC connection, runs an `INSERT` query with a `PreparedStatement`.
7. On success, `StudentForm` rebuilds all section tabs — the new student appears in the "All" tab and in its section's tab.
8. Input fields are cleared, ready for the next entry.

**Section tabs are dynamic.** When a student is added with a new section value (e.g., "C"), a new tab "Section C" appears automatically. Sections are extracted from the current student data — no separate sections table needed.

**The same pattern applies to Edit and Delete** — the form calls the appropriate DAO method, then rebuilds all tabs.

**Delete cascading:** When a student is deleted, `StudentDao.delete()` first removes all related rows from the `grades` and `assessments` tables, then deletes the student. This is done in code (not via `ON DELETE CASCADE` in the schema) so the behavior is explicit and readable. All three deletes use the same JDBC connection.

---

## What happens when a subject is added?

1. User navigates from `DashboardForm` to `SubjectForm`.
2. User fills in Subject Code and Subject Name, then clicks "Add."
3. `SubjectForm` creates a `Subject` object with `id = 0` (placeholder — the database auto-generates the real ID).
4. `SubjectForm` calls `SubjectDao.add(subject)`.
5. `SubjectDao` opens a JDBC connection, runs an `INSERT` with only `subject_code` and `subject_name` (omits `subject_id` — it's `AUTO_INCREMENT`).
6. On success, `SubjectForm` refreshes the JTable by calling `SubjectDao.getAll()` and rebuilding the table model.
7. Input fields are cleared, ready for the next entry.

**The same pattern applies to Edit and Delete** — the form calls the appropriate DAO method, then refreshes the table. Edit reads the `subject_id` from the selected table row to preserve identity. Delete confirms via dialog before removing.

**Key difference from Student:** Subject has no separate input panel. StudentForm extracted `StudentInputPanel` because 7 input fields would have pushed the form past the class field limit. SubjectForm has only 2 input fields, keeping it at 5 total fields — well within the atom limit.

---

## What happens when assessments are entered and grades computed?

This is the core workflow of the system.

1. User navigates to `GradeForm`.
2. `GradeForm` shows a JTabbedPane with 4 season tabs: **Prelim**, **Midterm**, **Pre-Final**, **Final**. Each tab contains a styled JTable of assessments for that season, plus a season average label at the bottom.
3. User selects a student (dropdown populated by `StudentDao.getAll()`) and a subject (dropdown populated by `SubjectDao.getAll()`).
4. User selects a grading season (e.g., "Midterm"), types an assessment name (e.g., "Quiz 1"), and enters a score (0-100).
5. User clicks "Add." `GradeForm` creates an `Assessment` object and calls `AssessmentDao.add(assessment)`.
6. The assessment persists to the database. All four season tabs refresh — the new assessment appears in the correct season tab.
7. The season average label updates automatically. `GradeComputer.computeAverage(assessments)` calculates the simple average of all assessment scores in that season and determines "PASSED" (>= 75) or "FAILED" (< 75).
8. The average label is color-coded: green for PASSED, red for FAILED.

**Assessment examples:**
- Student STU001, Subject CS101, Midterm season: "Quiz 1" = 85, "Unit Test A" = 90, "Project" = 92
- Each is a separate row in the `assessments` table
- The Midterm tab shows all three with a season average of 89.00 — PASSED

**Why Assessment replaces Grade:** The old model had fixed columns (quiz, assignment, exam) — rigid and couldn't represent arbitrary assessment types. The new model stores one row per assessment with a name and score, allowing unlimited assessment types per season.

**Why GradeComputer is separate from AssessmentDao:** Computation is business logic. Database access is infrastructure. They are different atoms with different reasons to change. If the averaging formula changes, only `GradeComputer` changes. If the database schema changes, only `AssessmentDao` changes.

---

## What happens when the dashboard loads?

1. `DashboardForm` opens (after login or when navigating back).
2. It queries the database for summary counts via `DashboardDao`:
   - `DashboardDao.countStudents()` — total students
   - `DashboardDao.countSubjects()` — total subjects
   - `DashboardDao.countPassed()` — student-subject pairs where `AVG(score) >= 75.0`
   - `DashboardDao.countFailed()` — student-subject pairs where `AVG(score) < 75.0`
3. These counts are displayed in labels on the dashboard (Passed in green, Failed in red).
4. Navigation buttons allow the user to open `StudentForm`, `SubjectForm`, or `GradeForm`.

**How passed/failed counts work with weighted grades:** The dashboard uses SQL conditional aggregation to compute the same weighted formula as `GradeComputer.computeFinalGrade()`. For each student-subject pair: `COALESCE(AVG(CASE WHEN season = 'Prelim' THEN score END), 0) * 0.20 + ... + COALESCE(AVG(CASE WHEN season = 'Final' THEN score END), 0) * 0.40`. The `HAVING` clause checks if this weighted grade is >= 75 (passed) or < 75 (failed). Season weights are passed as PreparedStatement parameters from `GradeConstants`, keeping the SQL and Java computations in sync.

**Why DashboardDao is separate from StudentDao/SubjectDao/AssessmentDao:** The dashboard counts are a display concern — aggregate queries for a summary view. They don't belong in the CRUD DAOs because those atoms exist to manage individual records. `DashboardDao` is its own atom with a single responsibility: provide dashboard statistics.

---

## How does the database connection work without forms knowing about JDBC?

`DatabaseConnection` is a utility class with a single static method: `getConnection()`. It returns a `java.sql.Connection` to the MySQL database.

- **Only DAO classes call `DatabaseConnection.getConnection()`.** No form, service, or model class ever touches it.
- **Connection details (URL, username, password) live in one place.** If the database moves, only `DatabaseConnection.java` changes.

**The test:** If you delete `DatabaseConnection.java`, only the DAO classes fail to compile. No form, no model, no service is affected.

---

## What's the difference between Service and DAO?

- **DAO (Data Access Object)** — Knows how to read and write data to/from MySQL. Speaks SQL. One DAO per table. Its job: translate between Java objects and database rows.
- **Service** — Knows business rules. Does NOT speak SQL. Its job: compute grade averages, apply domain logic. Calls DAOs when it needs data.

Think of it as: DAO is the librarian (fetches and files books), Service is the teacher (decides what grade a student gets based on the scores the librarian retrieved).

In this project, the service layer is thin (mainly `GradeComputer`), because the business logic is straightforward. That's fine — don't add abstraction just to have it. The layer exists so that computation logic and database logic don't live in the same class.

---

## How do section tabs work?

`StudentForm` uses a `JTabbedPane` to organize students by section.

1. On load (or after any CRUD operation), `StudentForm` calls `loadStudents()` — which returns all students (`StudentDao.getAll()`) or only enrolled students (`EnrollmentDao.getStudentsBySubject()`) depending on the subject filter.
2. It extracts all unique section values from the student list using a `LinkedHashSet` (preserves insertion order).
3. It creates one `SectionTablePanel` per section, plus an "All" tab showing every student.
4. Each `SectionTablePanel` is a self-contained atom — a `JPanel` holding a styled `JTable` with alternating row colors and dark header.
5. Selecting a row in any tab populates the `StudentInputPanel` for editing.
6. After Add/Edit/Delete, all tabs rebuild from fresh data.

**Subject filtering:** `StudentFilterPanel` sits between the input panel and the section tabs. It provides a subject dropdown ("All Subjects" + list from `SubjectDao.getAll()`) and a search field. When a subject is selected, `loadStudents()` queries `EnrollmentDao.getStudentsBySubject()` instead of `StudentDao.getAll()`, so only enrolled students appear. Section tabs rebuild from the filtered set — the instructor sees only students enrolled in e.g. "CS101", organized by section. Search also respects the subject filter via `filterBySubject()`.

**Dynamic behavior:** If the only student in "Section C" is deleted, the "Section C" tab disappears. If a new student is added to "Section D", a "Section D" tab appears. No manual section management needed.

---

## How do season tabs work in grade management?

`GradeForm` uses a `JTabbedPane` with 4 fixed tabs — one per `GradingSeason` enum value (Prelim, Midterm, Pre-Final, Final).

1. On load, `GradeForm` calls `AssessmentDao.getAll()` and filters assessments by season.
2. Each tab shows a JTable with columns: ID, Student, Subject, Assessment Name, Score.
3. Below each table, a label shows the season average computed by `GradeComputer.computeAverage()`.
4. The average label is color-coded: green (PASSED) if >= 75, red (FAILED) if < 75.
5. Selecting a row in any tab populates the `AssessmentInputPanel` (student, subject, season, assessment name, score).
6. The season dropdown in the input panel auto-selects to match the active tab's season when loading from a row selection.

**Section and status filtering:** `GradeFilterPanel` sits between the input panel and the season tabs. It provides three controls: a section dropdown ("All Sections" + dynamic list from `StudentDao.getAllSections()`), a status dropdown ("All Results" / "Passed Only" / "Failed Only"), and a search field. When a section is selected, `filterBySection()` builds a student-to-section map and removes assessments whose students don't belong to that section. When a status is selected, `filterByStatus()` groups assessments by student-subject, computes each group's average, and keeps only groups matching the pass/fail filter. On the Final Grade tab, `matchesStatusFilter()` checks the weighted final grade instead. All filters compose — selecting "Section A" + "Failed Only" shows only failing students in Section A across all tabs.

**Assessment storage:** Each assessment is one row: (student_id, subject_id, season, assessment_name, score). The UNIQUE constraint prevents duplicate assessment names within the same student-subject-season combination.

### How does the Final Grade tab work?

The 5th tab in `GradeForm` — "Final Grade" — shows a summary of weighted grades across all four seasons.

1. When the tab populates, `populateFinalGradeTab()` groups all assessments by student-subject pair using a composite key (`studentId|subjectId`).
2. For each pair, it further groups assessments by `GradingSeason`.
3. Each season's simple average is computed via `GradeComputer.computeAverage()`.
4. The weighted final grade is computed via `GradeComputer.computeFinalGrade()` — Prelim × 0.20 + Midterm × 0.20 + Pre-Final × 0.20 + Final × 0.40.
5. One row per student-subject pair: Student, Subject, Prelim avg, Midterm avg, Pre-Final avg, Final avg, Weighted Final Grade, Remarks.
6. The Final Grade and Remarks columns are color-coded: green for PASSED (>= 75), red for FAILED (< 75).

**Read-only tab.** The Final Grade tab is a summary view. Clicking a row does not populate the input panel (the `getSelectedAssessment()` guard returns null for tab index >= 4). To edit individual assessments, use the season tabs.

**Missing seasons show 0.00.** If a student has no assessments in a season, that season's average is 0.00 and contributes 0 to the weighted total.

---

## What happens when students are enrolled in a subject?

Enrollment is the bridge between students and subjects. Before a student can have attendance marked or grades entered for a subject, they must be enrolled.

1. User navigates to `EnrollmentForm` from the dashboard.
2. `EnrollmentFilterPanel` shows two dropdowns: Subject and Section. Subjects come from `SubjectDao.getAll()`. Sections come from `StudentDao` (all distinct sections in the students table).
3. User selects a subject and section. `EnrollmentForm` loads all students in that section and shows them in a JTable with a checkbox column.
4. For each student, the checkbox is pre-populated: checked if `EnrollmentDao.isEnrolled(studentId, subjectId)` returns true.
5. User checks/unchecks students and clicks "Save."
6. Save logic compares each row's checkbox state to the database:
   - Checked + not enrolled → `EnrollmentDao.enroll(studentId, subjectId)`
   - Unchecked + enrolled → `EnrollmentDao.unenrollByStudentAndSubject(studentId, subjectId)`
   - No change → skip
7. A summary dialog shows how many were enrolled/unenrolled.

**Bulk operations:** The "Enroll" column header contains a "Select All" checkbox — checking it toggles all visible rows on/off. Changes don't persist until "Save" is clicked.

---

## What happens when scores are entered in batch?

Batch Score Entry is the fastest way to enter grades for a whole class at once — instead of adding one assessment at a time in GradeForm.

1. User navigates to `BatchScoreEntryForm` from the dashboard.
2. `BatchScoreFilterPanel` shows four controls: Subject, Section (populated from enrolled sections), Season, and Assessment Name. A "Load Students" button triggers the table refresh.
3. User selects subject, section, season, types an assessment name (e.g., "Quiz 1"), and clicks "Load Students."
4. `BatchScoreEntryForm` loads all enrolled students for that subject+section via `EnrollmentDao.getStudentsBySubjectAndSection()`. If scores already exist for this assessment (same student + subject + season + name), they are pre-populated via `AssessmentDao.getBySeason()`.
5. The table shows Student ID, Name, and an editable Score column. The instructor types scores for each student.
6. "Save All" iterates every row. Empty scores are skipped. Each non-empty score is saved via `AssessmentDao.saveOrUpdate()` — which checks if the assessment already exists (by student + subject + season + name) and updates it, otherwise inserts a new record.
7. A summary dialog shows how many were saved and how many were skipped.

**Why this exists:** Entering 40 individual assessments through GradeForm takes 20-30 minutes. Batch entry reduces it to 2-3 minutes — pick the assessment once, fill in scores, save.

**Why enrollment exists:** Without it, attendance and grade forms would show every student in the database for every subject. Enrollment scopes the data — only enrolled students appear in `AttendanceForm` and `GradeForm`.

---

## What happens when attendance is marked?

1. User navigates to `AttendanceForm` from the dashboard.
2. `AttendanceFilterPanel` shows four controls: Subject dropdown, Section dropdown (populated dynamically by `EnrollmentDao.getSectionsBySubject()`), "From" date dropdown (defaults to today), and "To" date dropdown (empty by default). Both date controls are editable JComboBoxes — the dropdown lists dates that have existing attendance records (via `AttendanceDao.getDatesBySubjectAndSection()`), but the instructor can also type a custom date for marking new attendance. Date lists refresh when the section changes.
3. **Single-date mode (To is empty or same as From):** User selects a subject, section, and date. `AttendanceForm` loads enrolled students via `EnrollmentDao.getStudentsBySubjectAndSection()`. Each student row has a status dropdown: Present, Absent, Late, Excused. If attendance was already marked for this date, existing statuses are pre-loaded via `AttendanceDao.getBySubjectAndDate()`. User sets each student's status and clicks "Save." For each row, `AttendanceDao.saveOrUpdate()` checks if a record exists for that student+subject+date — if exists, updates; if not, inserts.
4. **Date range mode (To is different from From):** The table switches to a read-only 4-column view (Student ID, Name, Date, Status) showing all attendance records in the range via `AttendanceDao.getBySubjectSectionAndDateRange()`. Save and Mark All Present are blocked with clear error messages. This mode is for reviewing attendance history — e.g., "who was absent last week?"

**Bulk entry:** "Mark All Present" sets every dropdown to "Present" — useful for days with full attendance. User still clicks "Save" to persist. Only available in single-date mode.

**Only enrolled students appear.** The `EnrollmentDao.getStudentsBySubjectAndSection()` JOIN query ensures only students enrolled in the selected subject AND belonging to the selected section are shown.

---

## How does the dashboard show enrollment and attendance?

The dashboard stats panel (`DashboardStatsPanel`) shows 6 statistics in a 3x2 grid:

1. **Total Students** — `DashboardDao.countStudents()`
2. **Total Subjects** — `DashboardDao.countSubjects()`
3. **Enrolled** — `DashboardDao.countEnrolled()` (total enrollment records)
4. **Passed** — `DashboardDao.countPassed()` (student-subject pairs with AVG score >= 75)
5. **Failed** — `DashboardDao.countFailed()` (student-subject pairs with AVG score < 75)
6. **Today's Attendance** — displayed as "Today: 3/5 sections (15/20 present)". The first part (`countTodaySectionsMarked()` / `countTotalEnrolledSections()`) shows how many subject-section combos have attendance marked today vs. how many exist. The second part (`countTodayPresent()` / `countTodayTotal()`) shows overall present count. Label turns green when all sections are done, blue when partially done.

All counts refresh when the dashboard opens via `statsPanel.refresh()`.
