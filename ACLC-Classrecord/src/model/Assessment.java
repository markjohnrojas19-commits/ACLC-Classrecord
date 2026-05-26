package model;

public class Assessment {

    private int assessmentId;
    private String studentId;
    private int subjectId;
    private GradingSeason season;
    private String assessmentName;
    private double score;

    public Assessment(int assessmentId, String studentId, int subjectId,
                      GradingSeason season, String assessmentName, double score) {
        this.assessmentId = assessmentId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.season = season;
        this.assessmentName = assessmentName;
        this.score = score;
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
}
