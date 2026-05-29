# Task Board — User-Identified Issues

> Created: 2026-05-29
> These are tasks identified by the developer/user — things that need fixing or rethinking.
> NOT milestones. When ready to tackle one, say "let's do T1" (or whichever).

---

## T1. UI Overhaul

**The problem:** The system works, but it looks like a default Java Swing app from 2005. Plain gray panels, default button styles, no visual hierarchy, no breathing room. An instructor opening this for the first time wouldn't feel confident in the tool.

**What needs attention:**
- **Color scheme** — The app has `StyleConstants` with colors defined, but the overall look is still very "raw Swing." Buttons are plain, panels are flat gray, there's no visual warmth or modern feel.
- **Typography** — Font sizes exist in StyleConstants but the layout feels dense. Labels, fields, and buttons are crammed together without enough padding.
- **Button styling** — All buttons look identical regardless of importance. "Save" and "Clear" have the same visual weight. Primary actions (Save, Add) should stand out. Destructive actions (Delete) should look different.
- **Form layout** — Input panels use basic GridLayout which makes everything the same size. A more thoughtful layout with proper alignment, spacing, and grouping would look more professional.
- **Table styling** — Tables have alternating rows and dark headers (good), but could use better column sizing, cell padding, and hover effects.
- **Overall polish** — Rounded borders, subtle shadows, icon usage, a proper color palette that feels cohesive. The dashboard especially should feel like a landing page, not a list of buttons.

**Options to explore:**
- **FlatLaf** — A modern, flat Look-and-Feel for Swing. Drop-in replacement, one line of code to activate, makes the entire app look like a modern desktop application. Has light and dark themes. Very popular in the Java community.
- **Custom styling** — Build custom components (rounded buttons, card panels, etc.) using Swing's painting API. More work, but full control.
- **Hybrid** — Use FlatLaf as the base, then customize specific components where needed.

**Scope:** Every form in the system. This is a cross-cutting concern — it touches all UI files.

---

## ~~T2. Rethink Grade Entry — GradeForm vs. Batch Score Entry~~ DONE

**Resolution:** Implemented Option B. GradeForm became a view-only grade dashboard (view, filter, print, export). BatchScoreEntryForm handles all new score entry + delete. Navigation: Dashboard → Grades → Enter Scores → Back to Grades. "Batch Score Entry" button removed from dashboard.

**The problem:** We now have two ways to enter scores:
1. **GradeForm** — Select one student, one subject, one season, type assessment name, type score. Click Add. Repeat 40 times.
2. **BatchScoreEntryForm** — Select subject + section + season + assessment name once. Type scores for all 40 students. Click Save All.

**Why would an instructor ever use GradeForm for data entry?** Batch is faster in every scenario. GradeForm's individual add workflow is obsolete for entering new scores. But GradeForm still has value for:
- **Viewing** grades organized by season tabs
- **Editing** a single existing score (click row, change, save)
- **Deleting** a specific assessment
- **Filtering** by section, pass/fail, search
- **Final Grade tab** with weighted computation
- **Print / Export CSV**

**The real question:** Should GradeForm stop being an entry form and become a **Grade Viewer / Manager** instead? The input panel at the top takes up screen space for a workflow that Batch Entry does better.

**Options:**
| Option | Description | Trade-off |
|--------|-------------|-----------|
| **A. Keep both as-is** | No changes. GradeForm still has Add, Batch Entry still exists. | Redundant entry paths. Confusing — which do I use? |
| **B. GradeForm becomes view-only + edit/delete** | Remove the Add button from GradeForm. Keep Edit + Delete for managing existing scores. All new score entry goes through Batch Entry. | Clean separation: Batch = enter, GradeForm = view/manage. |
| **C. Merge into one form** | Combine batch entry into GradeForm. Season tabs show scores, a "Batch Enter" mode lets you fill in scores for all students at once within the same form. | One place for everything, but the form gets complex. |
| **D. GradeForm shows only summaries** | Remove individual assessment rows from GradeForm entirely. Show only season averages and final grades. All assessment-level work goes through Batch Entry. | Simplest GradeForm, but loses the ability to see/edit individual assessments. |

**Recommendation:** Option B feels right. GradeForm becomes the "grade dashboard" — view, filter, edit, delete, export. Batch Entry is the "data entry" tool. Clear roles, no overlap.

---

## ~~T3. Adjustable Score (Total Items / Max Score per Assessment)~~ DONE

**Resolution:** Added `totalItems` (default 100) and `date` fields to Assessment model, database, DAO, and all UI forms. Scores normalized to percentages via `getPercentage()`. GradeForm shows "8.0/10 (80.0%)" for non-100 totals. Backward compatible — existing data defaults to totalItems=100.

**The problem:** Every score is currently 0-100. But in reality:
- A 10-item quiz has a max score of 10 (or 20, or 50)
- A midterm exam might be out of 80
- A project might be out of 150

Right now, if a quiz has 10 items and a student got 8, the instructor has to mentally convert (8/10 = 80%) and type "80". That's error-prone and annoying. The instructor wants to type "8" and have the system know it's out of 10.

**What the system currently does:**
- `Assessment` model: has `score` (double), no concept of max score
- `GradeConstants`: `MIN_SCORE = 0.0`, `MAX_SCORE = 100.0`
- `GradeComputer.calculateAverage()`: sums scores and divides by count — assumes all scores are on the same scale (0-100)
- Validation: score must be between 0 and 100

