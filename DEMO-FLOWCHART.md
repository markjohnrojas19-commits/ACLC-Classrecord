# ACLC Class Record System — Presentation Guide

> **Audience:** Instructor + Panelists
> **Duration:** 5 to 15 minutes max
> **Language:** Strictly English only
> **Dress Code:** Semi-formal attire or official school uniform (ALL members must comply)
> **Golden rule:** Assign parts to team members ahead of time for seamless transitions.

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
- [ ] Each member knows their assigned part

### Mindset

- **Move the mouse slowly.** Projectors make fast mouse movements invisible.
- **Explain why you made decisions**, not just what the app does. The panel tests your understanding, not just your output.
- **If something breaks**, stay calm. Say "Let me try that again" or move on. Don't panic.

---

## Part 1 — Greeting & Introduction (1-2 minutes)

**Face the panel. Don't touch the app yet.**

1. **Polite opening** — greet the panel and instructor
2. **Introduce each member** — name and role (e.g., "I'm [Name], the lead developer")
3. **State the project title:**

> "Our project is the **ACLC Class Record System** — a desktop application built in Java Swing with MySQL that replaces manual record keeping — whether that's Excel spreadsheets, paper forms, or other general-purpose tools — with a purpose-built solution for ACLC instructors."

---

## Part 2 — Project Core (2-3 minutes)

**Still facing the panel. Explain the goal, objectives, and key features.**

### Goal

> "The goal is to give ACLC instructors a dedicated class record application — replacing scattered tools like Excel files, paper forms, or generic apps with one system where they can manage students, enrollment, attendance, and grades across multiple semesters."

### Objectives

> "Our objectives are:"
> 1. "Eliminate manual grade computation — the system auto-computes weighted final grades using ACLC's standard formula."
> 2. "Centralize student data — enrollment, grades, and attendance in one place instead of scattered Excel files, paper records, or multiple apps."
> 3. "Support semester transitions — instructors can start fresh each semester without losing past records."
> 4. "Enable reporting — print or export grade summaries and attendance for official submission."

### Key Features

> "The system has five core features:"
> 1. "**Student Management** — add, import, and organize students by section."
> 2. "**Enrollment** — enroll students into subjects per semester with batch operations."
> 3. "**Grade Management** — batch score entry, automatic grade computation across four seasons (Prelim, Midterm, Pre-Final, Final), with configurable weight formula."
> 4. "**Attendance Tracking** — mark attendance per subject per date, with date-range history and export."
> 5. "**Semester System** — create and switch between semesters. All data is scoped — old records are preserved."

---

## Part 3 — Expected Outcome (1-2 minutes)

**Explain the value and impact of the project.**

> "With this system, an ACLC instructor can:"
> - "Record an entire class's quiz scores in under 2 minutes — compared to 20-30 minutes manually."
> - "See at a glance which students are failing and need intervention — the dashboard highlights this automatically."
> - "Generate a student's complete grade summary with one click — ready to print for the student or their parents."
> - "Switch between semesters instantly — no data is lost, no files to manage."

> "The expected impact is a significant reduction in the instructor's administrative workload, fewer computation errors, and faster access to student performance data for decision-making."

---

## Part 4 — Live Demonstration (5-7 minutes)

**Now turn to the app. Walk the panel through a smooth, realistic workflow.**

### Login & Dashboard (1 minute)

1. Log in with `admin` / `admin123`

> "Credentials are verified against MySQL using prepared statements — preventing SQL injection."

