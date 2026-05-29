package model;

import java.time.LocalDate;

import util.GradeConstants;

public class Assessment {

    private int assessmentId;
    private String studentId;
    private int subjectId;
    private GradingSeason season;
    private String assessmentName;
    private double score;
    private double totalItems;
    private LocalDate date;

    public Assessment(int assessmentId, String studentId, int subjectId,
                      GradingSeason season, String assessmentName, double score) {
        this(assessmentId, studentId, subjectId, season, assessmentName, score,
             GradeConstants.DEFAULT_TOTAL_ITEMS, null);
    }

    public Assessment(int assessmentId, String studentId, int subjectId,
                      GradingSeason season, String assessmentName, double score,
                      double totalItems, LocalDate date) {
        this.assessmentId = assessmentId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.season = season;
        this.assessmentName = assessmentName;
        this.score = score;
        this.totalItems = totalItems;
        this.date = date;
    }

    public double getPercentage() {
        if (totalItems <= 0) {
            return 0.0;
        }
        return (score / totalItems) * 100.0;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public GradingSeason getSeason() {
        return season;
    }

    public void setSeason(GradingSeason season) {
        this.season = season;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(double totalItems) {
        this.totalItems = totalItems;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
