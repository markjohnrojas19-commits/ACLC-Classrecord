# ACLC Class Record System — Demo Presentation Flow

> **Audience:** Instructor + Panelists
> **Estimated Time:** 15-20 minutes
> **Tip:** Before the demo, make sure XAMPP MySQL is running and the database has sample data (students, subjects, enrollments, some scores, some attendance).

---

## Pre-Demo Checklist

Before you start, verify these are ready:

- [ ] XAMPP MySQL is running
- [ ] Database has at least 5-10 students across 2+ sections (e.g., BSIT 1-A, BSCS 2-B)
- [ ] At least 2-3 subjects exist (e.g., CS101 Programming, MATH201 Calculus)
- [ ] Some students are enrolled in subjects
- [ ] Some attendance records exist for today
- [ ] Some assessment scores exist (at least in Prelim and Midterm seasons)
- [ ] The active semester is "2025-2026 / 1st Semester"

---

## PHASE 1 — System Entry (Login)

### What to show
1. Launch the application — the **Login Form** appears
2. Type username: `admin`, password: `admin123`
3. Click **Login**

### What to explain
- "The system starts with a login screen. It supports two roles — Admin and Instructor — for future role-based access control."
- "Credentials are verified against the MySQL database using prepared statements to prevent SQL injection."
- "On successful login, the user is taken to the Dashboard."

### If panelist asks
- **"What happens with wrong credentials?"** — Show it: type a wrong password, click Login. An error dialog appears.
- **"How is the password stored?"** — Currently plain text for the school project. Production would use hashing (SHA-256 or bcrypt). This is documented as a future milestone (Milestone 17).

---

## PHASE 2 — Dashboard Overview

### What to show
1. Point out the **header** — shows "ACLC Class Record — admin"
2. Point out the **Semester selector** — currently "2025-2026 / 1st Semester"
3. Point out the **Navigation buttons** — Students, Subjects, Enrollment, Grades, Attendance
4. Point out the **Dashboard Overview stats** — Total Students, Total Subjects, Today's Attendance

### What to explain
- "The Dashboard is the central hub. Every form is accessible from here."
- "The stats panel shows live counts from the database. Students and Subjects are global counts. Today's Attendance shows how many subject-section combinations have been marked today versus total, and the overall present count."
- "The attendance indicator turns green when all sections are marked for today."

### Key talking point
- "The Semester selector at the top is a key feature. All enrollment, grades, and attendance data is scoped to the selected semester. When a new semester starts, the instructor creates a new semester here and gets a clean slate — without losing any old data. Students and subjects remain the same, but enrollments, scores, and attendance start fresh."

---

## PHASE 3 — Student Management

### What to show
1. Click **Students** from the Dashboard
2. Show the **section tabs** — "All" tab plus one tab per course/section (e.g., "BSIT 1-A", "BSCS 2-B")
3. **Add a student live:**
   - Fill in Student ID, First Name, Last Name, Course, Year Level, Section (dropdown A-E), Gender
   - Click **Add** — student appears in the table and in the correct section tab
4. **Click on a student row** — fields auto-populate for editing
5. Show the **Subject filter** dropdown — select a subject to see only enrolled students
6. Show the **Search field** — type a name and press Enter

### What to explain
- "Students are organized into tabs by their course and section. These tabs are dynamic — if I add a student to a new section, a new tab appears automatically."
- "The subject filter lets you see only students enrolled in a specific subject. This is useful when you need to check who's in your class."
- "The system also supports bulk operations..."

### Optional: Show advanced features (if time allows)
7. Click **Add Multiple** — show the BatchStudentEntryForm dialog
   - "You can set defaults (Course, Year, Section) and apply them to all rows, then just type IDs and names."
