// controller/DeliveryController.java
package controller;

import DAO.DeliveryDAO;
import DAO.ShipmentDAO; // Required to update shipment status
import DAO.DeliveryPersonnelDAO; // Required to update personnel status
import Model.Delivery;
import Model.Shipment; // To update shipment status
import Model.DeliveryPersonnel; // To update personnel status

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing Delivery-related operations.
 * This class handles scheduling, updating status, and deleting deliveries.
 */
public class DeliveryController {

    private DeliveryDAO deliveryDAO;
    private ShipmentDAO shipmentDAO; // To update associated shipment status
    private DeliveryPersonnelDAO personnelDAO; // To update associated personnel status
    private DefaultTableModel deliveryTableModel; // Can be null initially, set by the view

    /**
     * Constructor for DeliveryController.
     * @param deliveryDAO The DAO for Delivery operations.
     * @param shipmentDAO The DAO for Shipment operations.
     * @param personnelDAO The DAO for DeliveryPersonnel operations.
     * @param deliveryTableModel The table model for displaying deliveries in the UI (can be null if set later).
     */
    public DeliveryController(DeliveryDAO deliveryDAO, ShipmentDAO shipmentDAO,
                              DeliveryPersonnelDAO personnelDAO, DefaultTableModel deliveryTableModel) {
        this.deliveryDAO = deliveryDAO;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;
        this.deliveryTableModel = deliveryTableModel;
    }

    /**
     * Sets the table model for this controller. This is useful when the table model
     * is created by the view panel and then passed to the controller.
     * @param deliveryTableModel The DefaultTableModel to use.
     */
    public void setTableModel(DefaultTableModel deliveryTableModel) {
        this.deliveryTableModel = deliveryTableModel;
    }

    /**
     * Schedules a new delivery.
     * @param shipmentId The ID of the shipment to deliver.
     * @param personnelId The ID of the personnel assigned.
     * @param scheduledDate The scheduled date for delivery.
     * @param estimatedTime The estimated time for delivery.
     * @return true if delivery was successfully scheduled, false otherwise.
     */
    public boolean scheduleDelivery(String shipmentId, String personnelId, Date scheduledDate, Time estimatedTime) {
        String deliveryId = "DEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try {
            Delivery delivery = new Delivery(deliveryId, shipmentId, personnelId,
                    scheduledDate, estimatedTime, null, null); // Outcome and Notes are null initially
            boolean success = deliveryDAO.addDelivery(delivery);

            if (success) {
                // Update associated Shipment status to 'Scheduled' and assign driver
                Shipment shipment = shipmentDAO.getShipmentById(shipmentId);
                if (shipment != null) {
                    shipment.setDeliveryStatus("Scheduled");
                    shipment.setAssignedDriverId(personnelId);
                    shipment.setScheduledDeliveryDate(scheduledDate);
                    shipment.setEstimatedDeliveryTime(estimatedTime);
                    shipmentDAO.updateShipment(shipment);
                }

                // Update associated Personnel status to 'On Duty' or similar
                DeliveryPersonnel personnel = personnelDAO.getPersonnelById(personnelId);
                if (personnel != null) {
                    personnel.setAvailabilityStatus("On Duty");
                    personnelDAO.updatePersonnel(personnel); // Use overloaded method
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error scheduling delivery: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the status and outcome of an existing delivery.
     * @param deliveryId The ID of the delivery to update.
     * @param actualDate The actual date of delivery.
     * @param actualTime The actual time of delivery.
     * @param deliveryOutcome The outcome of the delivery (e.g., "Successful", "Failed").
     * @param deliveryNotes Any notes regarding the delivery.
     * @return true if delivery status was successfully updated, false otherwise.
     */
    public boolean updateDeliveryStatus(String deliveryId, Date actualDate, Time actualTime,
                                        String deliveryOutcome, String deliveryNotes) {
        try {
            Delivery delivery = deliveryDAO.getDeliveryById(deliveryId);
            if (delivery == null) {
                System.err.println("Delivery with ID " + deliveryId + " not found. Cannot update status.");
                return false;
            }

            delivery.setActualDeliveryDate(actualDate);
            delivery.setActualDeliveryTime(actualTime);
            delivery.setDeliveryOutcome(deliveryOutcome);
            delivery.setDeliveryNotes(deliveryNotes);

            boolean success = deliveryDAO.updateDelivery(delivery);

            if (success) {
                // Update associated Shipment status based on delivery outcome
                Shipment shipment = shipmentDAO.getShipmentById(delivery.getShipmentId());
                if (shipment != null) {
                    if ("Successful".equalsIgnoreCase(deliveryOutcome)) {
                        shipment.setDeliveryStatus("Delivered");
                    } else if ("Failed".equalsIgnoreCase(deliveryOutcome)) {
                        shipment.setDeliveryStatus("Failed");
                    } else {
                        shipment.setDeliveryStatus("In Transit"); // Or other appropriate status if outcome is not final
                    }
                    shipmentDAO.updateShipment(shipment);
                }

                // Update associated Personnel status (e.g., if 'Delivered', personnel might become 'Available')
                DeliveryPersonnel personnel = personnelDAO.getPersonnelById(delivery.getPersonnelId());
                if (personnel != null) {
                    if ("Successful".equalsIgnoreCase(deliveryOutcome) || "Failed".equalsIgnoreCase(deliveryOutcome)) {
                        personnel.setAvailabilityStatus("Available");
                    }
                    personnelDAO.updatePersonnel(personnel); // Use overloaded method
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error updating delivery status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a delivery record from the system.
     * @param deliveryId The ID of the delivery to delete.
     * @return true if delivery was successfully deleted, false otherwise.
     */
    public boolean deleteDelivery(String deliveryId) {
        try {
            boolean success = deliveryDAO.deleteDelivery(deliveryId);
            if (success) {
                // No table refresh here; handled by panel's refreshDeliveryTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error deleting delivery: " + e.getMessage());
            return false;
        }
    }

    /**
     * Refreshes the delivery table in the UI by fetching all deliveries from the database.
     */
    public void refreshDeliveryTable() {
        if (deliveryTableModel == null) { // Defensive check
            System.err.println("DeliveryTableModel is null in controller. Cannot refresh table.");
            return;
        }
        try {
            List<Delivery> deliveries = deliveryDAO.getAllDeliveries();
            // Clear existing data from the table model
            deliveryTableModel.setRowCount(0);
            // Add new data
            for (Delivery delivery : deliveries) {
                deliveryTableModel.addRow(new Object[]{
                        delivery.getDeliveryId(),
                        delivery.getShipmentId(),
                        delivery.getPersonnelId(),
                        delivery.getActualDeliveryDate(),
                        delivery.getActualDeliveryTime(),
                        delivery.getDeliveryOutcome(),
                        delivery.getDeliveryNotes()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing delivery table: " + e.getMessage());
        }
    }
}
