
// controller/ShipmentController.java
package controller;

import DAO.ShipmentDAO;
import Model.Shipment;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing Shipment-related operations.
 * This class handles the business logic for adding, updating, and deleting shipments.
 */
public class ShipmentController {

    private ShipmentDAO shipmentDAO;
    private DefaultTableModel shipmentTableModel; // Can be null initially, set by the view

    /**
     * Constructor for ShipmentController.
     * @param shipmentDAO The DAO for Shipment operations.
     * @param shipmentTableModel The table model for displaying shipment data in the UI (can be null if set later).
     */
    public ShipmentController(ShipmentDAO shipmentDAO, DefaultTableModel shipmentTableModel) {
        this.shipmentDAO = shipmentDAO;
        this.shipmentTableModel = shipmentTableModel;
    }

    /**
     * Sets the table model for this controller. This is useful when the table model
     * is created by the view panel and then passed to the controller.
     * @param shipmentTableModel The DefaultTableModel to use.
     */
    public void setTableModel(DefaultTableModel shipmentTableModel) {
        this.shipmentTableModel = shipmentTableModel;
    }


    /**
     * Adds a new shipment record.
     * @param senderName Name of the sender.
     * @param senderAddress Address of the sender.
     * @param receiverName Name of the receiver.
     * @param receiverAddress Address of the receiver.
     * @param packageContents Description of the package contents.
     * @param weightKg Weight of the package in kilograms.
     * @param dimensionsCm Dimensions of the package in centimeters.
     * @param deliveryStatus Current status of the delivery.
     * @return true if shipment was successfully added, false otherwise.
     */
    public boolean addShipment(String senderName, String senderAddress, String receiverName,
                               String receiverAddress, String packageContents, BigDecimal weightKg,
                               String dimensionsCm, String deliveryStatus) {
        String shipmentId = "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try {
            // FIXED: Explicitly cast null to Date, Time, and String for constructor
            Shipment shipment = new Shipment(shipmentId, senderName, senderAddress, receiverName,
                    receiverAddress, packageContents, weightKg, dimensionsCm,
                    deliveryStatus, "Warehouse", (Date) null, (Time) null, (String) null); // Initial location & null driver/dates
            boolean success = shipmentDAO.addShipment(shipment);
            if (success) {
                // No table refresh here; handled by panel's refreshShipmentTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error adding shipment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing shipment record.
     * @param shipmentId The ID of the shipment to update.
     * @param senderName The new sender name.
     * @param senderAddress The new sender address.
     * @param receiverName The new receiver name.
     * @param receiverAddress The new receiver address.
     * @param packageContents The new package contents.
     * @param weightKg The new weight.
     * @param dimensionsCm The new dimensions.
     * @param deliveryStatus The new delivery status.
     * @param currentLocation The new current location.
     * @param scheduledDeliveryDate The new scheduled delivery date.
     * @param estimatedDeliveryTime The new estimated delivery time.
     * @param assignedDriverId The ID of the new assigned driver.
     * @return true if shipment was successfully updated, false otherwise.
     */
    public boolean updateShipment(String shipmentId, String senderName, String senderAddress,
                                  String receiverName, String receiverAddress, String packageContents,
                                  BigDecimal weightKg, String dimensionsCm, String deliveryStatus,
                                  String currentLocation, Date scheduledDeliveryDate,
                                  Time estimatedDeliveryTime, String assignedDriverId) {
        try {
            // FIXED: Explicitly cast nullable Date, Time, and String for constructor
            Shipment shipment = new Shipment(shipmentId, senderName, senderAddress, receiverName,
                    receiverAddress, packageContents, weightKg, dimensionsCm,
                    deliveryStatus, currentLocation, (Date) scheduledDeliveryDate,
                    (Time) estimatedDeliveryTime, (String) assignedDriverId);
            boolean success = shipmentDAO.updateShipment(shipment);
            if (success) {
                // No table refresh here; handled by panel's refreshShipmentTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error updating shipment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a Shipment object by its ID.
     * @param shipmentId The ID of the shipment to retrieve.
     * @return The Shipment object, or null if not found.
     */
    public Shipment getShipmentById(String shipmentId) {
        try {
            return shipmentDAO.getShipmentById(shipmentId);
        } catch (SQLException e) {
            System.err.println("Error retrieving shipment by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a shipment record from the system.
     * @param shipmentId The ID of the shipment to delete.
     * @return true if shipment was successfully deleted, false otherwise.
     */
    public boolean deleteShipment(String shipmentId) {
        try {
            boolean success = shipmentDAO.deleteShipment(shipmentId);
            if (success) {
                // No table refresh here; handled by panel's refreshShipmentTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error deleting shipment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Refreshes the shipment table in the UI by fetching all shipments from the database.
     */
    public void refreshShipmentTable() {
        if (shipmentTableModel == null) { // Defensive check
            System.err.println("ShipmentTableModel is null in controller. Cannot refresh table.");
            return;
        }
        try {
            List<Shipment> shipments = shipmentDAO.getAllShipments();
            // Clear existing data from the table model
            shipmentTableModel.setRowCount(0);
            // Add new data
            for (Shipment shipment : shipments) {
                shipmentTableModel.addRow(new Object[]{
                        shipment.getShipmentId(),
                        shipment.getSenderName(),
                        shipment.getReceiverName(),
                        shipment.getPackageContents(),
                        shipment.getDeliveryStatus(),
                        shipment.getCurrentLocation(),
                        shipment.getAssignedDriverId(),
                        shipment.getScheduledDeliveryDate(),
                        shipment.getEstimatedDeliveryTime()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing shipment table: " + e.getMessage());
        }
    }

    /**
     * Provides access to the ShipmentDAO instance.
     * This is used by views (like TrackShipmentsPanel) to directly fetch all shipments
     * for custom filtering or display logic within the view.
     * @return The ShipmentDAO instance.
     */
    public ShipmentDAO getShipmentDAO() {
        return this.shipmentDAO;
    }
}
