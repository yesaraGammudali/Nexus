// dao/DeliveryPersonnelDAO.java
package DAO; // Package declaration for the 'dao' directory

import Model.DeliveryPersonnel; // Import the DeliveryPersonnel model class from the 'model' directory
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the DeliveryPersonnel entity.
 * This class handles all database operations (CRUD) for DeliveryPersonnel objects.
 * It provides methods to add, retrieve, update, and delete personnel records.
 */
public class DeliveryPersonnelDAO {

    /**
     * Adds a new delivery personnel record to the database.
     * @param personnel The DeliveryPersonnel object to be added.
     * @return true if the personnel was added successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean addPersonnel(DeliveryPersonnel personnel) throws SQLException {
        String sql = "INSERT INTO DeliveryPersonnel (personnel_id, name, contact_number, email, " +
                "vehicle_details, availability_status, current_route) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getPersonnelId());
            pstmt.setString(2, personnel.getName());
            pstmt.setString(3, personnel.getContactNumber());
            pstmt.setString(4, personnel.getEmail());
            pstmt.setString(5, personnel.getVehicleDetails());
            pstmt.setString(6, personnel.getAvailabilityStatus());
            pstmt.setString(7, personnel.getCurrentRoute()); // Can be null

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves a delivery personnel record from the database by their ID.
     * @param personnelId The unique ID of the personnel to retrieve.
     * @return The DeliveryPersonnel object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public DeliveryPersonnel getPersonnelById(String personnelId) throws SQLException {
        String sql = "SELECT * FROM DeliveryPersonnel WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DeliveryPersonnel personnel = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                personnel = new DeliveryPersonnel();
                personnel.setPersonnelId(rs.getString("personnel_id"));
                personnel.setName(rs.getString("name"));
                personnel.setContactNumber(rs.getString("contact_number"));
                personnel.setEmail(rs.getString("email"));
                personnel.setVehicleDetails(rs.getString("vehicle_details"));
                personnel.setAvailabilityStatus(rs.getString("availability_status"));
                personnel.setCurrentRoute(rs.getString("current_route"));
                personnel.setCreatedAt(rs.getTimestamp("created_at"));
                personnel.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return personnel;
    }

    /**
     * Updates an existing delivery personnel record in the database.
     * @param personnel The DeliveryPersonnel object with updated details.
     * @return true if the personnel was updated successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updatePersonnel(DeliveryPersonnel personnel) throws SQLException {
        String sql = "UPDATE DeliveryPersonnel SET name = ?, contact_number = ?, email = ?, " +
                "vehicle_details = ?, availability_status = ?, current_route = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnel.getName());
            pstmt.setString(2, personnel.getContactNumber());
            pstmt.setString(3, personnel.getEmail());
            pstmt.setString(4, personnel.getVehicleDetails());
            pstmt.setString(5, personnel.getAvailabilityStatus());
            pstmt.setString(6, personnel.getCurrentRoute());
            pstmt.setString(7, personnel.getPersonnelId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Deletes a delivery personnel record from the database by their ID.
     * @param personnelId The unique ID of the personnel to delete.
     * @return true if the personnel was deleted successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deletePersonnel(String personnelId) throws SQLException {
        String sql = "DELETE FROM DeliveryPersonnel WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves all delivery personnel records from the database.
     * @return A List of all DeliveryPersonnel objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<DeliveryPersonnel> getAllPersonnel() throws SQLException {
        List<DeliveryPersonnel> personnelList = new ArrayList<>();
        String sql = "SELECT * FROM DeliveryPersonnel";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                DeliveryPersonnel personnel = new DeliveryPersonnel();
                personnel.setPersonnelId(rs.getString("personnel_id"));
                personnel.setName(rs.getString("name"));
                personnel.setContactNumber(rs.getString("contact_number"));
                personnel.setEmail(rs.getString("email"));
                personnel.setVehicleDetails(rs.getString("vehicle_details"));
                personnel.setAvailabilityStatus(rs.getString("availability_status"));
                personnel.setCurrentRoute(rs.getString("current_route"));
                personnel.setCreatedAt(rs.getTimestamp("created_at"));
                personnel.setUpdatedAt(rs.getTimestamp("updated_at"));
                personnelList.add(personnel);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(stmt);
            DatabaseConnection.closeConnection(conn);
        }
        return personnelList;
    }
}
