package service;

import model.ScoreResult;
import util.GradeConstants;

public class GradeComputer {

    public ScoreResult compute(double quiz, double assignment, double exam) {
        double finalGrade = computeWeightedAverage(quiz, assignment, exam);
        String remarks = determineRemarks(finalGrade);
        return new ScoreResult(finalGrade, remarks);
    }

    private double computeWeightedAverage(double quiz, double assignment, double exam) {
        return (quiz * GradeConstants.QUIZ_WEIGHT)
             + (assignment * GradeConstants.ASSIGNMENT_WEIGHT)
             + (exam * GradeConstants.EXAM_WEIGHT);
    }

    private String determineRemarks(double finalGrade) {
        if (finalGrade >= GradeConstants.PASSING_GRADE) {
            return "PASSED";
        }
        return "FAILED";
    }
}
