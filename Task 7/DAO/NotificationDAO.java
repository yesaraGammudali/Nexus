// dao/NotificationDAO.java
package view.DAO; // Package declaration for the 'dao' directory

import view.Model.Notification; // Import the Notification model class from the 'model' directory
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the Notification entity.
 * This class handles all database operations (CRUD) for Notification objects.
 * It provides methods to add, retrieve, update, and delete notification records.
 */
public class NotificationDAO {

    /**
     * Adds a new notification record to the database.
     * @param notification The Notification object to be added.
     * @return true if the notification was added successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean addNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO Notifications (notification_id, shipment_id, personnel_id, " +
                "customer_contact, message_content, notification_type, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, notification.getNotificationId());
            pstmt.setString(2, notification.getShipmentId());   // Can be null
            pstmt.setString(3, notification.getPersonnelId());  // Can be null
            pstmt.setString(4, notification.getCustomerContact());
            pstmt.setString(5, notification.getMessageContent());
            pstmt.setString(6, notification.getNotificationType());
            pstmt.setString(7, notification.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves a notification record from the database by its ID.
     * @param notificationId The unique ID of the notification to retrieve.
     * @return The Notification object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public Notification getNotificationById(String notificationId) throws SQLException {
        String sql = "SELECT * FROM Notifications WHERE notification_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Notification notification = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, notificationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                notification = new Notification();
                notification.setNotificationId(rs.getString("notification_id"));
                notification.setShipmentId(rs.getString("shipment_id"));
                notification.setPersonnelId(rs.getString("personnel_id"));
                notification.setCustomerContact(rs.getString("customer_contact"));
                notification.setMessageContent(rs.getString("message_content"));
                notification.setNotificationType(rs.getString("notification_type"));
                notification.setStatus(rs.getString("status"));
                notification.setTimestamp(rs.getTimestamp("timestamp"));
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return notification;
    }

    /**
     * Retrieves all notifications for a specific shipment.
     * @param shipmentId The ID of the shipment.
     * @return A list of Notification objects related to the shipment.
     * @throws SQLException if a database access error occurs.
     */
    public List<Notification> getNotificationsByShipmentId(String shipmentId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE shipment_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, shipmentId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getString("notification_id"));
                notification.setShipmentId(rs.getString("shipment_id"));
                notification.setPersonnelId(rs.getString("personnel_id"));
                notification.setCustomerContact(rs.getString("customer_contact"));
                notification.setMessageContent(rs.getString("message_content"));
                notification.setNotificationType(rs.getString("notification_type"));
                notification.setStatus(rs.getString("status"));
                notification.setTimestamp(rs.getTimestamp("timestamp"));
                notifications.add(notification);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return notifications;
    }

    /**
     * Retrieves all notifications for a specific delivery personnel.
     * @param personnelId The ID of the delivery personnel.
     * @return A list of Notification objects related to the personnel.
     * @throws SQLException if a database access error occurs.
     */
    public List<Notification> getNotificationsByPersonnelId(String personnelId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications WHERE personnel_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, personnelId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getString("notification_id"));
                notification.setShipmentId(rs.getString("shipment_id"));
                notification.setPersonnelId(rs.getString("personnel_id"));
                notification.setCustomerContact(rs.getString("customer_contact"));
                notification.setMessageContent(rs.getString("message_content"));
                notification.setNotificationType(rs.getString("notification_type"));
                notification.setStatus(rs.getString("status"));
                notification.setTimestamp(rs.getTimestamp("timestamp"));
                notifications.add(notification);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return notifications;
    }

    /**
     * Updates an existing notification record in the database.
     * @param notification The Notification object with updated details.
     * @return true if the notification was updated successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean updateNotification(Notification notification) throws SQLException {
        String sql = "UPDATE Notifications SET shipment_id = ?, personnel_id = ?, customer_contact = ?, " +
                "message_content = ?, notification_type = ?, status = ? WHERE notification_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, notification.getShipmentId());
            pstmt.setString(2, notification.getPersonnelId());
            pstmt.setString(3, notification.getCustomerContact());
            pstmt.setString(4, notification.getMessageContent());
            pstmt.setString(5, notification.getNotificationType());
            pstmt.setString(6, notification.getStatus());
            pstmt.setString(7, notification.getNotificationId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Deletes a notification record from the database by its ID.
     * @param notificationId The unique ID of the notification to delete.
     * @return true if the notification was deleted successfully, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean deleteNotification(String notificationId) throws SQLException {
        String sql = "DELETE FROM Notifications WHERE notification_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, notificationId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves all notification records from the database.
     * @return A List of all Notification objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM Notifications";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Notification notification = new Notification();
                notification.setNotificationId(rs.getString("notification_id"));
                notification.setShipmentId(rs.getString("shipment_id"));
                notification.setPersonnelId(rs.getString("personnel_id"));
                notification.setCustomerContact(rs.getString("customer_contact"));
                notification.setMessageContent(rs.getString("message_content"));
                notification.setNotificationType(rs.getString("notification_type"));
                notification.setStatus(rs.getString("status"));
                notification.setTimestamp(rs.getTimestamp("timestamp"));
                notifications.add(notification);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(stmt);
            DatabaseConnection.closeConnection(conn);
        }
        return notifications;
    }
}
