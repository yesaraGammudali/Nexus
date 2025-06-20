// controller/DeliveryPersonnelController.java
package controller;

import DAO.DeliveryPersonnelDAO;
import Model.DeliveryPersonnel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * Controller for managing Delivery Personnel.
 * This class handles the business logic for adding, updating, and deleting personnel.
 */
public class DeliveryPersonnelController {

    private DeliveryPersonnelDAO personnelDAO;
    private DefaultTableModel personnelTableModel; // Can be null initially, set by the view

    /**
     * Constructor for DeliveryPersonnelController.
     * @param personnelDAO The DAO for DeliveryPersonnel operations.
     * @param personnelTableModel The table model for displaying personnel data in the UI (can be null if set later).
     */
    public DeliveryPersonnelController(DeliveryPersonnelDAO personnelDAO, DefaultTableModel personnelTableModel) {
        this.personnelDAO = personnelDAO;
        this.personnelTableModel = personnelTableModel;
    }

    /**
     * Sets the table model for this controller. This is useful when the table model
     * is created by the view panel and then passed to the controller.
     * @param personnelTableModel The DefaultTableModel to use.
     */
    public void setTableModel(DefaultTableModel personnelTableModel) {
        this.personnelTableModel = personnelTableModel;
    }

    /**
     * Adds a new delivery personnel record.
     * @param name The name of the personnel.
     * @param contactNumber The contact number of the personnel.
     * @param email The email address of the personnel.
     * @param vehicleDetails Details about the personnel's vehicle.
     * @param availabilityStatus The current availability status (e.g., "Available", "On Duty").
     * @return true if personnel was successfully added, false otherwise.
     */
    public boolean addPersonnel(String name, String contactNumber, String email,
                                String vehicleDetails, String availabilityStatus) {
        String personnelId = "DP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try {
            DeliveryPersonnel personnel = new DeliveryPersonnel(personnelId, name, contactNumber, email,
                    vehicleDetails, availabilityStatus, (String) null); // CurrentRoute is null initially
            boolean success = personnelDAO.addPersonnel(personnel);
            if (success) {
                // No table refresh here; handled by panel's refreshPersonnelTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error adding personnel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing delivery personnel record.
     * @param personnelId The ID of the personnel to update.
     * @param name The new name of the personnel.
     * @param contactNumber The new contact number.
     * @param email The new email.
     * @param vehicleDetails The new vehicle details.
     * @param availabilityStatus The new availability status.
     * @param currentRoute The new current route (can be null).
     * @return true if personnel was successfully updated, false otherwise.
     */
    public boolean updatePersonnel(String personnelId, String name, String contactNumber, String email,
                                   String vehicleDetails, String availabilityStatus, String currentRoute) {
        try {
            DeliveryPersonnel personnel = new DeliveryPersonnel(personnelId, name, contactNumber, email,
                    vehicleDetails, availabilityStatus, (String) currentRoute);
            boolean success = personnelDAO.updatePersonnel(personnel);
            if (success) {
                // No table refresh here; handled by panel's refreshPersonnelTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error updating personnel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing delivery personnel record using a DeliveryPersonnel object.
     * This method extracts the individual fields from the object and calls the primary
     * updatePersonnel method.
     * @param personnel The DeliveryPersonnel object containing updated details.
     * @return true if personnel was successfully updated, false otherwise.
     */
    public boolean updatePersonnel(DeliveryPersonnel personnel) {
        if (personnel == null) {
            System.err.println("Cannot update personnel: provided object is null.");
            return false;
        }
        return updatePersonnel(
                personnel.getPersonnelId(),
                personnel.getName(),
                personnel.getContactNumber(),
                personnel.getEmail(),
                personnel.getVehicleDetails(),
                personnel.getAvailabilityStatus(),
                personnel.getCurrentRoute()
        );
    }

    /**
     * Deletes a delivery personnel record from the system.
     * @param personnelId The ID of the personnel to delete.
     * @return true if personnel was successfully deleted, false otherwise.
     */
    public boolean deletePersonnel(String personnelId) {
        try {
            boolean success = personnelDAO.deletePersonnel(personnelId);
            if (success) {
                // No table refresh here; handled by panel's refreshPersonnelTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error deleting personnel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a DeliveryPersonnel object by its ID.
     * @param personnelId The ID of the personnel to retrieve.
     * @return The DeliveryPersonnel object, or null if not found.
     */
    public DeliveryPersonnel getPersonnelById(String personnelId) {
        try {
            return personnelDAO.getPersonnelById(personnelId);
        } catch (SQLException e) {
            System.err.println("Error retrieving personnel by ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Refreshes the personnel table in the UI by fetching all personnel from the database.
     */
    public void refreshPersonnelTable() {
        if (personnelTableModel == null) { // Defensive check
            System.err.println("PersonnelTableModel is null in controller. Cannot refresh table.");
            return;
        }
        try {
            List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
            // Clear existing data from the table model
            personnelTableModel.setRowCount(0);
            // Add new data
            for (DeliveryPersonnel personnel : personnelList) {
                personnelTableModel.addRow(new Object[]{
                        personnel.getPersonnelId(),
                        personnel.getName(),
                        personnel.getContactNumber(),
                        personnel.getEmail(),
                        personnel.getVehicleDetails(),
                        personnel.getAvailabilityStatus(),
                        personnel.getCurrentRoute()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing personnel table: " + e.getMessage());
        }
    }
}
