# ACLC Class Record System

A Java Swing desktop application for ACLC instructors to manage students, subjects, enrollment, attendance, and grades — replacing manual pen-and-paper record keeping. Built with MySQL (XAMPP) for data persistence.

## Features

### Core
- **Login System** — Admin and Instructor roles with database-backed authentication
- **Dashboard** — Live stats (students, subjects, today's attendance), per-subject pass/fail table, semester selector
- **Student Management** — CRUD with section tabs, subject filter, search, bulk add, CSV import
- **Subject Management** — CRUD with search and column sorting
- **Enrollment** — Checkbox table to enroll students in subjects by section, select-all header

### Grading
- **Batch Score Entry** — Enter scores for an entire class in one screen per assessment
- **Grade View** — Season tabs (Prelim, Midterm, Pre-Final, Final) + Final Grade tab with weighted average
- **Student Grade Summary** — Report card view per student with attendance percentage
- **Configurable Formula** — Season weights (20/20/20/40) and passing grade (75) in one constants file
- **Score Normalization** — Assessments can be out of any total (quiz /10, exam /100), auto-normalized to percentages

### Attendance
- **Daily Marking** — Status dropdowns (Present, Absent, Late, Excused) with "Mark All Present" bulk action
- **Date Range View** — Read-only history view across any date range
- **Date Picker** — Dropdown of dates with existing records, or type a custom date

### Semester Management
- **Multi-Semester Support** — Create, switch, and delete semesters from the dashboard
- **Scoped Data** — Enrollment, grades, and attendance are scoped per semester; students and subjects are global
- **Clean Slate** — New semester starts fresh without losing old data

### Export
- **Print** — Available on grades, attendance, enrollment, and student summary forms
- **CSV Export** — Available on grades, attendance, and enrollment forms

## Architecture

```
UI (Swing Forms)  -->  Service (Grade Computation)  -->  DAO (Database Access)  -->  Model (Data Objects)
```

- 11 Model classes, 9 DAO classes, 1 Service class, 4 Utility classes, 21 UI classes
- Lower layers never reference upper layers
- All SQL uses PreparedStatement with parameterized queries
- UI styling centralized in StyleConstants

## Tech Stack

| Tool | Purpose |
|------|---------|
| Java | Language |
| Java Swing | GUI framework |
| MySQL | Database (7 tables) |
| XAMPP | Local MySQL server |
| JDBC | Database connectivity |
| NetBeans IDE | Development environment |

## Prerequisites

- NetBeans IDE
- XAMPP (with MySQL running)
- MySQL Connector/J (JDBC driver)

## Setup

1. Start XAMPP and ensure MySQL is running
2. Import `sql/schema.sql` into phpMyAdmin to create the database
3. Open the project in NetBeans
4. Add MySQL Connector/J to project libraries
5. Run the application
6. Login with default credentials: `admin` / `admin123`

## Database

7 tables: `users`, `students`, `subjects`, `semesters`, `enrollments`, `assessments`, `attendance`

See `sql/schema.sql` for the full schema. For existing databases, use `sql/migrate_add_semesters.sql` to add semester support.
