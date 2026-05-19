package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDao {

    public int countStudents() {
        return executeCount("SELECT COUNT(*) FROM students");
    }

    public int countSubjects() {
        return executeCount("SELECT COUNT(*) FROM subjects");
    }

    public int countPassed() {
        return executeCount("SELECT COUNT(*) FROM grades WHERE remarks = 'PASSED'");
    }

    public int countFailed() {
        return executeCount("SELECT COUNT(*) FROM grades WHERE remarks = 'FAILED'");
    }

    private int executeCount(String sql) {
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
