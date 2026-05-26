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
    UNIQUE (student_id, subject_id, season, assessment_name),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- Insert a default admin user for testing (password: admin123)
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'Admin')
ON DUPLICATE KEY UPDATE username = username;
