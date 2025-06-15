// dao/DeliveryDAO.java
package view.DAO; // Package declaration for the 'dao' directory

import view.Model.Delivery; // Import the Delivery model class from the 'model' directory
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Delivery entity.
 * This class handles all database operations (CRUD) for Delivery objects.
 * It provides methods to add, retrieve, update, and delete delivery records.
 */
public class DeliveryDAO {

    /**
     * Adds a new delivery record to the database.
     * @param delivery The Delivery object to be added.
     * @return true if the delivery was added successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean addDelivery(Delivery delivery) throws SQLException {
        String sql = "INSERT INTO Deliveries (delivery_id, shipment_id, personnel_id, " +
                "actual_delivery_date, actual_delivery_time, delivery_outcome, delivery_notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, delivery.getDeliveryId());
            pstmt.setString(2, delivery.getShipmentId());
            pstmt.setString(3, delivery.getPersonnelId());
            pstmt.setDate(4, delivery.getActualDeliveryDate());
            pstmt.setTime(5, delivery.getActualDeliveryTime());
            pstmt.setString(6, delivery.getDeliveryOutcome());
            pstmt.setString(7, delivery.getDeliveryNotes());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves a delivery record from the database by its ID.
     * @param deliveryId The unique ID of the delivery to retrieve.
     * @return The Delivery object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public Delivery getDeliveryById(String deliveryId) throws SQLException {
        String sql = "SELECT * FROM Deliveries WHERE delivery_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Delivery delivery = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deliveryId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                delivery = new Delivery();
                delivery.setDeliveryId(rs.getString("delivery_id"));
                delivery.setShipmentId(rs.getString("shipment_id"));
                delivery.setPersonnelId(rs.getString("personnel_id"));
                delivery.setActualDeliveryDate(rs.getDate("actual_delivery_date"));
                delivery.setActualDeliveryTime(rs.getTime("actual_delivery_time"));
                delivery.setDeliveryOutcome(rs.getString("delivery_outcome"));
                delivery.setDeliveryNotes(rs.getString("delivery_notes"));
                delivery.setCreatedAt(rs.getTimestamp("created_at"));
                delivery.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return delivery;
    }

    /**
     * Retrieves all delivery records for a specific shipment.
     * @param shipmentId The ID of the shipment.
     * @return A list of Delivery objects related to the shipment.
     * @throws SQLException if a database access error occurs.
     */
    public List<Delivery> getDeliveriesByShipmentId(String shipmentId) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Deliveries WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Delivery delivery = new Delivery();
                delivery.setDeliveryId(rs.getString("delivery_id"));
                delivery.setShipmentId(rs.getString("shipment_id"));
                delivery.setPersonnelId(rs.getString("personnel_id"));
                delivery.setActualDeliveryDate(rs.getDate("actual_delivery_date"));
                delivery.setActualDeliveryTime(rs.getTime("actual_delivery_time"));
                delivery.setDeliveryOutcome(rs.getString("delivery_outcome"));
                delivery.setDeliveryNotes(rs.getString("delivery_notes"));
                delivery.setCreatedAt(rs.getTimestamp("created_at"));
                delivery.setUpdatedAt(rs.getTimestamp("updated_at"));
                deliveries.add(delivery);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return deliveries;
    }

    /**
     * Retrieves all delivery records for a specific delivery personnel.
     * @param personnelId The ID of the delivery personnel.
     * @return A list of Delivery objects related to the personnel.
     * @throws SQLException if a database access error occurs.
     */
    public List<Delivery> getDeliveriesByPersonnelId(String personnelId) throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Deliveries WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Delivery delivery = new Delivery();
                delivery.setDeliveryId(rs.getString("delivery_id"));
                delivery.setShipmentId(rs.getString("shipment_id"));
                delivery.setPersonnelId(rs.getString("personnel_id"));
                delivery.setActualDeliveryDate(rs.getDate("actual_delivery_date"));
                delivery.setActualDeliveryTime(rs.getTime("actual_delivery_time"));
                delivery.setDeliveryOutcome(rs.getString("delivery_outcome"));
                delivery.setDeliveryNotes(rs.getString("delivery_notes"));
                delivery.setCreatedAt(rs.getTimestamp("created_at"));
                delivery.setUpdatedAt(rs.getTimestamp("updated_at"));
                deliveries.add(delivery);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return deliveries;
    }

    /**
     * Updates an existing delivery record in the database.
     * @param delivery The Delivery object with updated details.
     * @return true if the delivery was updated successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updateDelivery(Delivery delivery) throws SQLException {
        String sql = "UPDATE Deliveries SET shipment_id = ?, personnel_id = ?, actual_delivery_date = ?, " +
                "actual_delivery_time = ?, delivery_outcome = ?, delivery_notes = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE delivery_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, delivery.getShipmentId());
            pstmt.setString(2, delivery.getPersonnelId());
            pstmt.setDate(3, delivery.getActualDeliveryDate());
            pstmt.setTime(4, delivery.getActualDeliveryTime());
            pstmt.setString(5, delivery.getDeliveryOutcome());
            pstmt.setString(6, delivery.getDeliveryNotes());
            pstmt.setString(7, delivery.getDeliveryId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Deletes a delivery record from the database by its ID.
     * @param deliveryId The unique ID of the delivery to delete.
     * @return true if the delivery was deleted successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteDelivery(String deliveryId) throws SQLException {
        String sql = "DELETE FROM Deliveries WHERE delivery_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deliveryId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves all delivery records from the database.
     * @return A List of all Delivery objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Delivery> getAllDeliveries() throws SQLException {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Deliveries";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Delivery delivery = new Delivery();
                delivery.setDeliveryId(rs.getString("delivery_id"));
                delivery.setShipmentId(rs.getString("shipment_id"));
                delivery.setPersonnelId(rs.getString("personnel_id"));
                delivery.setActualDeliveryDate(rs.getDate("actual_delivery_date"));
                delivery.setActualDeliveryTime(rs.getTime("actual_delivery_time"));
                delivery.setDeliveryOutcome(rs.getString("delivery_outcome"));
                delivery.setDeliveryNotes(rs.getString("delivery_notes"));
                delivery.setCreatedAt(rs.getTimestamp("created_at"));
                delivery.setUpdatedAt(rs.getTimestamp("updated_at"));
                deliveries.add(delivery);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(stmt);
            DatabaseConnection.closeConnection(conn);
        }
        return deliveries;
    }
}
