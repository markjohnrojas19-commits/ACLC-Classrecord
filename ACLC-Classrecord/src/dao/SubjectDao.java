package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Subject;

public class SubjectDao {

    public boolean add(Subject subject) {
        String sql = "INSERT INTO subjects (subject_code, subject_name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, subject.getSubjectCode());
            statement.setString(2, subject.getSubjectName());
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add subject error: " + e.getMessage());
            return false;
        }
    }

    public List<Subject> getAll() {
        String sql = "SELECT * FROM subjects ORDER BY subject_code";
        List<Subject> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                subjects.add(extractSubject(result));
            }

        } catch (SQLException e) {
            System.out.println("Get all subjects error: " + e.getMessage());
        }

        return subjects;
    }

    public boolean update(Subject subject) {
        String sql = "UPDATE subjects SET subject_code = ?, subject_name = ? WHERE subject_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, subject.getSubjectCode());
            statement.setString(2, subject.getSubjectName());
            statement.setInt(3, subject.getSubjectId());
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Update subject error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Delete subject error: " + e.getMessage());
            return false;
        }
    }

    public List<Subject> search(String keyword) {
        String sql = "SELECT * FROM subjects WHERE subject_code LIKE ? OR subject_name LIKE ? "
                   + "ORDER BY subject_code";
        List<Subject> subjects = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    subjects.add(extractSubject(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Search subjects error: " + e.getMessage());
        }

        return subjects;
    }

    private Subject extractSubject(ResultSet result) throws SQLException {
        return new Subject(
            result.getInt("subject_id"),
            result.getString("subject_code"),
            result.getString("subject_name")
        );
    }
}
