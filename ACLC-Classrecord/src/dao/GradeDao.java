package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Grade;
import model.GradeRecord;
import model.ScoreResult;

public class GradeDao {

    public boolean add(Grade grade, ScoreResult scoreResult) {
        String sql = "INSERT INTO grades (student_id, subject_id, quiz, assignment, exam, final_grade, remarks) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setGradeParameters(statement, grade, scoreResult);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add grade error: " + e.getMessage());
            return false;
        }
    }

    public List<GradeRecord> getAll() {
        String sql = "SELECT * FROM grades ORDER BY student_id, subject_id";
        List<GradeRecord> records = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                records.add(extractGradeRecord(result));
            }

        } catch (SQLException e) {
            System.out.println("Get all grades error: " + e.getMessage());
        }

        return records;
    }

    public boolean update(Grade grade, ScoreResult scoreResult) {
        String sql = "UPDATE grades SET student_id = ?, subject_id = ?, quiz = ?, "
                   + "assignment = ?, exam = ?, final_grade = ?, remarks = ? WHERE grade_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setGradeParameters(statement, grade, scoreResult);
            statement.setInt(8, grade.getGradeId());
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Update grade error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, gradeId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Delete grade error: " + e.getMessage());
            return false;
        }
    }

    public List<GradeRecord> search(String keyword) {
        String sql = "SELECT * FROM grades WHERE student_id LIKE ? ORDER BY student_id, subject_id";
        List<GradeRecord> records = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, pattern);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    records.add(extractGradeRecord(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Search grades error: " + e.getMessage());
        }

        return records;
    }

    private void setGradeParameters(PreparedStatement statement, Grade grade,
                                    ScoreResult scoreResult) throws SQLException {
        statement.setString(1, grade.getStudentId());
        statement.setInt(2, grade.getSubjectId());
        statement.setDouble(3, grade.getQuiz());
        statement.setDouble(4, grade.getAssignment());
        statement.setDouble(5, grade.getExam());
        statement.setDouble(6, scoreResult.getFinalGrade());
        statement.setString(7, scoreResult.getRemarks());
    }

    private GradeRecord extractGradeRecord(ResultSet result) throws SQLException {
        Grade grade = new Grade(
            result.getInt("grade_id"),
            result.getString("student_id"),
            result.getInt("subject_id"),
            result.getDouble("quiz"),
            result.getDouble("assignment"),
            result.getDouble("exam")
        );

        ScoreResult scoreResult = new ScoreResult(
            result.getDouble("final_grade"),
            result.getString("remarks")
        );

        return new GradeRecord(grade, scoreResult);
    }
}
