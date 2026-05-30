package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Semester;

public class SemesterDao {

    public List<Semester> getAll() {
        String sql = "SELECT * FROM semesters ORDER BY school_year DESC, semester DESC";
        List<Semester> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                results.add(extractSemester(result));
            }

        } catch (SQLException e) {
            System.out.println("Get all semesters error: " + e.getMessage());
        }

        return results;
    }

    public int getActiveId() {
        String sql = "SELECT semester_id FROM semesters WHERE is_active = TRUE LIMIT 1";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getInt("semester_id");
            }

        } catch (SQLException e) {
            System.out.println("Get active semester error: " + e.getMessage());
        }

        return 1;
    }

    public boolean add(Semester semester) {
        String sql = "INSERT INTO semesters (school_year, semester, is_active) VALUES (?, ?, FALSE)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, semester.getSchoolYear());
            statement.setInt(2, semester.getSemester());
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add semester error: " + e.getMessage());
            return false;
        }
    }

    public boolean setActive(int semesterId) {
        String clearSql = "UPDATE semesters SET is_active = FALSE";
        String setSql = "UPDATE semesters SET is_active = TRUE WHERE semester_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.prepareStatement(clearSql).executeUpdate();

            PreparedStatement setStatement = connection.prepareStatement(setSql);
            setStatement.setInt(1, semesterId);
            setStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Set active semester error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int semesterId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            deleteRelatedRows(connection, "attendance", semesterId);
            deleteRelatedRows(connection, "assessments", semesterId);
            deleteRelatedRows(connection, "enrollments", semesterId);

            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM semesters WHERE semester_id = ?");
            statement.setInt(1, semesterId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Delete semester error: " + e.getMessage());
            return false;
        }
    }

    private void deleteRelatedRows(Connection connection, String table, int semesterId)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM " + table + " WHERE semester_id = ?");
        statement.setInt(1, semesterId);
        statement.executeUpdate();
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM semesters";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Count semesters error: " + e.getMessage());
        }

        return 0;
    }

    private Semester extractSemester(ResultSet result) throws SQLException {
        return new Semester(
            result.getInt("semester_id"),
            result.getString("school_year"),
            result.getInt("semester")
        );
    }
}
