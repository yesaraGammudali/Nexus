// dao/ShipmentDAO.java
package DAO; // Package declaration for the 'dao' directory

import Model.Shipment; // Import the Shipment model class from the 'model' directory
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Shipment entity.
 * This class handles all database operations (CRUD) for Shipment objects.
 * It provides methods to add, retrieve, update, and delete shipment records.
 */
public class ShipmentDAO {

    /**
     * Adds a new shipment record to the database.
     * @param shipment The Shipment object to be added.
     * @return true if the shipment was added successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean addShipment(Shipment shipment) throws SQLException {
        String sql = "INSERT INTO Shipments (shipment_id, sender_name, sender_address, receiver_name, " +
                "receiver_address, package_contents, weight_kg, dimensions_cm, delivery_status, " +
                "current_location, scheduled_delivery_date, estimated_delivery_time, assigned_driver_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection(); // Get connection from DatabaseConnection utility
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipment.getShipmentId());
            pstmt.setString(2, shipment.getSenderName());
            pstmt.setString(3, shipment.getSenderAddress());
            pstmt.setString(4, shipment.getReceiverName());
            pstmt.setString(5, shipment.getReceiverAddress());
            pstmt.setString(6, shipment.getPackageContents());
            pstmt.setBigDecimal(7, shipment.getWeightKg());
            pstmt.setString(8, shipment.getDimensionsCm());
            pstmt.setString(9, shipment.getDeliveryStatus());
            pstmt.setString(10, shipment.getCurrentLocation());
            pstmt.setDate(11, shipment.getScheduledDeliveryDate());
            pstmt.setTime(12, shipment.getEstimatedDeliveryTime());
            pstmt.setString(13, shipment.getAssignedDriverId()); // Can be null

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            // Ensure resources are closed in reverse order of creation
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves a shipment record from the database by its ID.
     * @param shipmentId The unique ID of the shipment to retrieve.
     * @return The Shipment object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public Shipment getShipmentById(String shipmentId) throws SQLException {
        String sql = "SELECT * FROM Shipments WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Shipment shipment = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                shipment = new Shipment(); // Create a new Shipment object
                shipment.setShipmentId(rs.getString("shipment_id"));
                shipment.setSenderName(rs.getString("sender_name"));
                shipment.setSenderAddress(rs.getString("sender_address"));
                shipment.setReceiverName(rs.getString("receiver_name"));
                shipment.setReceiverAddress(rs.getString("receiver_address"));
                shipment.setPackageContents(rs.getString("package_contents"));
                shipment.setWeightKg(rs.getBigDecimal("weight_kg"));
                shipment.setDimensionsCm(rs.getString("dimensions_cm"));
                shipment.setDeliveryStatus(rs.getString("delivery_status"));
                shipment.setCurrentLocation(rs.getString("current_location"));
                shipment.setScheduledDeliveryDate(rs.getDate("scheduled_delivery_date"));
                shipment.setEstimatedDeliveryTime(rs.getTime("estimated_delivery_time"));
                shipment.setAssignedDriverId(rs.getString("assigned_driver_id"));
                shipment.setCreatedAt(rs.getTimestamp("created_at"));
                shipment.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } finally {
            // Ensure resources are closed
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return shipment;
    }

    /**
     * Updates an existing shipment record in the database.
     * @param shipment The Shipment object with updated details.
     * @return true if the shipment was updated successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updateShipment(Shipment shipment) throws SQLException {
        String sql = "UPDATE Shipments SET sender_name = ?, sender_address = ?, receiver_name = ?, " +
                "receiver_address = ?, package_contents = ?, weight_kg = ?, dimensions_cm = ?, " +
                "delivery_status = ?, current_location = ?, scheduled_delivery_date = ?, " +
                "estimated_delivery_time = ?, assigned_driver_id = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipment.getSenderName());
            pstmt.setString(2, shipment.getSenderAddress());
            pstmt.setString(3, shipment.getReceiverName());
            pstmt.setString(4, shipment.getReceiverAddress());
            pstmt.setString(5, shipment.getPackageContents());
            pstmt.setBigDecimal(6, shipment.getWeightKg());
            pstmt.setString(7, shipment.getDimensionsCm());
            pstmt.setString(8, shipment.getDeliveryStatus());
            pstmt.setString(9, shipment.getCurrentLocation());
            pstmt.setDate(10, shipment.getScheduledDeliveryDate());
            pstmt.setTime(11, shipment.getEstimatedDeliveryTime());
            pstmt.setString(12, shipment.getAssignedDriverId());
            pstmt.setString(13, shipment.getShipmentId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Deletes a shipment record from the database by its ID.
     * @param shipmentId The unique ID of the shipment to delete.
     * @return true if the shipment was deleted successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteShipment(String shipmentId) throws SQLException {
        String sql = "DELETE FROM Shipments WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves all shipment records from the database.
     * @return A List of all Shipment objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Shipment> getAllShipments() throws SQLException {
        List<Shipment> shipments = new ArrayList<>();
        String sql = "SELECT * FROM Shipments";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Shipment shipment = new Shipment();
                shipment.setShipmentId(rs.getString("shipment_id"));
                shipment.setSenderName(rs.getString("sender_name"));
                shipment.setSenderAddress(rs.getString("sender_address"));
                shipment.setReceiverName(rs.getString("receiver_name"));
                shipment.setReceiverAddress(rs.getString("receiver_address"));
                shipment.setPackageContents(rs.getString("package_contents"));
                shipment.setWeightKg(rs.getBigDecimal("weight_kg"));
                shipment.setDimensionsCm(rs.getString("dimensions_cm"));
                shipment.setDeliveryStatus(rs.getString("delivery_status"));
                shipment.setCurrentLocation(rs.getString("current_location"));
                shipment.setScheduledDeliveryDate(rs.getDate("scheduled_delivery_date"));
                shipment.setEstimatedDeliveryTime(rs.getTime("estimated_delivery_time"));
                shipment.setAssignedDriverId(rs.getString("assigned_driver_id"));
                shipment.setCreatedAt(rs.getTimestamp("created_at"));
                shipment.setUpdatedAt(rs.getTimestamp("updated_at"));
                shipments.add(shipment);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(stmt);
            DatabaseConnection.closeConnection(conn);
        }
        return shipments;
    }
}
