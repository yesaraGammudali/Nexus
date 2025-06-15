// model/Delivery.java
package view.Model;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Represents a delivery record within the FastTrack Logistics system.
 * This POJO corresponds directly to the 'Deliveries' table in the database.
 */
public class Delivery {

    private String deliveryId;
    private String shipmentId;
    private String personnelId;
    private Date scheduledDeliveryDate;
    private Time estimatedDeliveryTime;
    private Date actualDeliveryDate;
    private Time actualDeliveryTime;
    private String deliveryOutcome; // e.g., 'Successful', 'Failed', 'In Progress'
    private String deliveryNotes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public Delivery() {
    }

    /**
     * Parameterized constructor for creating a Delivery object with all fields.
     *
     * @param deliveryId Unique identifier for the delivery.
     * @param shipmentId ID of the associated shipment.
     * @param personnelId ID of the assigned delivery personnel.
     * @param scheduledDeliveryDate The date the delivery is scheduled for.
     * @param estimatedDeliveryTime The estimated time of delivery.
     * @param actualDeliveryDate The actual date the delivery occurred (can be null).
     * @param actualDeliveryTime The actual time the delivery occurred (can be null).
     * @param deliveryOutcome The outcome of the delivery (e.g., "Successful", "Failed").
     * @param deliveryNotes Any additional notes about the delivery.
     * @param createdAt Timestamp when this record was created.
     * @param updatedAt Timestamp when this record was last updated.
     */
    public Delivery(String deliveryId, String shipmentId, String personnelId,
                    Date scheduledDeliveryDate, Time estimatedDeliveryTime,
                    Date actualDeliveryDate, Time actualDeliveryTime,
                    String deliveryOutcome, String deliveryNotes,
                    Timestamp createdAt, Timestamp updatedAt) {
        this.deliveryId = deliveryId;
        this.shipmentId = shipmentId;
        this.personnelId = personnelId;
        this.scheduledDeliveryDate = scheduledDeliveryDate;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.actualDeliveryDate = actualDeliveryDate;
        this.actualDeliveryTime = actualDeliveryTime;
        this.deliveryOutcome = deliveryOutcome;
        this.deliveryNotes = deliveryNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // NEW CONSTRUCTOR ADDED
    /**
     * Parameterized constructor for creating a Delivery object,
     * typically used for initial scheduling where actual details, outcome, and notes
     * are not yet available, and timestamps are handled by the database.
     *
     * @param deliveryId Unique identifier for the delivery.
     * @param shipmentId ID of the associated shipment.
     * @param personnelId ID of the assigned delivery personnel.
     * @param scheduledDeliveryDate The date the delivery is scheduled for.
     * @param estimatedDeliveryTime The estimated time of delivery.
     * @param deliveryOutcome The outcome of the delivery (can be null initially).
     * @param deliveryNotes Any additional notes about the delivery (can be null initially).
     */
    public Delivery(String deliveryId, String shipmentId, String personnelId,
                    Date scheduledDeliveryDate, Time estimatedDeliveryTime,
                    String deliveryOutcome, String deliveryNotes) {
        this.deliveryId = deliveryId;
        this.shipmentId = shipmentId;
        this.personnelId = personnelId;
        this.scheduledDeliveryDate = scheduledDeliveryDate;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.deliveryOutcome = deliveryOutcome;
        this.deliveryNotes = deliveryNotes;
        this.actualDeliveryDate = null; // Initialize to null
        this.actualDeliveryTime = null; // Initialize to null
        this.createdAt = null; // Expecting DAO/DB to handle timestamps
        this.updatedAt = null; // Expecting DAO/DB to handle timestamps
    }


    // --- Getters ---
    public String getDeliveryId() {
        return deliveryId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public Date getScheduledDeliveryDate() {
        return scheduledDeliveryDate;
    }

    public Time getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public Time getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public String getDeliveryOutcome() {
        return deliveryOutcome;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public void setScheduledDeliveryDate(Date scheduledDeliveryDate) {
        this.scheduledDeliveryDate = scheduledDeliveryDate;
    }

    public void setEstimatedDeliveryTime(Time estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public void setActualDeliveryTime(Time actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public void setDeliveryOutcome(String deliveryOutcome) {
        this.deliveryOutcome = deliveryOutcome;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Delivery object.
     * @return A string containing all delivery details.
     */
    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId='" + deliveryId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", personnelId='" + personnelId + '\'' +
                ", scheduledDeliveryDate=" + scheduledDeliveryDate +
                ", estimatedDeliveryTime=" + estimatedDeliveryTime +
                ", actualDeliveryDate=" + actualDeliveryDate +
                ", actualDeliveryTime=" + actualDeliveryTime +
                ", deliveryOutcome='" + deliveryOutcome + '\'' +
                ", deliveryNotes='" + deliveryNotes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
