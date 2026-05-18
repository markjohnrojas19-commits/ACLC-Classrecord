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
- **Plan:** User needs to run the app in NetBeans to verify "Connected to database successfully!" prints to console. If it does, Milestone 1 is complete.
