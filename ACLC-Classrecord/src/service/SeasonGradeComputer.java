package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Assessment;
import model.ComponentCategory;
import model.ScoreResult;
import util.GradeConstants;

public class SeasonGradeComputer {

    public ScoreResult computeSeasonGrade(List<Assessment> assessments) {
        if (assessments == null || assessments.isEmpty()) {
            return new ScoreResult(0.0, GradeComputer.NO_GRADES);
        }

        Map<ComponentCategory, List<Assessment>> groups = groupByComponent(assessments);
        double weightedTotal = 0.0;
        double activeWeight = 0.0;

        for (Map.Entry<ComponentCategory, List<Assessment>> entry : groups.entrySet()) {
            double componentAvg = calculateAverage(entry.getValue());
            double weight = getComponentWeight(entry.getKey());
            weightedTotal += componentAvg * weight;
            activeWeight += weight;
        }

        if (activeWeight == 0.0) {
            return new ScoreResult(calculateAverage(assessments), determineRemarks(calculateAverage(assessments)));
        }

        double normalizedGrade = weightedTotal / activeWeight;
        return new ScoreResult(normalizedGrade, determineRemarks(normalizedGrade));
    }

    private Map<ComponentCategory, List<Assessment>> groupByComponent(List<Assessment> assessments) {
        Map<ComponentCategory, List<Assessment>> groups = new HashMap<>();
        for (Assessment assessment : assessments) {
            groups.computeIfAbsent(assessment.getComponent(), k -> new ArrayList<>())
                  .add(assessment);
        }
        return groups;
    }

    private double calculateAverage(List<Assessment> assessments) {
        double total = 0.0;
        for (Assessment assessment : assessments) {
            total += assessment.getPercentage();
        }
        return total / assessments.size();
    }

    private double getComponentWeight(ComponentCategory component) {
        switch (component) {
            case QUIZ:       return GradeConstants.QUIZ_WEIGHT;
            case ACTIVITY:   return GradeConstants.ACTIVITY_WEIGHT;
            case RECITATION: return GradeConstants.RECITATION_WEIGHT;
            case MAJOR_EXAM: return GradeConstants.MAJOR_EXAM_WEIGHT;
            default:         return 0.0;
        }
    }

    private String determineRemarks(double average) {
        if (average >= GradeConstants.PASSING_GRADE) {
            return "PASSED";
        }
        return "FAILED";
    }
}
