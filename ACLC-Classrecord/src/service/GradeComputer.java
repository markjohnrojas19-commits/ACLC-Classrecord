package service;

import java.util.List;

import model.Assessment;
import model.ScoreResult;
import util.GradeConstants;

public class GradeComputer {

    public ScoreResult computeAverage(List<Assessment> assessments) {
        double average = calculateAverage(assessments);
        String remarks = determineRemarks(average);
        return new ScoreResult(average, remarks);
    }

    private double calculateAverage(List<Assessment> assessments) {
        if (assessments.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Assessment assessment : assessments) {
            total += assessment.getScore();
        }
        return total / assessments.size();
    }

    private String determineRemarks(double average) {
        if (average >= GradeConstants.PASSING_GRADE) {
            return "PASSED";
        }
        return "FAILED";
    }
}
