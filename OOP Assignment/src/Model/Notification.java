// model/Notification.java
package Model; // Changed package declaration

import java.sql.Timestamp;

/**
 * Represents a notification sent within the FastTrack Logistics system.
 * This POJO corresponds directly to the 'Notifications' table in the database.
 */
public class Notification {

    private String notificationId;
    private String shipmentId; // Can be null if not shipment-specific
    private String personnelId; // Can be null if not personnel-specific
    private String customerContact; // E.g., phone number or email of the customer
    private String messageContent;
    private String notificationType; // E.g., 'SMS', 'Email', 'In-App'
    private String status; // E.g., 'Sent', 'Failed', 'Pending'
    private Timestamp timestamp; // When the notification was created/sent

    /**
     * Default constructor.
     */
    public Notification() {
    }

    /**
     * Parameterized constructor for creating a Notification object.
     *
     * @param notificationId Unique identifier for the notification.
     * @param shipmentId ID of the related shipment (can be null).
     * @param personnelId ID of the related personnel (can be null).
     * @param customerContact Contact detail for the customer (e.g., phone, email).
     * @param messageContent The content of the notification message.
     * @param notificationType Type of notification (e.g., "SMS", "Email").
     * @param status Status of the notification (e.g., "Sent", "Failed").
     * @param timestamp Timestamp when the notification was created/sent.
     */
    public Notification(String notificationId, String shipmentId, String personnelId, String customerContact,
                        String messageContent, String notificationType, String status, Timestamp timestamp) {
        this.notificationId = notificationId;
        this.shipmentId = shipmentId;
        this.personnelId = personnelId;
        this.customerContact = customerContact;
        this.messageContent = messageContent;
        this.notificationType = notificationType;
        this.status = status;
        this.timestamp = timestamp;
    }

    // --- Getters ---
    public String getNotificationId() {
        return notificationId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // --- Setters ---
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a string representation of the Notification object.
     * @return A string containing all notification details.
     */
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", personnelId='" + personnelId + '\'' +
                ", customerContact='" + customerContact + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", notificationType='" + notificationType + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
