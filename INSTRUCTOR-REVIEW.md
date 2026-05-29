# Instructor Review — Feature Recommendations

> Reviewed: 2026-05-29
> Perspective: "If I were the instructor using this system daily, what's missing?"
> These are NOT milestones. They are recommendations to revisit when deciding what to build next.

---

## Priority 1 — Would make an instructor actually use this daily

### R1. Semester / School Year Selector
**Problem:** After one semester, all data from last semester is still there. No way to start fresh for a new school year without manually deleting everything. Data accumulates forever.
**What it needs:** A `school_year` and `semester` concept (e.g., "2025-2026 / 1st Semester"). All data is scoped to the active semester. Starting a new semester = clean slate without deleting old data. A semester switcher on the dashboard or login screen.
**Why it matters:** This is the single most important missing feature for real use. Without it, the system is a one-semester tool.

### R2. Filter GradeForm Student Dropdown by Subject
**Problem:** When entering grades for CS101, the student dropdown shows ALL students in the database. With 200+ students, scrolling to find the right one wastes time every single entry.
**What it needs:** When a subject is selected in GradeForm's input panel, the student dropdown should only show students enrolled in that subject. Same for AssessmentInputPanel.
**Why it matters:** Saves minutes of frustration per grading session. Every instructor would notice this immediately.

### R3. Attendance Export / Print
**Problem:** Grades can be exported to CSV and printed, but attendance cannot. ACLC requires attendance reports for submission too.
**What it needs:** Add Print and Export CSV buttons to AttendanceForm (same pattern as GradeForm). Export should include student name, dates, and status. Possibly a summary view (total present/absent/late/excused per student).
**Why it matters:** Instructors submit attendance reports alongside grade reports. Without export, they'd still need a separate system for attendance records.

---

## Priority 2 — Would make the daily workflow smoother

### R4. "Missing Scores" Indicator in Batch Score Entry
**Problem:** After entering Quiz 1 scores via batch entry, there's no way to quickly see which students were missed. Have to eyeball the table for empty rows.
**What it needs:** Highlight rows with no score (yellow background or bold text). Or add a filter toggle: "Show only students without a score for this assessment." A count label like "12/40 entered" would also help.
**Why it matters:** With 40+ students, it's easy to accidentally skip someone. The instructor won't know until grades are submitted.

### R5. Attendance Summary in Student Grade Summary
**Problem:** The Student Grade Summary (report card view) shows grades but not attendance. When talking to a parent or reviewing a student, the instructor has to switch to AttendanceForm to check attendance separately.
**What it needs:** Add attendance columns to the Student Grade Summary table — e.g., "Present", "Absent", "Late", "Attendance %" per subject. Data comes from AttendanceDao counts.
**Why it matters:** Attendance and grades go hand-in-hand. A student failing with 50% attendance tells a different story than one failing with 95% attendance.

### R6. Per-Subject Dashboard Statistics
**Problem:** Dashboard shows "15 passed, 3 failed" globally across ALL subjects. Not useful — an instructor wants to know pass/fail per subject they teach.
**What it needs:** A small summary table on the dashboard showing each subject with its own passed/failed/enrolled counts. Something like:

| Subject | Enrolled | Passed | Failed |
|---------|----------|--------|--------|
| CS101   | 40       | 35     | 5      |
| MATH201 | 38       | 30     | 8      |

**Why it matters:** Lets the instructor immediately see which subjects need attention without navigating to GradeForm and filtering.

---

## Priority 3 — Nice polish for a complete feel

### R7. Class List Print from Enrollment
**Problem:** Sometimes the instructor just needs a simple printable roster — student names and IDs for a specific subject + section. Currently EnrollmentForm has no print or export.
**What it needs:** Add Print and Export CSV buttons to EnrollmentForm. Exports the current table (students enrolled in the selected subject + section).
**Why it matters:** Class lists are handed out on the first day, used for seating charts, and attached to grade submissions.

### R8. Password Change
**Problem:** Users are stuck with the password set at account creation. No way to change it from within the app.
**What it needs:** A "Change Password" option accessible from the dashboard (or a small settings/profile area). Asks for current password, new password, confirm new password.
**Why it matters:** Basic account management. Low priority since this is single-user for now, but expected in any login-based system.

---

## Summary Table

| ID | Recommendation | Priority | Complexity |
|----|----------------|----------|------------|
| R1 | Semester / School Year Selector | P1 | High — touches schema, all DAOs, all forms |
| R2 | Filter student dropdown by subject | P1 | Low — filter logic in GradeForm + AssessmentInputPanel |
| R3 | Attendance export / print | P1 | Low — reuse existing CSV/print pattern |
| R4 | Missing scores indicator | P2 | Low — UI highlight + count label |
| R5 | Attendance in grade summary | P2 | Medium — new DAO queries + summary table columns |
| R6 | Per-subject dashboard stats | P2 | Medium — new DAO query + table component |
| R7 | Class list print from enrollment | P3 | Low — reuse existing print/export pattern |
| R8 | Password change | P3 | Low — new form + UserDao.updatePassword |
