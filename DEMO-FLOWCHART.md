# ACLC Class Record System — Demo Presentation Guide

> **Audience:** Instructor + Panelists
> **Time:** 15-20 minutes (aim to finish in 15 — actual runs always take longer than planned)
> **Golden rule:** Tell a story, not a feature list. Walk the panel through a real instructor's day.

---

## Before You Start

### Pre-Demo Checklist

- [ ] XAMPP MySQL is running
- [ ] App is already open on the Login screen (don't fumble with startup during the demo)
- [ ] Database has realistic sample data:
  - 8-10 students across 2+ sections (e.g., BSIT 1-A, BSCS 2-B)
  - 2-3 subjects (e.g., CS101 Programming, MATH201 Calculus)
  - Students enrolled in subjects
  - Assessment scores in at least Prelim and Midterm seasons
  - Some attendance records for today
  - Active semester: "2025-2026 / 1st Semester"
- [ ] Mouse cursor is large and visible (for projector)
- [ ] All unrelated windows are closed
- [ ] Have a backup plan if the live demo fails (screenshots or screen recording)

### Mindset

- **You are not showing every feature.** You are showing how an instructor uses this system in a realistic workflow.
- **Move the mouse slowly.** Projectors make fast mouse movements invisible.
- **Explain why you made decisions**, not just what the app does. The panel tests your understanding, not just your output.
- **If something breaks**, stay calm. Say "Let me try that again" or move on. Don't panic.

---

## The Demo

### Opening — Set the Scene (1 minute)

**Don't touch the app yet.** Face the panel and say:

> "Right now, ACLC instructors track grades and attendance using paper record books or Excel spreadsheets. These are error-prone — manual grade computation is slow, and there's no easy way to filter by section, look up one student's complete record, or carry data across semesters."
>
> "Our system replaces that. An instructor logs in and can manage students, enrollment, attendance, and grades — all in one application. Grades auto-compute. Attendance is tracked per subject. And when a new semester starts, the instructor gets a clean slate without losing old records."
>
> "Let me walk you through what a typical day looks like."

**Why this works:** The panel now knows the problem, the audience, and the solution before seeing a single screen. Everything that follows has context.

---

### Act 1 — Start the Day (2-3 minutes)

**Story:** *"The instructor arrives Monday morning and opens the system."*

**Login**
1. App is already on the Login screen — point out the branded header matching the rest of the app
2. Log in with `admin` / `admin123`

> "Credentials are verified against MySQL using prepared statements — no SQL injection risk."

**Dashboard**
3. Land on the Dashboard — pause and let the panel see it
4. Point out three things only:
   - **Stats cards** — live counts from the database (students, subjects, today's attendance)
   - **Semester selector** — currently "2025-2026 / 1st Semester"
   - **Navigation** — five buttons, one for each major function

> "This is the hub. The instructor sees their key numbers at a glance and navigates to any feature from here."

**Per-Subject Stats Table**
5. Scroll down to the per-subject statistics table

> "The instructor can immediately see which subjects have the most failing students and need attention."

---

### Act 2 — Enter Today's Scores (3-4 minutes)

**Story:** *"The instructor just finished a Prelim quiz and needs to record scores for the whole class."*

This is your most impressive workflow — show it early.

**Batch Score Entry**
1. Click **Grades** from Dashboard
2. Click **Enter Scores** — BatchScoreEntryForm opens
3. Select Subject: CS101, Section: BSIT 1-A, Season: Prelim
4. Type Assessment Name: "Quiz 3", set Total Items: **10**
5. Click **Load Students** — all enrolled students appear

> "Notice the total items is 10, not 100. A quiz can be out of 10, an exam out of 50, a project out of 100. The system normalizes everything to percentages automatically."

6. Enter scores quickly (8, 7, 9, 6, 10...) — point out the yellow highlighting for missing scores and the count label at the bottom
7. Click **Save All**

> "One screen, one assessment, the whole class done. This saves 20-30 minutes compared to entering scores one at a time."

**Grade View**
8. Click back to **Grades** — show the Prelim tab with the quiz you just entered
   - Point out the score format: "8/10 (80.0%)"
9. Click the **Final Grade** tab — show the weighted final grade

> "The system computes final grades using ACLC's standard formula: Prelim 20%, Midterm 20%, Pre-Final 20%, Final 40%. Green means passed, red means failed. The formula is configurable — just one file to change."

10. Show the **Status filter** — select "Failed Only"

> "The instructor can instantly find at-risk students who need attention."

---

### Act 3 — Mark Attendance (2-3 minutes)

**Story:** *"Class is about to start. The instructor marks attendance."*

1. Go back to Dashboard, click **Attendance**
2. Select Subject: CS101, Section: BSIT 1-A — today's date is auto-selected
3. Click **Mark All Present** — all dropdowns switch to Present
4. Change one student to **Absent**
5. Click **Save**

> "Only enrolled students appear. The 'Mark All Present' button handles the common case — the instructor just corrects the exceptions."

6. Show the **date range** feature — type a past date in the "To" field
   - Table switches to read-only history view

> "Instructors can review past attendance across any date range. This can also be printed or exported for ACLC's attendance reporting requirements."

7. Go back to Dashboard — point out the attendance stat updated

---

### Act 4 — Look Up a Student (2-3 minutes)

**Story:** *"A student comes to the office asking about their grades."*

1. Click **Students** from Dashboard
2. Show the **section tabs** — click through a couple
3. Click on a student, then click **View Grades**
4. The **Student Grade Summary** opens with that student pre-selected

> "This is the report card view. One click from the student list and the instructor sees everything — every subject, every season average, the weighted final grade, and even attendance percentage."

5. Click through a few students in the sidebar to show it updating
6. Click **Print**

> "This can be printed directly for the student or their parents."

---

### Act 5 — Semester Transition (2-3 minutes)

**Story:** *"The semester ends. The instructor needs to start fresh for the next one."*

This is your strongest architectural feature — end on a high note.

1. On the Dashboard, click **New Semester**
2. Enter "2025-2026", select "2nd Semester"
3. Switch to the new semester in the dropdown
4. Point out: Students and Subjects counts are unchanged, but attendance shows 0/0

> "Students and subjects carry over — they're global. But enrollment, grades, and attendance start fresh. The instructor re-enrolls students for the new semester and begins from zero."

5. Switch back to "2025-2026 / 1st Semester" — all old data reappears instantly

> "Nothing is lost. The old semester's data is always there. The instructor can switch back and review past records anytime."

6. Delete the test semester (show the warning dialog, confirm)

> "Deleting a semester permanently removes all its enrollment, grades, and attendance. The confirmation dialog makes sure this can't happen by accident."

---

### Closing — Architecture & Reflection (2 minutes)

**Face the panel again.** Don't demo anymore.

> "Under the hood, the system follows a 4-layer architecture:"

```
UI (Swing Forms)  -->  Service (Grade Computation)  -->  DAO (Database Access)  -->  Model (Data Objects)
```

> "Lower layers never reference upper layers. SQL lives only in DAO classes. Grade computation is isolated in one service class. UI styling is centralized in one constants file. If the grade formula changes, one file changes. If we switch from MySQL to another database, only the DAO layer changes."

**Honest limitations:**

> "Passwords are stored in plain text — for a production system, we'd hash them. The app is single-user — supporting multiple instructors would require linking each instructor to their subjects and sections. These are documented as future milestones."

**Closing line:**

> "The ACLC Class Record System replaces manual record-keeping with a complete digital solution. An instructor can manage students, enrollment, attendance, and grades — all in one app, across multiple semesters, with automatic grade computation and export for official submission."

---

## Handling Q&A

### Prepare for these questions

| Question | How to answer |
|----------|---------------|
| "Why Java Swing and not a web app?" | "Swing is the mandated tech stack for this course. It's bundled with Java — zero external dependencies. We documented web migration as a future milestone." |
| "How is the grade formula configurable?" | "Season weights are constants in one file: GradeConstants.java. Changing the formula means editing four numbers. The rest of the system adapts automatically." |
| "What about security?" | "We use PreparedStatement for all SQL to prevent injection. Passwords are plain text for the school project — production would add hashing. This is documented as Milestone 17." |
| "Can scores be out of different totals?" | "Yes. Each assessment has a total items field. A quiz can be /10, an exam /100. The system normalizes to percentages for averaging." |
| "What happens to old data when switching semesters?" | "Nothing — it stays. Semester is a filter, not a delete. Switch back anytime to see old records." |
| "What design patterns did you use?" | "DAO pattern for database access, layered architecture (Model-DAO-Service-UI), and centralized constants for grade formulas and UI styling." |
| "What would you change if you rebuilt this?" | "I'd add password hashing from day one, and consider a web framework for multi-user access. The layered architecture would stay the same — it kept the codebase organized as it grew to 46 Java files." |

### Q&A tips

- **If you don't know the answer**, say so honestly: "I'm not sure, but I'd look into..." The panel respects honesty over bluffing.
- **If asked about a feature you didn't demo**, offer to show it live: "Would you like me to pull that up?"
- **If asked "why not X?"**, don't be defensive. Frame it as a trade-off: "We chose Y because [reason]. X would work too, but [trade-off]."

---

## Quick Reference — Demo Flow

```
OPENING (1 min)
  "Instructors use paper/Excel. This replaces that."
      |
      v
ACT 1 — Start the Day (2-3 min)
  Login --> Dashboard (stats, semester, navigation)
      |
      v
ACT 2 — Enter Today's Scores (3-4 min)    <-- most impressive, show early
  Grades --> Batch Score Entry (quiz /10, whole class)
  --> Grade View (season tabs, Final Grade, failed filter)
      |
      v
ACT 3 — Mark Attendance (2-3 min)
  Attendance --> Mark All Present --> save --> date range view
      |
      v
ACT 4 — Look Up a Student (2-3 min)
  Students --> click student --> View Grades (report card)
      |
      v
ACT 5 — Semester Transition (2-3 min)    <-- strongest architecture, end high
  New Semester --> clean slate --> switch back --> old data intact
      |
      v
CLOSING (2 min)
  Architecture (4 layers) --> honest limitations --> one-line summary
      |
      v
Q&A
```
