package service;

import java.util.List;
import java.util.Map;

import model.Assessment;
import model.GradingSeason;
import model.ScoreResult;
import util.GradeConstants;

public class GradeComputer {

    public ScoreResult computeAverage(List<Assessment> assessments) {
        double average = calculateAverage(assessments);
        String remarks = determineRemarks(average);
        return new ScoreResult(average, remarks);
    }

    public ScoreResult computeFinalGrade(Map<GradingSeason, List<Assessment>> seasonAssessments) {
        double weightedTotal = 0.0;

        for (GradingSeason season : GradingSeason.values()) {
            List<Assessment> assessments = seasonAssessments.get(season);
            double seasonAverage = calculateAverage(assessments);
            weightedTotal += seasonAverage * getSeasonWeight(season);
        }

        String remarks = determineRemarks(weightedTotal);
        return new ScoreResult(weightedTotal, remarks);
    }

    private double getSeasonWeight(GradingSeason season) {
        switch (season) {
            case PRELIM:    return GradeConstants.PRELIM_WEIGHT;
            case MIDTERM:   return GradeConstants.MIDTERM_WEIGHT;
            case PRE_FINAL: return GradeConstants.PRE_FINAL_WEIGHT;
            case FINAL:     return GradeConstants.FINAL_WEIGHT;
            default:        return 0.0;
        }
    }

    private double calculateAverage(List<Assessment> assessments) {
        if (assessments == null || assessments.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Assessment assessment : assessments) {
            total += assessment.getPercentage();
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
