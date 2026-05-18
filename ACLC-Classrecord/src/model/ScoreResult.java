package model;

public class ScoreResult {

    private double finalGrade;
    private String remarks;

    public ScoreResult(double finalGrade, String remarks) {
        this.finalGrade = finalGrade;
        this.remarks = remarks;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
