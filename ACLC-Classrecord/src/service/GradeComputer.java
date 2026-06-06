package service;

import java.util.List;
import java.util.Map;

import model.Assessment;
import model.GradingSeason;
import model.ScoreResult;
import util.GradeConstants;

public class GradeComputer {

    public static final String NO_GRADES = "NO GRADES";

    private SeasonGradeComputer seasonComputer = new SeasonGradeComputer();

    public ScoreResult computeAverage(List<Assessment> assessments) {
        return seasonComputer.computeSeasonGrade(assessments);
    }

    public ScoreResult computeFinalGrade(Map<GradingSeason, List<Assessment>> seasonAssessments) {
        double weightedTotal = 0.0;
        double activeWeight = 0.0;

        for (GradingSeason season : GradingSeason.values()) {
            List<Assessment> assessments = seasonAssessments.get(season);
            if (hasNoAssessments(assessments)) {
                continue;
            }
            double seasonGrade = seasonComputer.computeSeasonGrade(assessments).getFinalGrade();
            double weight = getSeasonWeight(season);
            weightedTotal += seasonGrade * weight;
            activeWeight += weight;
        }

        if (activeWeight == 0.0) {
            return new ScoreResult(0.0, NO_GRADES);
        }

        double normalizedGrade = weightedTotal / activeWeight;
        String remarks = determineRemarks(normalizedGrade);
        return new ScoreResult(normalizedGrade, remarks);
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

    private boolean hasNoAssessments(List<Assessment> assessments) {
        return assessments == null || assessments.isEmpty();
    }

    private String determineRemarks(double average) {
        if (average >= GradeConstants.PASSING_GRADE) {
            return "PASSED";
        }
        return "FAILED";
    }
}
