package model;

public class Student {

    private String studentId;
    private String firstname;
    private String lastname;
    private String course;
    private int yearLevel;
    private String section;
    private String gender;

    public Student(String studentId, String firstname, String lastname,
                   String course, int yearLevel, String section, String gender) {
        this.studentId = studentId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.course = course;
        this.yearLevel = yearLevel;
        this.section = section;
        this.gender = gender;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
