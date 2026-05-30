package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import util.ActiveSemester;
import util.GradeConstants;

public class DashboardDao {

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
        return executePassFailCount(true);
    }

    public int countFailed() {
        return executePassFailCount(false);
    }

    private int executePassFailCount(boolean passed) {
        String comparison = passed ? ">=" : "<";
        String sql = "SELECT COUNT(*) FROM ("
                   + "SELECT student_id, subject_id, "
                   + "COALESCE(AVG(CASE WHEN season = 'Prelim' THEN score END), 0) * ? + "
                   + "COALESCE(AVG(CASE WHEN season = 'Midterm' THEN score END), 0) * ? + "
                   + "COALESCE(AVG(CASE WHEN season = 'Pre-Final' THEN score END), 0) * ? + "
                   + "COALESCE(AVG(CASE WHEN season = 'Final' THEN score END), 0) * ? "
                   + "AS weighted_grade "
                   + "FROM assessments "
                   + "WHERE semester_id = ? "
                   + "GROUP BY student_id, subject_id "
                   + "HAVING weighted_grade " + comparison + " ?"
                   + ") AS result";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, GradeConstants.PRELIM_WEIGHT);
            statement.setDouble(2, GradeConstants.MIDTERM_WEIGHT);
            statement.setDouble(3, GradeConstants.PRE_FINAL_WEIGHT);
            statement.setDouble(4, GradeConstants.FINAL_WEIGHT);
            statement.setInt(5, ActiveSemester.getId());
            statement.setDouble(6, GradeConstants.PASSING_GRADE);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("Dashboard pass/fail count error: " + e.getMessage());
            return -1;
        }
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
