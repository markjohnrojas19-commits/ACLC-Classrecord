-- Migration: Add component category column to assessments
-- Run this ONCE on existing databases. New installs use the updated schema.sql.

USE aclc_classrecord_db;

-- 1. Add component column (existing assessments default to 'Quiz')
ALTER TABLE assessments
ADD COLUMN component ENUM('Quiz', 'Activity', 'Recitation', 'Major Exam')
NOT NULL DEFAULT 'Quiz'
AFTER season;

-- 2. Update uniqueness constraint to include component
ALTER TABLE assessments
DROP INDEX student_id,
ADD UNIQUE (student_id, subject_id, season, component, assessment_name, semester_id);
