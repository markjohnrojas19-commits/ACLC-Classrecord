package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;

public class StudentDao {

    public boolean add(Student student) {
        if (existsById(student.getStudentId())) {
            return false;
        }

        String sql = "INSERT INTO students (student_id, firstname, lastname, course, year_level, section, gender) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setStudentParameters(statement, student);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add student error: " + e.getMessage());
            return false;
        }
    }

    public boolean existsById(String studentId) {
        String sql = "SELECT 1 FROM students WHERE student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }

        } catch (SQLException e) {
            System.out.println("Check student exists error: " + e.getMessage());
            return false;
        }
    }

    public List<Student> getAll() {
        String sql = "SELECT * FROM students ORDER BY course, year_level, section, lastname, firstname";
        List<Student> students = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                students.add(extractStudent(result));
            }

        } catch (SQLException e) {
            System.out.println("Get all students error: " + e.getMessage());
        }

        return students;
    }

    public boolean update(Student student) {
        String sql = "UPDATE students SET firstname = ?, lastname = ?, course = ?, "
                   + "year_level = ?, section = ?, gender = ? WHERE student_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getFirstname());
            statement.setString(2, student.getLastname());
            statement.setString(3, student.getCourse());
            statement.setInt(4, student.getYearLevel());
            statement.setString(5, student.getSection());
            statement.setString(6, student.getGender());
            statement.setString(7, student.getStudentId());
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Update student error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String studentId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            deleteRelatedGrades(connection, studentId);
            deleteRelatedAssessments(connection, studentId);
            deleteStudent(connection, studentId);
            return true;

        } catch (SQLException e) {
            System.out.println("Delete student error: " + e.getMessage());
            return false;
        }
    }

    private void deleteRelatedGrades(Connection connection, String studentId) throws SQLException {
        String sql = "DELETE FROM grades WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.executeUpdate();
        }
    }

    private void deleteRelatedAssessments(Connection connection, String studentId) throws SQLException {
        String sql = "DELETE FROM assessments WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.executeUpdate();
        }
    }

    private void deleteStudent(Connection connection, String studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentId);
            statement.executeUpdate();
        }
    }

    public List<String> getAllSections() {
        String sql = "SELECT DISTINCT CONCAT(course, ' ', section) AS course_section "
                   + "FROM students ORDER BY course_section";
        List<String> sections = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                sections.add(result.getString("course_section"));
            }

        } catch (SQLException e) {
            System.out.println("Get all sections error: " + e.getMessage());
        }

        return sections;
    }

    public List<Student> search(String keyword) {
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR firstname LIKE ? OR lastname LIKE ? "
                   + "ORDER BY course, year_level, section, lastname, firstname";
        List<Student> students = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    students.add(extractStudent(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Search students error: " + e.getMessage());
        }

        return students;
    }

    private void setStudentParameters(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getStudentId());
        statement.setString(2, student.getFirstname());
        statement.setString(3, student.getLastname());
        statement.setString(4, student.getCourse());
        statement.setInt(5, student.getYearLevel());
        statement.setString(6, student.getSection());
        statement.setString(7, student.getGender());
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
