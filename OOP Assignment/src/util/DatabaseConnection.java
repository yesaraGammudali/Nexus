// util/DatabaseConnection.java
package util; // THIS MUST BE 'package util;'

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Utility class for managing database connections.
 * This class provides a static method to get a database connection
 * and includes basic error handling for connection issues.
 * It is now placed in the 'util' package for better architectural separation.
 */
public class DatabaseConnection {

    // --- Database Configuration ---
    // IMPORTANT: Replace these placeholders with your actual database credentials.
    // For MySQL: "jdbc:mysql://localhost:3306/fasttrack_logistics"
    private static final String DB_URL = "jdbc:mysql://localhost:3306/fasttrack_logistics"; // Your database URL
    private static final String DB_USER = "root"; // Your database username
    private static final String DB_PASSWORD = ""; // Your database password (empty string if no password)

    // --- Constructor ---
    // Private constructor to prevent instantiation, as this is a utility class
    private DatabaseConnection() {
        // No instantiation
    }

    /**
     * Establishes and returns a connection to the database.
     *
     * @return A `Connection` object to the database.
     * @throws SQLException If a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Register the JDBC driver (optional for newer JDBC versions but good practice for MySQL)
            // This line requires the 'mysql-connector-j-x.x.x.jar' to be in your project's classpath.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // System.out.println("Database connection established successfully."); // For debugging
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the caller
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Ensure 'mysql-connector-j-x.x.x.jar' is in your classpath: " + e.getMessage());
            throw new SQLException("JDBC Driver not found", e); // Wrap and re-throw as SQLException
        }
        return connection;
    }

    /**
     * Closes the given Statement object.
     * @param stmt The Statement object to be closed.
     */
    public static void closeConnection(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the given PreparedStatement object.
     * @param pstmt The PreparedStatement object to be closed.
     */
    public static void closeConnection(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the given ResultSet object.
     * @param rs The ResultSet object to be closed.
     */
    public static void closeConnection(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * Closes the given database connection.
     * @param connection The `Connection` object to be closed.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                // System.out.println("Database connection closed."); // For debugging
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
            }
        }
    }
}
