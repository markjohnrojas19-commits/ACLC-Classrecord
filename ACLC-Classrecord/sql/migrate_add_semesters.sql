-- Migration: Add semester support to existing database
-- Run this in phpMyAdmin AFTER backing up your data.

USE aclc_classrecord_db;

-- 1. Create semesters table
CREATE TABLE IF NOT EXISTS semesters (
    semester_id   INT AUTO_INCREMENT PRIMARY KEY,
    school_year   VARCHAR(20) NOT NULL,
    semester      INT NOT NULL,
    is_active     BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (school_year, semester)
);

-- 2. Insert default semester (all existing data belongs to this semester)
INSERT INTO semesters (school_year, semester, is_active)
VALUES ('2025-2026', 1, TRUE)
ON DUPLICATE KEY UPDATE is_active = TRUE;

-- 3. Add semester_id column to enrollments, assessments, attendance
--    Default 1 so existing rows are assigned to the default semester.
ALTER TABLE enrollments ADD COLUMN semester_id INT NOT NULL DEFAULT 1;
ALTER TABLE assessments ADD COLUMN semester_id INT NOT NULL DEFAULT 1;
ALTER TABLE attendance  ADD COLUMN semester_id INT NOT NULL DEFAULT 1;

-- 4. Add foreign key constraints
ALTER TABLE enrollments ADD FOREIGN KEY (semester_id) REFERENCES semesters(semester_id);
ALTER TABLE assessments ADD FOREIGN KEY (semester_id) REFERENCES semesters(semester_id);
ALTER TABLE attendance  ADD FOREIGN KEY (semester_id) REFERENCES semesters(semester_id);

-- 5. Update unique constraints to include semester_id
--    Drop old constraints, add new ones.
ALTER TABLE enrollments DROP INDEX student_id, ADD UNIQUE (student_id, subject_id, semester_id);
ALTER TABLE assessments DROP INDEX student_id, ADD UNIQUE (student_id, subject_id, season, assessment_name, semester_id);
ALTER TABLE attendance  DROP INDEX student_id, ADD UNIQUE (student_id, subject_id, date, semester_id);