**What it needs:**
- A `totalItems` (or `maxScore`) field on each assessment
- A `date` field on each assessment (when the assessment happened)
- Score validation changes: score must be between 0 and `totalItems` (not fixed 100)
- Grade computation changes: convert each score to a percentage first (`score / totalItems * 100`), then average the percentages
- Batch Entry: add "Total Items" and "Date" fields in the filter panel (shared for the whole batch)
- Display: show both raw score and percentage (e.g., "8/10 (80%)")
- GradeForm season tabs: show Date column alongside existing columns

**Bundled: Assessment Date** — Originally skipped in Milestone 14.5. Now bundled with T3 because both add columns to the same model/table/DAO/forms. One pass through everything instead of two.

**Impact:**
- `Assessment` model — add `totalItems` field + `date` field
- `assessments` table — add `total_items` column (DOUBLE, default 100) + `date` column (DATE, nullable for backward compat)
- `AssessmentDao` — update all queries to include total_items and date
- `GradeComputer` — normalize scores to percentages before averaging
- `BatchScoreEntryForm` / `BatchScoreFilterPanel` — add Total Items + Date inputs
- `GradeForm` — season tab tables show date + score as "8/10 (80%)"
- `StudentGradeSummaryForm` — display considers percentages
- All export/print — show raw score + total or percentage

**Backward compatibility:** Existing assessments without a total_items value default to 100 (current data stays correct). Existing assessments without a date show as blank.

---

## T4. Bulk Student Import (CSV / Copy-Paste)

**The problem:** Adding 40 students one-by-one means typing 7 fields x 40 students = 280 individual inputs. That's 20-30 minutes of tedious data entry at the start of every semester. Course, year level, and section are usually the same for an entire class — yet the instructor re-types them every row.

**What the system currently does:**
- `StudentInputPanel` has 7 text fields: Student ID, First Name, Last Name, Course, Year Level, Section, Gender
- Add one student at a time, click Add, fields clear, repeat
- No way to import or paste data

**Options:**

| Option | Description | Complexity |
|--------|-------------|------------|
| **A. CSV Import** | "Import" button opens a file chooser. Instructor uploads a CSV with columns: StudentID, FirstName, LastName, Course, YearLevel, Section, Gender. System parses and bulk-inserts. | Medium — file parsing, validation, error reporting |
| **B. Copy-Paste from Excel** | "Paste" button reads clipboard data (tab-separated from Excel/Sheets). Populates a preview table. Instructor reviews and clicks "Import All." | Medium — clipboard parsing, preview table |
| **C. Batch add with sticky fields** | Course, Year Level, Section, and Gender fields "stick" (don't clear) after adding a student. Only ID and name fields clear. This way the instructor only types 3 fields per student instead of 7. | Low — small change to `clear()` method |
| **D. Editable table entry** | Show an empty table where the instructor types directly into cells (like a spreadsheet). "Save All" button bulk-inserts. Similar to how Batch Score Entry works. | Medium — new form, table editing |
| **E. A + C combined** | CSV import for the initial load, sticky fields for adding stragglers during the semester. | Medium — best of both worlds |

**Recommendation:** Option E. CSV import handles the start-of-semester bulk load (instructor likely has a class list in Excel already). Sticky fields handle the 1-2 students who transfer in mid-semester. Both are independently useful.

---

## T5. Lacking Assessments View — See Who's Missing Scores

**The problem:** An instructor gives 5 quizzes and 2 exams during Prelim season. Some students were absent or missed submissions. Right now, there's no easy way to see which students are **missing** scores for a specific assessment. The instructor has to mentally cross-reference the student list against the scores list — tedious and error-prone with 40+ students.

**What the instructor wants:**
- Pick a subject, section, and season
- See a list of assessments given (e.g., "Quiz 1", "Quiz 2", "Unit Test A")
- For each assessment, see which students **don't** have a score (lacking/incomplete)
- Alternatively: see a per-student view showing which assessments they're missing

**Use cases:**
- "Who hasn't taken Quiz 3 yet?" — to schedule make-up tests
- "Does anyone have incomplete grades before I submit final marks?"
- "Which students are missing more than 2 assessments?" — early warning for at-risk students

---

## T6. Section Filter in Student Grade Summary

**The problem:** The Student Grade Summary form shows a dropdown of **all students** in the system. If the instructor has 6 sections × 40 students = 240 students, scrolling through a single dropdown to find one student is painful. There's no way to narrow it down by section.

**What the instructor wants:**
- A section dropdown (or subject dropdown) that filters the student list
- Select "Section A" → student dropdown shows only Section A students
- Makes it fast to browse through one class at a time instead of the entire school

**Scope:** Small change — add a filter dropdown above the student dropdown in `StudentGradeSummaryForm`.

---

## Summary

| ID | Task | Core Issue |
|----|------|------------|
| T1 | UI Overhaul | Looks dated, needs modern styling |
| ~~T2~~ | ~~Rethink Grade Entry~~ | ~~DONE — GradeForm is view-only, Batch Entry handles input~~ |
| ~~T3~~ | ~~Adjustable Score~~ | ~~DONE — totalItems + date per assessment, percentage-based averaging~~ |
| T4 | Bulk Student Import | Adding students one-by-one is too slow |
| T5 | Lacking Assessments View | No way to see who's missing scores |
| T6 | Section Filter in Student Summary | Student dropdown has all 240 students, no way to filter by section |