8. Click **Import CSV** — show the file chooser (don't need to actually import)
   - "For large classes, you can import from a CSV file. The system validates every row and shows a preview with errors highlighted in red before importing."

### If panelist asks
- **"What happens when you delete a student?"** — "The system cascades the delete — it removes all their enrollment records, assessment scores, and attendance records first, then deletes the student. A confirmation dialog prevents accidental deletion."
- **"Can you delete an entire section?"** — "Yes. When you're on a section tab and click Delete with no row selected, it asks if you want to delete all students in that section."

---

## PHASE 4 — Subject Management

### What to show
1. Click **Back to Dashboard**, then click **Subjects**
2. Show the subject table — Subject ID, Subject Code, Subject Name
3. **Add a subject live:** e.g., Subject Code: "ENG101", Subject Name: "English Communication"
4. Click **Add** — new subject appears in the table
5. Show the **Search** feature — type a keyword to filter

### What to explain
- "Subjects represent the courses the instructor teaches. Each subject has a unique code."
- "This is straightforward CRUD — Add, Edit, Delete, Search."

### Keep it brief
- This is the simplest form. Spend 1-2 minutes max here.

---

## PHASE 5 — Enrollment (Connecting Students to Subjects)

### What to show
1. Click **Back to Dashboard**, then click **Enrollment**
2. Select a **Subject** from the dropdown (e.g., CS101)
3. Select a **Course/Section** (e.g., BSIT 1-A)
4. Show the **checkbox table** — each student has a checkbox
5. **Check the "Select All" checkbox** in the header — all students get checked
6. Click **Save** — shows "Enrolled 10, Unenrolled 0" summary

### What to explain
- "Enrollment is the bridge between students and subjects. Before a student can have grades entered or attendance marked, they must be enrolled."
- "The Select All checkbox in the header makes it easy to enroll an entire section at once."
- "The save logic is smart — it compares the current checkbox state to the database. Only changes are applied. If a student was already enrolled and the box is still checked, nothing happens."

### Key talking point
- "Enrollment is semester-scoped. When you start a new semester, you need to re-enroll students. This is realistic — class lists change every semester."

---

## PHASE 6 — Attendance Marking

### What to show
1. Click **Back to Dashboard**, then click **Attendance**
2. Select a **Subject** (e.g., CS101)
3. Select a **Course/Section** (e.g., BSIT 1-A)
4. The **From date** defaults to today — show that only enrolled students appear
5. Each student has a **Status dropdown**: Present, Absent, Late, Excused
6. Click **Mark All Present** — all dropdowns switch to "Present"
7. Change one student to "Absent" manually
8. Click **Save** — shows "Attendance saved for 10 of 10 students"

### What to explain
- "Only enrolled students appear in the attendance table. If a student isn't enrolled in this subject, they won't show up."
- "The Mark All Present button is a timesaver for days when everyone is present — just click it and change the exceptions."
- "The date dropdown shows dates that already have attendance records. You can also type a new date manually."

### Show date range mode
9. Type a different date in the **To** field (e.g., a date from last week)
10. The table switches to **read-only mode** showing all attendance records in the range

### What to explain
- "When you enter a date range, the view switches to read-only mode showing the attendance history. This is for reviewing past records, not editing."

### Show Export (R3 feature)
11. Click **Print** — show the print dialog
12. Click **Export CSV** — show the file chooser, save a CSV file

### What to explain
- "Attendance can be printed or exported to CSV for submission. ACLC requires attendance reports alongside grade reports — this feature handles that."

---

## PHASE 7 — Grade Entry (Batch Score Entry)

### What to show
1. Click **Back to Dashboard**, then click **Grades**
2. Show the **GradeForm** — 5 tabs: Prelim, Midterm, Pre-Final, Final, Final Grade
3. Click **Enter Scores** — opens BatchScoreEntryForm
4. Select **Subject** (CS101), **Section** (BSIT 1-A), **Season** (Prelim)
5. Type **Assessment Name**: "Quiz 1"
6. Set **Total Items**: 10 (to show that scores can be out of any value, not just 100)
7. Click **Load Students** — enrolled students appear
8. **Enter scores** for each student (e.g., 8, 7, 9, 6, 10...)
9. Click **Save All** — shows "Saved 10, Skipped 0"

### What to explain
- "Batch Score Entry is the fastest way to enter grades for a whole class. Instead of adding one score at a time, the instructor picks one assessment and fills in scores for everyone."
- "The Total Items field means assessments can be out of any value — a quiz out of 10, an exam out of 50, a project out of 100. The system normalizes everything to percentages automatically."
- "If you come back later and load the same assessment, existing scores are pre-populated. You can edit them."

### Key talking point
- "This feature alone saves 20-30 minutes per grading session compared to individual entry."

---

## PHASE 8 — Grade Viewing & Analysis

### What to show
1. Click **Back to Grades** — you're back on GradeForm
2. Show the **Prelim tab** — the Quiz 1 scores you just entered appear
   - Point out the score format: "8/10 (80.0%)" — shows raw score, total items, and percentage
   - Point out the **Season Average** at the bottom of the tab
3. Click the **Final Grade tab** — shows weighted final grade per student
   - Columns: Student, Subject, Prelim, Midterm, Pre-Final, Final, Final Grade, Remarks
   - Green for PASSED, Red for FAILED
4. Show the **Section filter** — select a specific section
5. Show the **Status filter** — select "Failed Only" to see at-risk students

### What to explain
- "Each season tab shows all assessments for that grading period with a computed average."
- "The Final Grade tab combines all four seasons using a weighted formula: Prelim 20%, Midterm 20%, Pre-Final 20%, Final 40%. This matches ACLC's standard grading system."
- "The section and status filters let you quickly find at-risk students — for example, 'show me all failing students in BSIT 1-A.'"

### Show Edit Score
6. Go back to a **season tab**, click on a row, then click **Edit Score**
7. The **EditAssessmentDialog** opens — change the score, click Save

### What to explain
- "If a score needs correction, the instructor can edit individual assessments without going back to batch entry."

### Show Export
8. Click **Print** — show the print dialog
9. Click **Export CSV** — save a CSV file

### What to explain
- "Grades can be printed or exported to CSV for official submission."

---

## PHASE 9 — Student Grade Summary (Report Card View)

### What to show
1. Go back to **Students** from Dashboard
2. **Click on a student** in the table
3. Click **View Grades** — opens StudentGradeSummaryForm with that student pre-selected
4. Show the **left sidebar** — all students listed, filterable by section
5. Show the **summary table** — one row per subject showing: Subject Code, Subject Name, Prelim avg, Midterm avg, Pre-Final avg, Final avg, Final Grade, Remarks
6. Click on **different students** in the sidebar to see their grades update

### What to explain
- "This is the report card view. An instructor can pull up any student and see their complete grade picture across all subjects."
- "The section filter in the sidebar lets you browse students by class."
- "This is useful during parent-teacher meetings or when students ask about their standing."

### Show Print
7. Click **Print** — show the print dialog

### What to explain
- "The report card can be printed directly for the student or their parents."

---

## PHASE 10 — Semester Management (The Big Feature)

### What to show
1. Go back to the **Dashboard**
2. Click **New Semester** — enter "2025-2026", select "2nd Semester"
3. The new semester appears in the dropdown
4. **Switch to "2025-2026 / 2nd Semester"** — notice:
   - Total Students: same (students are global)
   - Total Subjects: same (subjects are global)
   - Today's Attendance: 0/0 sections (0/0 present) — clean slate
5. Go to **Enrollment** — no one is enrolled yet in the new semester
6. Go to **Grades** — all tabs are empty
7. **Switch back to "2025-2026 / 1st Semester"** — all the old data reappears
8. **Delete the 2nd semester** — click Delete Semester, confirm the warning
   - "This will permanently delete ALL enrollments, grades, and attendance records for this semester."

### What to explain
- "This is the most important feature for real-world use. After one semester, the instructor creates a new semester and gets a clean slate — without losing any historical data."
- "Students and subjects carry over. Only enrollments, scores, and attendance reset."
- "The instructor can switch between semesters at any time to review old records."
- "Deleting a semester removes all its data permanently — enrollment records, all assessment scores, all attendance records."

### Key talking point
- "Without this feature, the system is a one-semester tool. With it, it can be used year after year."

---

## PHASE 11 — Architecture & Code Quality (If Asked)

### Be ready to explain these if the panelist asks about the code:

**Layered Architecture (4 layers):**
```
UI Layer (Swing Forms)
   |
Service Layer (GradeComputer)
   |
DAO Layer (StudentDao, SubjectDao, etc.)
   |
Model Layer (Student, Subject, Assessment, etc.)
```
- "Lower layers never reference upper layers. The UI depends on DAOs, DAOs depend on Models, but Models know nothing about DAOs or UI."

**Key Design Decisions:**
- "We use the DAO pattern to separate database logic from UI logic. SQL lives only in DAO classes."
- "All database operations use PreparedStatement with parameterized queries to prevent SQL injection."
- "Database connections are managed with try-with-resources — connections are always properly closed."
- "Grade computation is in a separate GradeComputer service class — if the formula changes, only one class changes."
- "UI styling is centralized in StyleConstants — changing the app's look means editing one file."

**File Count:**
- 11 Model classes (data objects)
- 9 DAO classes (database access)
- 1 Service class (grade computation)
- 4 Utility classes (constants, CSV parser, semester tracker)
- 21 UI classes (forms, panels, dialogs)
- **Total: 46 Java files**

**Database:**
- 7 tables: users, students, subjects, semesters, enrollments, assessments, attendance
- Foreign key relationships enforce data integrity
- Semester scoping via semester_id on enrollments, assessments, attendance

---

## Demo Flow Summary (Quick Reference)

```
LOGIN
  |
  v
DASHBOARD -----> Show stats, semester selector
  |
  |---> STUDENTS -----> Add/Edit/Delete, section tabs, search, bulk add, CSV import
  |
  |---> SUBJECTS -----> Add/Edit/Delete, search
  |
  |---> ENROLLMENT ---> Check students per subject+section, select all, save
  |
  |---> ATTENDANCE ---> Mark status per student, mark all present, save
  |         |              date range view, print, export CSV
  |         v
  |---> GRADES -------> Season tabs (Prelim/Midterm/Pre-Final/Final)
  |         |              Final Grade tab (weighted average)
  |         |              Section + Status filters, search
  |         |              Enter Scores --> Batch Score Entry
  |         |              Edit Score --> Edit Assessment Dialog
  |         |              Print, Export CSV
  |         v
  |---> STUDENT SUMMARY -> Report card per student
  |                           Section filter + student list
  |                           Print
  |
  v
SEMESTER MANAGEMENT
  |---> Create new semester (clean slate)
  |---> Switch between semesters
  |---> Delete semester (cascade delete)
  |
  v
LOGOUT
```

---

## Anticipated Panelist Questions

| Question | Answer |
|----------|--------|
| "Why Java Swing?" | Instructor-mandated tech stack for this course. Swing is bundled with Java — no extra dependencies needed. |
| "Why not a web app?" | Scope is bounded by the semester. Desktop app meets all requirements. Web migration is documented as a future milestone. |
| "How do you handle concurrent users?" | This is a single-user desktop app. Multi-user support would require Milestone 18 (Instructor-Section-Subject Linking). |
| "What about security?" | Documented as Milestone 17. Would add password hashing, externalized credentials, session timeout, and audit logging. |
| "What design patterns did you use?" | DAO pattern (database access), MVC-adjacent (Model-DAO-Service-UI layers), Strategy pattern potential (grade computation), Observer (UI refresh on data change). |
| "How is the grade formula configurable?" | Season weights are constants in GradeConstants.java: Prelim 20%, Midterm 20%, Pre-Final 20%, Final 40%. Changing the weights requires editing one file. |
| "What if MySQL is down?" | DAOs catch SQLExceptions and show user-friendly error messages. Dashboard stats show "Error" instead of crashing. |
| "Can scores be out of different totals?" | Yes. Each assessment has a totalItems field. A quiz can be /10, an exam /100. The system normalizes to percentages for averaging. |
| "What happens to old data when switching semesters?" | Nothing — it stays. Semester scoping is a filter, not a delete. Switch back to see it again. |
| "How do you prevent duplicate students?" | The student_id is the primary key. The system checks existsById() before inserting and shows an error if the ID already exists. |

---

## Closing Statement

> "The ACLC Class Record System replaces manual pen-and-paper record keeping with a complete digital solution. An instructor can manage students, subjects, enrollment, attendance, and grades — all in one application. The semester system ensures it can be used year after year. Grades auto-compute with a configurable weighted formula, and everything can be printed or exported for official submission."

---

**Good luck with the presentation!**
