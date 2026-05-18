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

-- Grades table
CREATE TABLE IF NOT EXISTS grades (
    grade_id      INT AUTO_INCREMENT PRIMARY KEY,
    student_id    VARCHAR(20) NOT NULL,
    subject_id    INT NOT NULL,
    quiz          DOUBLE DEFAULT 0,
    assignment    DOUBLE DEFAULT 0,
    exam          DOUBLE DEFAULT 0,
    final_grade   DOUBLE DEFAULT 0,
    remarks       VARCHAR(10) DEFAULT 'N/A',
    UNIQUE (student_id, subject_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- Insert a default admin user for testing (password: admin123)
INSERT INTO users (username, password, role)
VALUES ('admin', 'admin123', 'Admin')
ON DUPLICATE KEY UPDATE username = username;
