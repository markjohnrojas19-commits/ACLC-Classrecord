package model;

public class Semester {

    private int semesterId;
    private String schoolYear;
    private int semester;

    public Semester(int semesterId, String schoolYear, int semester) {
        this.semesterId = semesterId;
        this.schoolYear = schoolYear;
        this.semester = semester;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String toDisplayName() {
        String ordinal = (semester == 1) ? "1st" : "2nd";
        return schoolYear + " / " + ordinal + " Semester";
    }

    @Override
    public String toString() {
        return toDisplayName();
    }
}
