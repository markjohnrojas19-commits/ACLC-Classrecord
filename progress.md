# Progress Log

> Append-only log. Every meaningful decision, action, and verification goes here as a bullet point.
> Keep entries short and chronological. The format is intentionally flat — no sections, no headings, no nesting.
>
> **Conventions:**
> - **Decision:** ... — a choice was made (record what and why)
> - **Action:** ... — something was changed in the codebase or environment
> - **Verified:** ... — the user confirmed the change works on their machine
> - **Rule:** ... — a new project-wide rule was added (also reflect in `CLAUDE.md`)
> - **Refactor:** ... — code was reshaped without changing behavior
> - **Bugfix:** ... — an incorrect behavior was corrected
> - **Issue:** ... — an unresolved problem to come back to
> - **Plan:** ... — the next thing to try

---

- Project initialized from `project-boilerplate`
- Created initial `CLAUDE.md`, `milestones.md`, `progress.md`, `ARCHITECTURE.md`
- **Decision:** Project is ACLC Class Record System — a Java Swing desktop app for managing students, subjects, grades, and attendance. Built in NetBeans IDE with MySQL (XAMPP) + JDBC.
- **Decision:** Tech stack confirmed — Java + Swing (GUI), MySQL + XAMPP (database), JDBC (connection), NetBeans IDE. No external frameworks.
- **Decision:** Primary quality attribute is **readability** — instructor will read the code.
- **Decision:** Architecture is 4-layer: Model -> DAO -> Service -> UI. Lower layers never reference upper layers.
- **Decision:** Grade computation weights stored as constants in `GradeConstants.java` so they're easy to change when instructor confirms the formula. Default: 30% Quiz, 30% Assignment, 40% Exam. Passing grade: 75.
- **Decision:** Feature tiers established — Must-have: Login, Student CRUD, Subject CRUD, Grade Management, Dashboard. Nice-to-have: Attendance, Search/Filter, Print. Deferred: PDF/Excel export, Calendar attendance.
- **Decision:** Database name is `aclc_classrecord_db` with tables: users, students, subjects, grades.
- **Decision:** 8 core milestones defined, from project setup through polish. 2 additional nice-to-have milestones (attendance, print).
- **Action:** Updated all md files (`CLAUDE.md`, `milestones.md`, `progress.md`, `ARCHITECTURE.md`) to reflect project-specific content.
- **Plan:** Next step is Milestone 1 — create NetBeans project and set up the MySQL database.
- **Action:** Step 1.1 — NetBeans Java Application project created at `ACLC-Classrecord/` subfolder (contains `build.xml`, `nbproject/`, empty `src/`).
- **Action:** Step 1.2 — Wrote `sql/schema.sql` with CREATE DATABASE, all four tables (users, students, subjects, grades), foreign keys, and a default admin user (admin / admin123).
- **Plan:** Next is Step 1.3 — run `schema.sql` in phpMyAdmin to create the database.
- **Verified:** Step 1.3 — user confirmed database and tables created in phpMyAdmin.
- **Verified:** Step 1.4 — user added MySQL Connector/J JAR to NetBeans project libraries.
- **Action:** Step 1.5 — Created `dao/DatabaseConnection.java` (connection utility with URL, user, password for localhost MySQL). Created `Main.java` entry point that calls `DatabaseConnection.getConnection()` and prints success/failure to console.
- **Verified:** Step 1.5 — user confirmed "Connected to database successfully!" prints to console. Database connection works.
- **Milestone 1 complete.** Project setup and database connection verified.
- **Action:** Step 2.1 — Created `model/Role.java` (enum: ADMIN, INSTRUCTOR) and `model/User.java` (4 fields: userId, username, password, role). User is immutable (getters only, no setters) since login returns a read-only snapshot.
- **Decision:** Role is a separate enum rather than a String field — gives compile-time safety and readable role checks.
- **Action:** Step 2.2 — Created `dao/UserDao.java` with `authenticate(username, password)` method. Uses PreparedStatement to query `users` table. Returns `User` object on match, `null` on failure. Extracts role string from DB and converts to `Role` enum via `Role.valueOf()`.
- **Action:** Step 2.3 — Already done. The `schema.sql` from Step 1.2 already inserts a default admin user (admin / admin123).
- **Action:** Updated `Main.java` to test authentication — runs both a valid and invalid login attempt and prints results to console.
- **Verified:** Steps 2.2 & 2.3 — authentication test printed expected output.
- **Action:** Steps 2.4 & 2.5 — Created `ui/LoginForm.java`. Uses nested BorderLayout + GridLayout for readable layout. Fields panel (GridLayout 2x2) holds username/password labels and fields. Button panel (FlowLayout) holds Login button. Login button calls `UserDao.authenticate()`, shows error dialog on failure, welcome dialog on success.
- **Decision:** Layout approach is nested simple layouts (BorderLayout + GridLayout + FlowLayout) — readable for instructor, handles resizing, no overengineering.
- **Action:** Updated `Main.java` to launch `LoginForm` via `SwingUtilities.invokeLater()` instead of the console test.
- **Verified:** Steps 2.4 & 2.5 — login form works. Valid credentials show welcome, invalid credentials show error, empty fields show warning.
- **Action:** Step 2.6 — Created `ui/DashboardForm.java` as a minimal shell (800x600 JFrame with welcome label). Updated `LoginForm.onLoginSuccess()` to open DashboardForm and dispose LoginForm. Removed the temporary welcome dialog.
- **Verified:** Step 2.6 — login opens dashboard, login window closes. Milestone 2 complete.
- **Action:** Steps 3.1, 3.2, 3.3 — Rewrote `ui/DashboardForm.java` with full layout. Header panel: welcome label (bold, 20pt) + logout button. Center: 2x2 grid of placeholder stat labels (Total Students, Total Subjects, Passed, Failed — all showing "0" for now). Bottom: navigation buttons (Students, Subjects, Grades) — not yet wired to forms. Logout creates a new LoginForm and disposes the dashboard.
- **Decision:** All three steps done together since they're all layout changes to the same file and individually trivial.
- **Plan:** User verifies dashboard layout + logout flow, then Milestone 3 is complete. Next: Milestone 4 — Student Management.
- **Verified:** Milestone 3 complete — dashboard layout and logout flow confirmed working.
- **Action:** Step 4.1 — Created `model/Student.java` with 7 fields (studentId, firstname, lastname, course, yearLevel, section, gender), constructor, getters, and setters. Mutable design chosen because students are actively edited via the UI form.
- **Decision:** Student is mutable (unlike User which is immutable) because the edit workflow requires updating fields on existing objects.
- **Action:** Step 4.2 — Created `dao/StudentDao.java` with 5 CRUD methods (add, getAll, update, delete, search) plus 2 private helpers (setStudentParameters, extractStudent). All use try-with-resources and PreparedStatement. Search uses LIKE on student_id, firstname, lastname.
- **Decision:** Extracted `StudentInputPanel` as a separate atom from `StudentForm` to respect class field limits. StudentInputPanel holds the 7 input fields (intrinsic to its role as an input panel). StudentForm holds only 4 fields (inputPanel, studentTable, studentDao, searchField).
- **Action:** Step 4.3 — Created `ui/StudentInputPanel.java` (7 input fields, methods: toStudent, fromStudent, clear, lockStudentId, hasEmptyFields). Created `ui/StudentForm.java` (header with back button, input panel, JTable with row selection, action buttons: Add/Edit/Delete/Clear/Search). Wired DashboardForm "Students" button to open StudentForm.
- **Verified:** Step 4.3 — StudentForm opens from dashboard, layout displays correctly, back button works.
- **Verified:** Steps 4.4-4.7 — All CRUD operations verified: Add inserts and refreshes table, Edit updates selected row, Delete confirms and removes, Search filters by name/ID. Edge cases (empty fields, no selection) show proper error messages.
- **Milestone 4 complete.** Full student CRUD works end-to-end with database persistence.
- **Action:** Step 4.2 — Created `dao/StudentDao.java` with 5 methods: `add()`, `getAll()`, `update()`, `delete()`, `search()`. All use try-with-resources and PreparedStatement. `search()` matches keyword against student_id, firstname, or lastname using LIKE. Two private helpers: `setStudentParameters()` (binds Student fields to INSERT statement) and `extractStudent()` (maps ResultSet row to Student object). Follows same patterns as `UserDao.java`.
- **Action:** Step 5.1 — Created `model/Subject.java` with 3 fields (subjectId, subjectCode, subjectName), constructor, getters, and setters. Mutable design (like Student) since subjects will be edited via the UI form.
- **Decision:** Subject is mutable (getters + setters) — same pattern as Student. The edit workflow requires updating fields on existing objects.
- **Action:** Step 5.2 — Created `dao/SubjectDao.java` with 5 methods: `add()`, `getAll()`, `update()`, `delete()`, `search()`. All use try-with-resources and PreparedStatement. `add()` omits `subject_id` since it's auto-increment. `search()` matches keyword against subject_code or subject_name using LIKE. One private helper: `extractSubject()` (maps ResultSet row to Subject object). Follows same patterns as `StudentDao.java`.
- **Decision:** No `setSubjectParameters()` helper needed (unlike StudentDao) — Subject only has 2 insertable fields, so inline binding is simpler and more readable.
- **Verified:** Step 5.2 — compiles successfully.
- **Action:** Step 5.3 — Created `ui/SubjectForm.java` with layout: header (title + back button), input panel (subject code + subject name in 1x4 GridLayout), JTable (3 columns: ID, Subject Code, Subject Name), and button panel (Add, Edit, Delete, Clear, Search — Clear wired, others pending Step 5.4). Row selection populates input fields. Table loads from database on open.
- **Decision:** No separate SubjectInputPanel needed — only 2 input fields, so SubjectForm stays at 5 total fields (within the 1-5 atom limit). Extracting a panel for 2 fields would be overengineering.
- **Action:** Wired DashboardForm "Subjects" button to open SubjectForm (same pattern as "Students" button).
- **Verified:** Step 5.3 — compiles and layout works. SubjectForm opens from dashboard, back button works.
- **Action:** Step 5.4 — Wired all CRUD buttons in `SubjectForm.java`: `handleAdd()` creates Subject with id=0 (auto-generated by DB), `handleEdit()` reads subjectId from table row to preserve identity, `handleDelete()` confirms via dialog then deletes by subjectId, `handleSearch()` filters by keyword or refreshes on empty. Added `hasEmptyFields()` and `showError()` helpers.
- **Verified:** Step 5.4 — All CRUD operations verified: Add inserts and refreshes table, Edit updates selected row, Delete confirms and removes, Search filters by code/name. Edge cases (empty fields, no selection) show proper error messages.
- **Milestone 5 complete.** Full subject CRUD works end-to-end with database persistence.
