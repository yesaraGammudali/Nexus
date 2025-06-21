// model/Shipment.java
package Model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Represents a shipment within the FastTrack Logistics system.
 * This POJO corresponds directly to the 'Shipments' table in the database.
 */
public class Shipment {

    private String shipmentId;
    private String senderName;
    private String senderAddress;
    private String receiverName;
    private String receiverAddress;
    private String packageContents;
    private BigDecimal weightKg;
    private String dimensionsCm;
    private String deliveryStatus; // e.g., 'Pending', 'In Transit', 'Delivered', 'Failed'
    private String currentLocation;
    private Date scheduledDeliveryDate;
    private Time estimatedDeliveryTime;
    private String assignedDriverId; // Personnel ID
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public Shipment() {
    }

    /**
     * Parameterized constructor for creating a Shipment object with all fields.
     *
     * @param shipmentId Unique identifier for the shipment.
     * @param senderName Full name of the sender.
     * @param senderAddress Full address of the sender.
     * @param receiverName Full name of the receiver.
     * @param receiverAddress Full address of the receiver.
     * @param packageContents Description of the contents of the package.
     * @param weightKg Weight of the package in kilograms.
     * @param dimensionsCm Dimensions of the package (e.g., "10x20x30 cm").
     * @param deliveryStatus Current status of the delivery.
     * @param currentLocation Current geographical location of the shipment.
     * @param scheduledDeliveryDate The date the delivery is scheduled for (can be null).
     * @param estimatedDeliveryTime The estimated time of delivery (can be null).
     * @param assignedDriverId The ID of the delivery personnel assigned (can be null).
     * @param createdAt Timestamp when this record was created.
     * @param updatedAt Timestamp when this record was last updated.
     */
    public Shipment(String shipmentId, String senderName, String senderAddress,
                    String receiverName, String receiverAddress, String packageContents,
                    BigDecimal weightKg, String dimensionsCm, String deliveryStatus,
                    String currentLocation, Date scheduledDeliveryDate,
                    Time estimatedDeliveryTime, String assignedDriverId,
                    Timestamp createdAt, Timestamp updatedAt) {
        this.shipmentId = shipmentId;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.packageContents = packageContents;
        this.weightKg = weightKg;
        this.dimensionsCm = dimensionsCm;
        this.deliveryStatus = deliveryStatus;
        this.currentLocation = currentLocation;
        this.scheduledDeliveryDate = scheduledDeliveryDate;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.assignedDriverId = assignedDriverId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // NEW CONSTRUCTOR ADDED: Matches the 13 arguments from ShipmentController's add/update
    /**
     * Parameterized constructor for creating or updating a Shipment object,
     * typically used when createdAt/updatedAt timestamps are handled by the database
     * or are not immediately available.
     *
     * @param shipmentId Unique identifier for the shipment.
     * @param senderName Full name of the sender.
     * @param senderAddress Full address of the sender.
     * @param receiverName Full name of the receiver.
     * @param receiverAddress Full address of the receiver.
     * @param packageContents Description of the contents of the package.
     * @param weightKg Weight of the package in kilograms.
     * @param dimensionsCm Dimensions of the package (e.g., "10x20x30 cm").
     * @param deliveryStatus Current status of the delivery.
     * @param currentLocation Current geographical location of the shipment.
     * @param scheduledDeliveryDate The date the delivery is scheduled for (can be null).
     * @param estimatedDeliveryTime The estimated time of delivery (can be null).
     * @param assignedDriverId The ID of the delivery personnel assigned (can be null).
     */
    public Shipment(String shipmentId, String senderName, String senderAddress,
                    String receiverName, String receiverAddress, String packageContents,
                    BigDecimal weightKg, String dimensionsCm, String deliveryStatus,
                    String currentLocation, Date scheduledDeliveryDate,
                    Time estimatedDeliveryTime, String assignedDriverId) {
        this.shipmentId = shipmentId;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.packageContents = packageContents;
        this.weightKg = weightKg;
        this.dimensionsCm = dimensionsCm;
        this.deliveryStatus = deliveryStatus;
        this.currentLocation = currentLocation;
        this.scheduledDeliveryDate = scheduledDeliveryDate;
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        this.assignedDriverId = assignedDriverId;
        this.createdAt = null; // Set to null, expecting DAO/DB to handle actual timestamp
        this.updatedAt = null; // Set to null, expecting DAO/DB to handle actual timestamp
    }


    // --- Getters ---
    public String getShipmentId() {
        return shipmentId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getPackageContents() {
        return packageContents;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public String getDimensionsCm() {
        return dimensionsCm;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public Date getScheduledDeliveryDate() {
        return scheduledDeliveryDate;
    }

    public Time getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public String getAssignedDriverId() {
        return assignedDriverId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setPackageContents(String packageContents) {
        this.packageContents = packageContents;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public void setDimensionsCm(String dimensionsCm) {
        this.dimensionsCm = dimensionsCm;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setScheduledDeliveryDate(Date scheduledDeliveryDate) {
        this.scheduledDeliveryDate = scheduledDeliveryDate;
    }

    public void setEstimatedDeliveryTime(Time estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public void setAssignedDriverId(String assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Shipment object.
     * @return A string containing all shipment details.
     */
    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId='" + shipmentId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", packageContents='" + packageContents + '\'' +
                ", weightKg=" + weightKg +
                ", dimensionsCm='" + dimensionsCm + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", scheduledDeliveryDate=" + scheduledDeliveryDate +
                ", estimatedDeliveryTime=" + estimatedDeliveryTime +
                ", assignedDriverId='" + assignedDriverId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
