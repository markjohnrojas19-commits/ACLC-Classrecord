package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Attendance;
import model.AttendanceStatus;

public class AttendanceDao {

    public boolean add(Attendance attendance) {
        String sql = "INSERT INTO attendance (student_id, subject_id, date, status) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            setParameters(statement, attendance);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Add attendance error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Attendance attendance) {
        String sql = "UPDATE attendance SET status = ? "
                   + "WHERE student_id = ? AND subject_id = ? AND date = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, attendance.getStatus().toDbValue());
            statement.setString(2, attendance.getStudentId());
            statement.setInt(3, attendance.getSubjectId());
            statement.setDate(4, Date.valueOf(attendance.getDate()));
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Update attendance error: " + e.getMessage());
            return false;
        }
    }

    public boolean saveOrUpdate(Attendance attendance) {
        if (exists(attendance.getStudentId(), attendance.getSubjectId(), attendance.getDate())) {
            return update(attendance);
        }
        return add(attendance);
    }

    public List<LocalDate> getDatesBySubjectAndSection(int subjectId, String section) {
        String sql = "SELECT DISTINCT a.date FROM attendance a "
                   + "JOIN students s ON a.student_id = s.student_id "
                   + "WHERE a.subject_id = ? AND s.section = ? "
                   + "ORDER BY a.date DESC";
        List<LocalDate> dates = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setString(2, section);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    dates.add(result.getDate("date").toLocalDate());
                }
            }

        } catch (SQLException e) {
            System.out.println("Get dates by subject and section error: " + e.getMessage());
        }

        return dates;
    }

    public List<Attendance> getBySubjectAndDate(int subjectId, LocalDate date) {
        String sql = "SELECT * FROM attendance WHERE subject_id = ? AND date = ? "
                   + "ORDER BY student_id";
        List<Attendance> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setDate(2, Date.valueOf(date));

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractAttendance(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get attendance by subject and date error: " + e.getMessage());
        }

        return results;
    }

    public List<Attendance> getBySubjectSectionAndDateRange(int subjectId,
            String section, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT a.* FROM attendance a "
                   + "JOIN students s ON a.student_id = s.student_id "
                   + "WHERE a.subject_id = ? AND s.section = ? "
                   + "AND a.date BETWEEN ? AND ? "
                   + "ORDER BY a.date, s.lastname, s.firstname";
        List<Attendance> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setString(2, section);
            statement.setDate(3, Date.valueOf(startDate));
            statement.setDate(4, Date.valueOf(endDate));

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractAttendance(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get attendance by date range error: " + e.getMessage());
        }

        return results;
    }

    public List<Attendance> getByStudent(String studentId) {
        String sql = "SELECT * FROM attendance WHERE student_id = ? "
                   + "ORDER BY date DESC, subject_id";
        List<Attendance> results = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    results.add(extractAttendance(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Get attendance by student error: " + e.getMessage());
        }

        return results;
    }

    public int countByStatus(AttendanceStatus status) {
        String sql = "SELECT COUNT(*) FROM attendance WHERE status = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.toDbValue());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Count attendance by status error: " + e.getMessage());
        }

        return -1;
    }

    public int countTodayPresent() {
        String sql = "SELECT COUNT(*) FROM attendance WHERE date = ? AND status = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(LocalDate.now()));
            statement.setString(2, AttendanceStatus.PRESENT.toDbValue());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Count today present error: " + e.getMessage());
        }

        return -1;
    }

    public int countTodayTotal() {
        String sql = "SELECT COUNT(*) FROM attendance WHERE date = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDate(1, Date.valueOf(LocalDate.now()));

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Count today total error: " + e.getMessage());
        }

        return -1;
    }

    private boolean exists(String studentId, int subjectId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM attendance "
                   + "WHERE student_id = ? AND subject_id = ? AND date = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, studentId);
            statement.setInt(2, subjectId);
            statement.setDate(3, Date.valueOf(date));

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Check attendance exists error: " + e.getMessage());
        }

        return false;
    }

    private void setParameters(PreparedStatement statement, Attendance attendance) throws SQLException {
        statement.setString(1, attendance.getStudentId());
        statement.setInt(2, attendance.getSubjectId());
        statement.setDate(3, Date.valueOf(attendance.getDate()));
        statement.setString(4, attendance.getStatus().toDbValue());
    }

    private Attendance extractAttendance(ResultSet result) throws SQLException {
        return new Attendance(
            result.getInt("attendance_id"),
            result.getString("student_id"),
            result.getInt("subject_id"),
            result.getDate("date").toLocalDate(),
            AttendanceStatus.fromDbValue(result.getString("status"))
        );
    }
}
