package model;

public class SubjectStats {

    private String subjectCode;
    private String subjectName;
    private int enrolled;
    private int passed;
    private int failed;

    public SubjectStats(String subjectCode, String subjectName,
                        int enrolled, int passed, int failed) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.enrolled = enrolled;
        this.passed = passed;
        this.failed = failed;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public int getPassed() {
        return passed;
    }

    public int getFailed() {
        return failed;
    }
}
