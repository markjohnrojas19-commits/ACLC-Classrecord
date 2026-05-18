package model;

public class Grade {

    private int gradeId;
    private String studentId;
    private int subjectId;
    private double quiz;
    private double assignment;
    private double exam;

    public Grade(int gradeId, String studentId, int subjectId,
                 double quiz, double assignment, double exam) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.quiz = quiz;
        this.assignment = assignment;
        this.exam = exam;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
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

    public double getQuiz() {
        return quiz;
    }

    public void setQuiz(double quiz) {
        this.quiz = quiz;
    }

    public double getAssignment() {
        return assignment;
    }

    public void setAssignment(double assignment) {
        this.assignment = assignment;
    }

    public double getExam() {
        return exam;
    }

    public void setExam(double exam) {
        this.exam = exam;
    }
}
