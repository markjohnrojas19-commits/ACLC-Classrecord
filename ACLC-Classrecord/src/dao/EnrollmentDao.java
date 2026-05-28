package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Enrollment;
import model.Student;

public class EnrollmentDao {

    public boolean enroll(String studentId, int subjectId) {
        String sql = "INSERT INTO enrollments (student_id, subject_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, subjectId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Enroll student error: " + e.getMessage());
            return false;
        }
    }

    public boolean unenroll(int enrollmentId) {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, enrollmentId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Unenroll student error: " + e.getMessage());
            return false;
        }
    }

    public boolean unenrollByStudentAndSubject(String studentId, int subjectId) {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND subject_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, subjectId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Unenroll by student and subject error: " + e.getMessage());
            return false;
        }
    }

    public boolean isEnrolled(String studentId, int subjectId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND subject_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, subjectId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Check enrollment error: " + e.getMessage());
        }

        return false;
    }

    public List<Enrollment> getBySubject(int subjectId) {
        String sql = "SELECT * FROM enrollments WHERE subject_id = ? ORDER BY student_id";

        return executeQueryWithSubject(sql, subjectId);
    }

    public List<Student> getStudentsBySubject(int subjectId) {
        String sql = "SELECT s.* FROM students s "
                   + "JOIN enrollments e ON s.student_id = e.student_id "
                   + "WHERE e.subject_id = ? "
                   + "ORDER BY s.lastname, s.firstname";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    students.add(extractStudent(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get students by subject error: " + e.getMessage());
        }

        return students;
    }

    public List<Student> getStudentsBySubjectAndSection(int subjectId, String section) {
        String sql = "SELECT s.* FROM students s "
                   + "JOIN enrollments e ON s.student_id = e.student_id "
                   + "WHERE e.subject_id = ? AND s.section = ? "
                   + "ORDER BY s.lastname, s.firstname";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setString(2, section);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    students.add(extractStudent(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get students by subject and section error: " + e.getMessage());
        }

        return students;
    }

    public List<String> getSectionsBySubject(int subjectId) {
        String sql = "SELECT DISTINCT s.section FROM students s "
                   + "JOIN enrollments e ON s.student_id = e.student_id "
                   + "WHERE e.subject_id = ? "
                   + "ORDER BY s.section";
        List<String> sections = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    sections.add(result.getString("section"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get sections by subject error: " + e.getMessage());
        }

        return sections;
    }

    private List<Enrollment> executeQueryWithSubject(String sql, int subjectId) {
        List<Enrollment> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractEnrollment(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Query enrollments error: " + e.getMessage());
        }

        return results;
    }

    private Enrollment extractEnrollment(ResultSet result) throws SQLException {
        return new Enrollment(
            result.getInt("enrollment_id"),
            result.getString("student_id"),
            result.getInt("subject_id")
        );
    }

    private Student extractStudent(ResultSet result) throws SQLException {
        return new Student(
            result.getString("student_id"),
            result.getString("firstname"),
            result.getString("lastname"),
            result.getString("course"),
            result.getInt("year_level"),
            result.getString("section"),
            result.getString("gender")
        );
    }
}
