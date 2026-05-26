package model;

public enum GradingSeason {
    PRELIM,
    MIDTERM,
    PRE_FINAL,
    FINAL;

    public String toDisplayName() {
        switch (this) {
            case PRELIM:    return "Prelim";
            case MIDTERM:   return "Midterm";
            case PRE_FINAL: return "Pre-Final";
            case FINAL:     return "Final";
            default:        return name();
        }
    }

    public String toDbValue() {
        return toDisplayName();
    }

    public static GradingSeason fromDbValue(String value) {
        for (GradingSeason season : values()) {
            if (season.toDisplayName().equals(value)) {
                return season;
            }
        }
        return PRELIM;
    }
}
