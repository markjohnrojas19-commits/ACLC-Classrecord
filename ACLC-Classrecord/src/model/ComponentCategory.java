package model;

public enum ComponentCategory {
    QUIZ,
    ACTIVITY,
    RECITATION,
    MAJOR_EXAM;

    public String toDisplayName() {
        switch (this) {
            case QUIZ:       return "Quiz";
            case ACTIVITY:   return "Activity";
            case RECITATION: return "Recitation";
            case MAJOR_EXAM: return "Major Exam";
            default:         return name();
        }
    }

    public String toDbValue() {
        return toDisplayName();
    }

    public static ComponentCategory fromDbValue(String value) {
        for (ComponentCategory category : values()) {
            if (category.toDisplayName().equals(value)) {
                return category;
            }
        }
        return QUIZ;
    }
}
