package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Assessment;
import model.GradingSeason;
import model.ScoreResult;
import model.SubjectStats;
import service.GradeComputer;
import util.ActiveSemester;
import util.GradeConstants;

public class DashboardDao {

    private GradeComputer gradeComputer = new GradeComputer();

    public int countStudents() {
        return executeSimpleCount("SELECT COUNT(*) FROM students");
    }

    public int countSubjects() {
        return executeSimpleCount("SELECT COUNT(*) FROM subjects");
    }

    public int countAssessments() {
        return executeCountWithSemester("SELECT COUNT(*) FROM assessments WHERE semester_id = ?");
    }

    public int countEnrolled() {
        return executeCountWithSemester("SELECT COUNT(*) FROM enrollments WHERE semester_id = ?");
    }

    public int countTodayPresent() {
        String sql = "SELECT COUNT(*) FROM attendance WHERE date = ? AND status = 'Present' AND semester_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setInt(2, ActiveSemester.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("Dashboard today present count error: " + e.getMessage());
            return -1;
        }
    }

    public int countTodayTotal() {
        String sql = "SELECT COUNT(*) FROM attendance WHERE date = ? AND semester_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setInt(2, ActiveSemester.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("Dashboard today total count error: " + e.getMessage());
            return -1;
        }
    }

    public int countTodaySectionsMarked() {
        String sql = "SELECT COUNT(DISTINCT CONCAT(a.subject_id, '|', s.course, ' ', s.section)) "
                   + "FROM attendance a "
                   + "JOIN students s ON a.student_id = s.student_id "
                   + "WHERE a.date = ? AND a.semester_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setInt(2, ActiveSemester.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("Dashboard today sections marked error: " + e.getMessage());
            return -1;
        }
    }

    public int countTotalEnrolledSections() {
        String sql = "SELECT COUNT(DISTINCT CONCAT(e.subject_id, '|', s.course, ' ', s.section)) "
                   + "FROM enrollments e "
                   + "JOIN students s ON e.student_id = s.student_id "
                   + "WHERE e.semester_id = ?";

        return executeCountWithSemester(sql);
    }

    public int countPassed() {
        return countByResult(true);
    }

    public int countFailed() {
        return countByResult(false);
    }

    public List<SubjectStats> getPerSubjectStats() {
        List<Assessment> allAssessments = new AssessmentDao().getAll();
        Map<Integer, String[]> subjectInfo = loadSubjectInfo();
        Map<Integer, Integer> enrolledCounts = loadEnrolledCounts();

        Map<Integer, Map<String, Map<GradingSeason, List<Assessment>>>> bySubject = groupBySubjectStudentSeason(allAssessments);

        List<SubjectStats> results = new ArrayList<>();

        for (Map.Entry<Integer, String[]> entry : subjectInfo.entrySet()) {
            int subjectId = entry.getKey();
            String subjectCode = entry.getValue()[0];
            String subjectName = entry.getValue()[1];
            int enrolled = enrolledCounts.getOrDefault(subjectId, 0);

            int passed = 0;
            int failed = 0;

            Map<String, Map<GradingSeason, List<Assessment>>> students = bySubject.get(subjectId);
            if (students != null) {
                for (Map<GradingSeason, List<Assessment>> seasonMap : students.values()) {
                    ScoreResult result = gradeComputer.computeFinalGrade(seasonMap);
                    if ("PASSED".equals(result.getRemarks())) {
                        passed++;
                    } else if ("FAILED".equals(result.getRemarks())) {
                        failed++;
                    }
                }
            }

            results.add(new SubjectStats(subjectCode, subjectName, enrolled, passed, failed));
        }

        return results;
    }

    private int countByResult(boolean countPassed) {
        List<Assessment> allAssessments = new AssessmentDao().getAll();
        Map<String, Map<GradingSeason, List<Assessment>>> grouped = groupByStudentSubjectSeason(allAssessments);

        int count = 0;
        for (Map<GradingSeason, List<Assessment>> seasonMap : grouped.values()) {
            ScoreResult result = gradeComputer.computeFinalGrade(seasonMap);
            boolean passed = "PASSED".equals(result.getRemarks());
            if (passed == countPassed) {
                count++;
            }
        }
        return count;
    }

    private Map<String, Map<GradingSeason, List<Assessment>>> groupByStudentSubjectSeason(
            List<Assessment> assessments) {
        Map<String, Map<GradingSeason, List<Assessment>>> grouped = new HashMap<>();
        for (Assessment a : assessments) {
            String key = a.getStudentId() + "|" + a.getSubjectId();
            grouped.computeIfAbsent(key, k -> new HashMap<>())
                   .computeIfAbsent(a.getSeason(), k -> new ArrayList<>())
                   .add(a);
        }
        return grouped;
    }

    private Map<Integer, Map<String, Map<GradingSeason, List<Assessment>>>> groupBySubjectStudentSeason(
            List<Assessment> assessments) {
        Map<Integer, Map<String, Map<GradingSeason, List<Assessment>>>> grouped = new HashMap<>();
        for (Assessment a : assessments) {
            grouped.computeIfAbsent(a.getSubjectId(), k -> new HashMap<>())
                   .computeIfAbsent(a.getStudentId(), k -> new HashMap<>())
                   .computeIfAbsent(a.getSeason(), k -> new ArrayList<>())
                   .add(a);
        }
        return grouped;
    }

    private Map<Integer, String[]> loadSubjectInfo() {
        String sql = "SELECT subject_id, subject_code, subject_name FROM subjects ORDER BY subject_code";
        Map<Integer, String[]> map = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                map.put(resultSet.getInt("subject_id"),
                    new String[]{resultSet.getString("subject_code"),
                                 resultSet.getString("subject_name")});
            }

        } catch (SQLException e) {
            System.out.println("Dashboard subject info error: " + e.getMessage());
        }

        return map;
    }

    private Map<Integer, Integer> loadEnrolledCounts() {
        String sql = "SELECT subject_id, COUNT(DISTINCT student_id) AS enrolled "
                   + "FROM enrollments WHERE semester_id = ? GROUP BY subject_id";
        Map<Integer, Integer> map = new HashMap<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ActiveSemester.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    map.put(resultSet.getInt("subject_id"), resultSet.getInt("enrolled"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Dashboard enrolled counts error: " + e.getMessage());
        }

        return map;
    }

    private int executeCountWithSemester(String sql) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ActiveSemester.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("Dashboard count error: " + e.getMessage());
            return -1;
        }
    }

    private int executeSimpleCount(String sql) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            System.out.println("Dashboard count error: " + e.getMessage());
            return -1;
        }
    }
}
