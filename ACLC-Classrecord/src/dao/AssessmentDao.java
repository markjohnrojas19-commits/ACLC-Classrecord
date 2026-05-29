package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Assessment;
import model.GradingSeason;

public class AssessmentDao {

    public boolean add(Assessment assessment) {
        String sql = "INSERT INTO assessments (student_id, subject_id, season, assessment_name, score) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, assessment);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add assessment error: " + e.getMessage());
            return false;
        }
    }

    public List<Assessment> getAll() {
        String sql = "SELECT * FROM assessments ORDER BY student_id, subject_id, season, assessment_name";
        return executeQuery(sql);
    }

    public List<Assessment> getBySeason(GradingSeason season) {
        String sql = "SELECT * FROM assessments WHERE season = ? "
                   + "ORDER BY student_id, subject_id, assessment_name";
        List<Assessment> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, season.toDbValue());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractAssessment(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get by season error: " + e.getMessage());
        }

        return results;
    }

    public List<Assessment> getByStudent(String studentId) {
        String sql = "SELECT * FROM assessments WHERE student_id = ? "
                   + "ORDER BY subject_id, season, assessment_name";
        List<Assessment> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractAssessment(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get by student error: " + e.getMessage());
        }

        return results;
    }

    public boolean saveOrUpdate(Assessment assessment) {
        String checkSql = "SELECT assessment_id FROM assessments "
                        + "WHERE student_id = ? AND subject_id = ? AND season = ? AND assessment_name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement check = connection.prepareStatement(checkSql)) {

            check.setString(1, assessment.getStudentId());
            check.setInt(2, assessment.getSubjectId());
            check.setString(3, assessment.getSeason().toDbValue());
            check.setString(4, assessment.getAssessmentName());

            try (ResultSet result = check.executeQuery()) {
                if (result.next()) {
                    assessment.setAssessmentId(result.getInt("assessment_id"));
                    return update(assessment);
                }
                return add(assessment);
            }

        } catch (SQLException e) {
            System.out.println("Save or update assessment error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Assessment assessment) {
        String sql = "UPDATE assessments SET student_id = ?, subject_id = ?, season = ?, "
                   + "assessment_name = ?, score = ? WHERE assessment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, assessment);
            statement.setInt(6, assessment.getAssessmentId());
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Update assessment error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int assessmentId) {
        String sql = "DELETE FROM assessments WHERE assessment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, assessmentId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Delete assessment error: " + e.getMessage());
            return false;
        }
    }

    public List<Assessment> search(String keyword) {
        String sql = "SELECT * FROM assessments WHERE student_id LIKE ? OR assessment_name LIKE ? "
                   + "ORDER BY student_id, subject_id, season, assessment_name";
        List<Assessment> results = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);
            statement.setString(2, pattern);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractAssessment(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Search assessments error: " + e.getMessage());
        }

        return results;
    }

    private List<Assessment> executeQuery(String sql) {
        List<Assessment> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                results.add(extractAssessment(result));
            }

        } catch (SQLException e) {
            System.out.println("Query assessments error: " + e.getMessage());
        }

        return results;
    }

    private void setParameters(PreparedStatement statement, Assessment assessment) throws SQLException {
        statement.setString(1, assessment.getStudentId());
        statement.setInt(2, assessment.getSubjectId());
        statement.setString(3, assessment.getSeason().toDbValue());
        statement.setString(4, assessment.getAssessmentName());
        statement.setDouble(5, assessment.getScore());
    }

    private Assessment extractAssessment(ResultSet result) throws SQLException {
        return new Assessment(
            result.getInt("assessment_id"),
            result.getString("student_id"),
            result.getInt("subject_id"),
            GradingSeason.fromDbValue(result.getString("season")),
            result.getString("assessment_name"),
            result.getDouble("score")
        );
    }
}
