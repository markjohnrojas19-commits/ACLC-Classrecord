package model;

public enum AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    EXCUSED;

    public String toDisplayName() {
        switch (this) {
            case PRESENT: return "Present";
            case ABSENT:  return "Absent";
            case LATE:    return "Late";
            case EXCUSED: return "Excused";
            default:      return name();
        }
    }

    public String toDbValue() {
        return toDisplayName();
    }

    public static AttendanceStatus fromDbValue(String value) {
        for (AttendanceStatus status : values()) {
            if (status.toDisplayName().equals(value)) {
                return status;
            }
        }
        return PRESENT;
    }
}
