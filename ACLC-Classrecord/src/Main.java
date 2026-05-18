import java.sql.Connection;
import java.sql.SQLException;

import dao.DatabaseConnection;

public class Main {

    public static void main(String[] args) {
        testDatabaseConnection();
    }

    private static void testDatabaseConnection() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }
}