2. Land on the Dashboard — pause and let the panel see it
3. Point out three things only:
   - **Stats cards** — live counts (students, subjects, today's attendance)
   - **Semester selector** — currently "2025-2026 / 1st Semester"
   - **Per-subject stats table** — shows which subjects have failing students

> "This is the instructor's hub — key numbers at a glance and navigation to every feature."

### Batch Score Entry (2 minutes)

This is the most impressive workflow — show it clearly.

1. Click **Grades** from Dashboard
2. Click **Enter Scores** — BatchScoreEntryForm opens
3. Select Subject: CS101, Section: BSIT 1-A, Season: Prelim
4. Type Assessment Name: "Quiz 3", set Total Items: **10**
5. Click **Load Students** — all enrolled students appear

> "A quiz can be out of 10, an exam out of 50. The system normalizes everything to percentages automatically."

6. Enter scores quickly (8, 7, 9, 6, 10...) — point out yellow highlighting for missing scores
7. Click **Save All**

> "One screen, one assessment, the whole class done."

8. Click back to **Grades** — show the Prelim tab with the score format: "8/10 (80.0%)"
9. Click the **Final Grade** tab — show the weighted final grade (green = passed, red = failed)

> "Grades auto-compute using ACLC's formula: Prelim 20%, Midterm 20%, Pre-Final 20%, Final 40%."

### Attendance (1-2 minutes)

1. Go back to Dashboard, click **Attendance**
2. Select Subject: CS101, Section: BSIT 1-A — today's date is auto-selected
3. Click **Mark All Present**, change one student to **Absent**, click **Save**

> "Only enrolled students appear. 'Mark All Present' handles the common case — the instructor just corrects exceptions."

4. Show the **date range** feature briefly

> "Past attendance can be reviewed, printed, or exported for reporting."

### Student Grade Summary (1 minute)

1. Click **Students** from Dashboard
2. Click on a student, then click **View Grades**

> "One click — the instructor sees every subject, every season average, the final grade, and attendance percentage. This can be printed directly for the student."

### Semester Transition (1 minute)

1. On the Dashboard, click **New Semester** — enter "2025-2026", select "2nd Semester"
2. Switch to the new semester — point out: Students/Subjects unchanged, but grades/attendance are fresh

> "Everything starts clean. But switching back to the old semester — all data is still there."

3. Switch back to show old data reappearing, then delete the test semester

---

## Part 5 — Closing & Q&A Session (1-2 minutes)

**Face the panel again. Don't demo anymore.**

### Architecture Summary

> "Under the hood, the system follows a 4-layer architecture:"

```
UI (Swing Forms)  -->  Service (Grade Computation)  -->  DAO (Database Access)  -->  Model (Data Objects)
```

> "Lower layers never reference upper layers. If the grade formula changes, one file changes. If the database changes, only the DAO layer changes."

### Honest Limitations

> "Passwords are stored in plain text — for production, we'd hash them. The app is single-user — supporting multiple instructors would require linking each instructor to their subjects. These are documented as future milestones."

### Closing Statement

> "The ACLC Class Record System replaces scattered tools like Excel, paper forms, and generic apps with a single, purpose-built solution. An instructor can manage students, enrollment, attendance, and grades — all in one app, across multiple semesters, with automatic grade computation and export for official submission."

> "Thank you. We're now open for questions."

### Q&A — Prepare for These Questions

| Question | How to answer |
|----------|---------------|
| "Why Java Swing and not a web app?" | "Swing is the mandated tech stack for this course. It's bundled with Java — zero external dependencies. We documented web migration as a future milestone." |
| "How is the grade formula configurable?" | "Season weights are constants in one file: GradeConstants.java. Changing the formula means editing four numbers." |
| "What about security?" | "We use PreparedStatement for all SQL to prevent injection. Passwords are plain text for this school project — production would add hashing." |
| "Can scores be out of different totals?" | "Yes. Each assessment has a total items field. The system normalizes to percentages for averaging." |
| "What happens to old data when switching semesters?" | "Nothing — it stays. Semester is a filter, not a delete. Switch back anytime." |
| "What design patterns did you use?" | "DAO pattern for database access, layered architecture (Model-DAO-Service-UI), and centralized constants for grade formulas and UI styling." |
| "What would you change if you rebuilt this?" | "Password hashing from day one, and a web framework for multi-user access. The layered architecture would stay the same." |

### Q&A Tips

- **If you don't know the answer**, say so honestly: "I'm not sure, but I'd look into..."
- **If asked about a feature you didn't demo**, offer to show it live: "Would you like me to pull that up?"
- **If asked "why not X?"**, don't be defensive. Frame it as a trade-off: "We chose Y because [reason]. X would work too, but [trade-off]."

---

## Quick Reference — Presentation Flow

```
PART 1 — GREETING & INTRODUCTION (1-2 min)
  Greet panel --> introduce members --> state project title
      |
      v
PART 2 — PROJECT CORE (2-3 min)
  Goal --> objectives (4 points) --> key features (5 features)
      |
      v
PART 3 — EXPECTED OUTCOME (1-2 min)
  Value: faster grading, fewer errors, instant reports, semester management
      |
      v
PART 4 — LIVE DEMONSTRATION (5-7 min)
  Login/Dashboard --> Batch Score Entry --> Attendance
  --> Student Grade Summary --> Semester Transition
      |
      v
PART 5 — CLOSING & Q&A (1-2 min)
  Architecture --> honest limitations --> closing statement --> open floor
```

**Total: 10-16 minutes** (aim for 12-13 to stay within the 15-minute cap)
