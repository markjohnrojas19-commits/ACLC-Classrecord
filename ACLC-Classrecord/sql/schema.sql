-- ACLC Class Record System — Database Schema
-- Run this script in phpMyAdmin (XAMPP) to create the database and all tables.

CREATE DATABASE IF NOT EXISTS aclc_classrecord_db;
USE aclc_classrecord_db;

-- Users table (login credentials)
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password      VARCHAR(255) NOT NULL,
    role          ENUM('Admin', 'Instructor') NOT NULL
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
    student_id    VARCHAR(20) PRIMARY KEY,
    firstname     VARCHAR(50) NOT NULL,
    lastname      VARCHAR(50) NOT NULL,
    course        VARCHAR(50) NOT NULL,
    year_level    INT NOT NULL,
    section       VARCHAR(10) NOT NULL,
    gender        ENUM('Male', 'Female') NOT NULL
);

-- Subjects table
CREATE TABLE IF NOT EXISTS subjects (
    subject_id    INT AUTO_INCREMENT PRIMARY KEY,
    subject_code  VARCHAR(20) UNIQUE NOT NULL,
    subject_name  VARCHAR(100) NOT NULL
);

-- Semesters table
-- Each row is one semester (e.g., "2025-2026 / 1st Semester").
-- is_active marks which semester is currently selected in the app.
CREATE TABLE IF NOT EXISTS semesters (
    semester_id   INT AUTO_INCREMENT PRIMARY KEY,
    school_year   VARCHAR(20) NOT NULL,
    semester      INT NOT NULL,
    is_active     BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (school_year, semester)
);

-- Insert default semester
INSERT INTO semesters (school_year, semester, is_active)
VALUES ('2025-2026', 1, TRUE)
ON DUPLICATE KEY UPDATE school_year = school_year;

-- Assessments table (replaces old grades table)
-- Each row is one assessment score for a student in a subject during a grading season.
-- Examples: "Quiz 1" under Midterm, "Unit Test A" under Prelim, "Project" under Final.
CREATE TABLE IF NOT EXISTS assessments (
    assessment_id    INT AUTO_INCREMENT PRIMARY KEY,
    student_id       VARCHAR(20) NOT NULL,
    subject_id       INT NOT NULL,
    season           ENUM('Prelim', 'Midterm', 'Pre-Final', 'Final') NOT NULL,
    assessment_name  VARCHAR(50) NOT NULL,
    score            DOUBLE NOT NULL DEFAULT 0,
    total_items      DOUBLE NOT NULL DEFAULT 100,
    date             DATE DEFAULT NULL,
    semester_id      INT NOT NULL DEFAULT 1,
    UNIQUE (student_id, subject_id, season, assessment_name, semester_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id)
);

-- Enrollments table
-- Links students to subjects. One row = "this student is enrolled in this subject."
-- Attendance and grade forms use this to show only enrolled students.
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id     VARCHAR(20) NOT NULL,
    subject_id     INT NOT NULL,
    semester_id    INT NOT NULL DEFAULT 1,
    UNIQUE (student_id, subject_id, semester_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id)
);

-- Attendance table
-- One row = one student's attendance for one subject on one date.
-- Only enrolled students should have attendance records.
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id  INT AUTO_INCREMENT PRIMARY KEY,
    student_id     VARCHAR(20) NOT NULL,
    subject_id     INT NOT NULL,
    date           DATE NOT NULL,
    status         ENUM('Present', 'Absent', 'Late', 'Excused') NOT NULL,
    semester_id    INT NOT NULL DEFAULT 1,
    UNIQUE (student_id, subject_id, date, semester_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (semester_id) REFERENCES semesters(semester_id)
);

-- Insert a default admin user for testing (password: admin123)
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'Admin')
ON DUPLICATE KEY UPDATE username = username;
