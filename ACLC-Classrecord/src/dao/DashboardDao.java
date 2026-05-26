package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.GradeConstants;

public class DashboardDao {

    public int countStudents() {
        return executeSimpleCount("SELECT COUNT(*) FROM students");
    }

    public int countSubjects() {
        return executeSimpleCount("SELECT COUNT(*) FROM subjects");
    }

    public int countAssessments() {
        return executeSimpleCount("SELECT COUNT(*) FROM assessments");
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
                   + "SELECT student_id, subject_id "
                   + "FROM assessments "
                   + "GROUP BY student_id, subject_id "
                   + "HAVING AVG(score) " + comparison + " ?"
                   + ") AS result";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, GradeConstants.PASSING_GRADE);

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
