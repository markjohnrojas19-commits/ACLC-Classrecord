package model;

public class GradeRecord {

    private Grade grade;
    private ScoreResult scoreResult;

    public GradeRecord(Grade grade, ScoreResult scoreResult) {
        this.grade = grade;
        this.scoreResult = scoreResult;
    }

    public Grade getGrade() {
        return grade;
    }

    public ScoreResult getScoreResult() {
        return scoreResult;
    }
}
