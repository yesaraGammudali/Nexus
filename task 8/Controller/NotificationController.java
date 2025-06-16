// controller/NotificationController.java
package controller;

import DAO.NotificationDAO;
import DAO.ShipmentDAO;
import DAO.DeliveryPersonnelDAO;
import Model.Notification;
import Model.Shipment;
import Model.DeliveryPersonnel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing Notification-related operations.
 * This class handles sending notifications to customers and delivery personnel.
 */
public class NotificationController {

    private NotificationDAO notificationDAO;
    private ShipmentDAO shipmentDAO;
    private DeliveryPersonnelDAO personnelDAO;
    private DefaultTableModel notificationTableModel; // Can be null initially, set by the view

    /**
     * Constructor for NotificationController.
     * @param notificationDAO The DAO for Notification operations.
     * @param shipmentDAO The DAO for Shipment operations (to retrieve customer info).
     * @param personnelDAO The DAO for DeliveryPersonnel operations (to retrieve personnel info).
     * @param notificationTableModel The table model for displaying notifications in the UI (can be null if set later).
     */
    public NotificationController(NotificationDAO notificationDAO, ShipmentDAO shipmentDAO,
                                  DeliveryPersonnelDAO personnelDAO, DefaultTableModel notificationTableModel) {
        this.notificationDAO = notificationDAO;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;
        this.notificationTableModel = notificationTableModel;
    }

    /**
     * Sets the table model for this controller. This is useful when the table model
     * is created by the view panel and then passed to the controller.
     * @param notificationTableModel The DefaultTableModel to use.
     */
    public void setTableModel(DefaultTableModel notificationTableModel) {
        this.notificationTableModel = notificationTableModel;
    }

    /**
     * Sends a notification to a customer regarding a specific shipment.
     * @param shipmentId The ID of the shipment.
     * @param messageContent The content of the message.
     * @param notificationType The type of notification (e.g., "SMS", "Email").
     * @return true if the notification was successfully sent (recorded), false otherwise.
     */
    public boolean sendCustomerNotification(String shipmentId, String messageContent, String notificationType) {
        String notificationId = "NOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String customerContact = null;

        try {
            Shipment shipment = shipmentDAO.getShipmentById(shipmentId);
            if (shipment != null) {
                customerContact = shipment.getReceiverName() + " (via shipment " + shipmentId + ")";
            } else {
                System.err.println("Shipment with ID " + shipmentId + " not found. Cannot send customer notification.");
                return false;
            }

            Notification notification = new Notification();
            notification.setNotificationId(notificationId);
            notification.setShipmentId(shipmentId);
            notification.setPersonnelId(null); // No personnel for customer notification
            notification.setCustomerContact(customerContact);
            notification.setMessageContent(messageContent);
            notification.setNotificationType(notificationType);
            notification.setStatus("Sent");

            boolean success = notificationDAO.addNotification(notification);
            if (success) {
                // No table refresh here; handled by panel's refreshNotificationTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error sending customer notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends a notification to a delivery personnel.
     * @param personnelId The ID of the personnel.
     * @param messageContent The content of the message.
     * @param notificationType The type of notification (e.g., "SMS", "Email").
     * @return true if the notification was successfully sent (recorded), false otherwise.
     */
    public boolean sendPersonnelNotification(String personnelId, String messageContent, String notificationType) {
        String notificationId = "NOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String personnelContact = null;

        try {
            DeliveryPersonnel personnel = personnelDAO.getPersonnelById(personnelId);
            if (personnel != null) {
                personnelContact = personnel.getName() + " (" + personnel.getContactNumber() + ")";
            } else {
                System.err.println("Personnel with ID " + personnelId + " not found. Cannot send personnel notification.");
                return false;
            }

            Notification notification = new Notification();
            notification.setNotificationId(notificationId);
            notification.setShipmentId(null); // No shipment for personnel notification directly
            notification.setPersonnelId(personnelId);
            notification.setCustomerContact(personnelContact); // Re-use contact field for personnel contact
            notification.setMessageContent(messageContent);
            notification.setNotificationType(notificationType);
            notification.setStatus("Sent");

            boolean success = notificationDAO.addNotification(notification);
            if (success) {
                // No table refresh here; handled by panel's refreshNotificationTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error sending personnel notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a notification record from the system.
     * @param notificationId The ID of the notification to delete.
     * @return true if the notification was successfully deleted, false otherwise.
     */
    public boolean deleteNotification(String notificationId) {
        try {
            boolean success = notificationDAO.deleteNotification(notificationId);
            if (success) {
                // No table refresh here; handled by panel's refreshNotificationTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error deleting notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Refreshes the notification table in the UI by fetching all notifications from the database.
     * This method is now called by the panels, and they filter the display.
     */
    public void refreshNotificationTable() {
        if (notificationTableModel == null) { // Defensive check
            System.err.println("NotificationTableModel is null in controller. Cannot refresh table.");
            return;
        }
        try {
            List<Notification> notifications = notificationDAO.getAllNotifications();
            // Clear existing data from the table model
            notificationTableModel.setRowCount(0);
            // Add new data (Note: panels now filter this based on their type)
            // This method in controller is meant to provide ALL notifications,
            // the panels then filter and add to their specific table models.
            for (Notification notification : notifications) {
                notificationTableModel.addRow(new Object[]{
                        notification.getNotificationId(),
                        notification.getShipmentId(),
                        notification.getPersonnelId(),
                        notification.getCustomerContact(),
                        notification.getMessageContent(),
                        notification.getNotificationType(),
                        notification.getStatus(),
                        notification.getTimestamp()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing notification table: " + e.getMessage());
        }
    }

    /**
     * Provides access to the NotificationDAO instance.
     * This is used by the Notification panels to directly fetch all notifications
     * for custom filtering logic within the view.
     * @return The NotificationDAO instance.
     */
    public NotificationDAO getNotificationDAO() {
        return this.notificationDAO;
    }
}
