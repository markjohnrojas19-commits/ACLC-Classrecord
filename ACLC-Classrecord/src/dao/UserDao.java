package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Role;
import model.User;

public class UserDao {

    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return extractUser(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }

        return null;
    }

    private User extractUser(ResultSet result) throws SQLException {
        int userId = result.getInt("user_id");
        String username = result.getString("username");
        String password = result.getString("password");
        Role role = Role.valueOf(result.getString("role").toUpperCase());

        return new User(userId, username, password, role);
    }
}
