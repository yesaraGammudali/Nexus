// view/AssignDriversPanel.java
package view.View;

import controller.ShipmentController;
import view.Controller.DeliveryPersonnelController;
import Model.Shipment;
import Model.DeliveryPersonnel;
import DAO.ShipmentDAO;
import DAO.DeliveryPersonnelDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.List;

/**
 * JPanel for assigning drivers (delivery personnel) to existing shipments (Task: "Assign Drivers").
 * This panel allows updating an existing shipment to assign a driver and set basic delivery details.
 */
public class AssignDriversPanel extends JPanel {

    private ShipmentController shipmentController;
    private DeliveryPersonnelController personnelController;
    private ShipmentDAO shipmentDAO; // To populate shipment combo box
    private DeliveryPersonnelDAO personnelDAO; // To populate personnel combo box

    // UI Components for input
    private JComboBox<String> shipmentIdComboBox;
    private JComboBox<String> assignedDriverIdComboBox;
    private JTextField scheduledDateField;
    private JTextField estimatedTimeField;
    private JTextField deliveryStatusField;
    private JTextField currentLocationField;

    /**
     * Constructor for AssignDriversPanel.
     * @param shipmentController The controller for shipment operations.
     * @param personnelController The controller for personnel operations (to get available drivers).
     * @param shipmentDAO The DAO for shipment data.
     * @param personnelDAO The DAO for personnel data.
     */
    public AssignDriversPanel(ShipmentController shipmentController, DeliveryPersonnelController personnelController,
                              ShipmentDAO shipmentDAO, DeliveryPersonnelDAO personnelDAO) {
        this.shipmentController = shipmentController;
        this.personnelController = personnelController;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;
        // No table model here as this panel is primarily for interaction, not displaying a full table.

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshComboBoxes(); // Initial load
    }

    /**
     * Initializes the UI components for the Assign Drivers panel.
     */
    private void initComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Assign Driver to Shipment"));

        shipmentIdComboBox = new JComboBox<>();
        assignedDriverIdComboBox = new JComboBox<>();
        scheduledDateField = new JTextField(20);
        estimatedTimeField = new JTextField(20);
        deliveryStatusField = new JTextField(20);
        deliveryStatusField.setEditable(false);
        currentLocationField = new JTextField(20);
        currentLocationField.setEditable(false);

        inputPanel.add(new JLabel("Select Shipment:")); inputPanel.add(shipmentIdComboBox);
        inputPanel.add(new JLabel("Assign Driver:")); inputPanel.add(assignedDriverIdComboBox);
        inputPanel.add(new JLabel("Scheduled Date (YYYY-MM-DD):")); inputPanel.add(scheduledDateField);
        inputPanel.add(new JLabel("Estimated Time (HH:MM:SS):")); inputPanel.add(estimatedTimeField);
        inputPanel.add(new JLabel("Delivery Status:")); inputPanel.add(deliveryStatusField);
        inputPanel.add(new JLabel("Current Location:")); inputPanel.add(currentLocationField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton assignButton = new JButton("Assign Driver");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh Dropdowns");

        buttonPanel.add(assignButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        assignButton.addActionListener(e -> assignDriverAction());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> refreshComboBoxes());

        // Listener for Shipment selection to pre-populate fields (if shipment has existing assignment)
        shipmentIdComboBox.addActionListener(e -> {
            String selectedShipmentIdItem = (String) shipmentIdComboBox.getSelectedItem();
            String selectedShipmentId = extractIdFromComboBoxItem(selectedShipmentIdItem);

            if (selectedShipmentId != null && !selectedShipmentId.isEmpty()) {
                Shipment shipment = shipmentController.getShipmentById(selectedShipmentId);
                if (shipment != null) {
                    scheduledDateField.setText(shipment.getScheduledDeliveryDate() != null ? shipment.getScheduledDeliveryDate().toString() : "");
                    estimatedTimeField.setText(shipment.getEstimatedDeliveryTime() != null ? shipment.getEstimatedDeliveryTime().toString() : "");
                    deliveryStatusField.setText(shipment.getDeliveryStatus());
                    currentLocationField.setText(shipment.getCurrentLocation());
                    if (shipment.getAssignedDriverId() != null && !shipment.getAssignedDriverId().isEmpty()) {
                        setComboBoxSelectionByPrefix(assignedDriverIdComboBox, shipment.getAssignedDriverId());
                    } else {
                        assignedDriverIdComboBox.setSelectedIndex(-1);
                    }
                }
            } else {
                clearFields(); // Clear if no shipment is selected
            }
        });
    }

    /**
     * Handles the action for assigning a driver to a shipment.
     */
    private void assignDriverAction() {
        String selectedShipmentIdItem = (String) shipmentIdComboBox.getSelectedItem();
        String selectedDriverIdItem = (String) assignedDriverIdComboBox.getSelectedItem();

        String selectedShipmentId = extractIdFromComboBoxItem(selectedShipmentIdItem);
        String selectedDriverId = extractIdFromComboBoxItem(selectedDriverIdItem);

        if (selectedShipmentId == null || selectedShipmentId.isEmpty() ||
                selectedDriverId == null || selectedDriverId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select both a Shipment and a Driver.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Date scheduledDate = null;
            Time estimatedTime = null;
            if (!scheduledDateField.getText().isEmpty()) {
                scheduledDate = Date.valueOf(scheduledDateField.getText());
            }
            if (!estimatedTimeField.getText().isEmpty()) {
                estimatedTime = Time.valueOf(estimatedTimeField.getText());
            }

            // Retrieve the current shipment to update
            Shipment shipmentToUpdate = shipmentController.getShipmentById(selectedShipmentId);
            if (shipmentToUpdate == null) {
                JOptionPane.showMessageDialog(this, "Selected Shipment not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update shipment details
            shipmentToUpdate.setAssignedDriverId(selectedDriverId);
            shipmentToUpdate.setScheduledDeliveryDate(scheduledDate);
            shipmentToUpdate.setEstimatedDeliveryTime(estimatedTime);
            // Assuming "In Transit" or "Assigned" status upon driver assignment
            if (!"Delivered".equalsIgnoreCase(shipmentToUpdate.getDeliveryStatus()) &&
                    !"Failed".equalsIgnoreCase(shipmentToUpdate.getDeliveryStatus())) {
                shipmentToUpdate.setDeliveryStatus("Assigned");
                shipmentToUpdate.setCurrentLocation("With Driver: " + selectedDriverId);
            }

            boolean success = shipmentController.updateShipment(
                    shipmentToUpdate.getShipmentId(),
                    shipmentToUpdate.getSenderName(),
                    shipmentToUpdate.getSenderAddress(),
                    shipmentToUpdate.getReceiverName(),
                    shipmentToUpdate.getReceiverAddress(),
                    shipmentToUpdate.getPackageContents(),
                    shipmentToUpdate.getWeightKg(),
                    shipmentToUpdate.getDimensionsCm(),
                    shipmentToUpdate.getDeliveryStatus(),
                    shipmentToUpdate.getCurrentLocation(),
                    shipmentToUpdate.getScheduledDeliveryDate(),
                    shipmentToUpdate.getEstimatedDeliveryTime(),
                    shipmentToUpdate.getAssignedDriverId()
            );

            if (success) {
                // Optionally update personnel status (e.g., from 'Available' to 'On Duty')
                DeliveryPersonnel assignedPersonnel = personnelController.getPersonnelById(selectedDriverId);
                if (assignedPersonnel != null) {
                    assignedPersonnel.setAvailabilityStatus("On Duty");
                    personnelController.updatePersonnel(assignedPersonnel); // This uses the overloaded updatePersonnel(Personnel obj)
                }

                JOptionPane.showMessageDialog(this, "Driver assigned successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign driver.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. Use YYYY-MM-DD and HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all input fields on the panel.
     */
    private void clearFields() {
        shipmentIdComboBox.setSelectedIndex(-1);
        assignedDriverIdComboBox.setSelectedIndex(-1);
        scheduledDateField.setText("");
        estimatedTimeField.setText("");
        deliveryStatusField.setText("");
        currentLocationField.setText("");
    }

    /**
     * Refreshes the Shipment and Personnel JComboBoxes.
     * This method is public so MainApplication can call it when the tab is selected.
     */
    public void refreshComboBoxes() {
        populateShipmentComboBox();
        populatePersonnelComboBox();
    }

    /**
     * Populates the Shipment ID JComboBox with all shipments.
     */
    private void populateShipmentComboBox() {
        shipmentIdComboBox.removeAllItems();
        shipmentIdComboBox.addItem(""); // Add a blank default option
        try {
            List<Shipment> shipments = shipmentDAO.getAllShipments();
            for (Shipment shipment : shipments) {
                // Displaying ID and Sender Name for better context
                shipmentIdComboBox.addItem(shipment.getShipmentId() + " (" + shipment.getSenderName() + " to " + shipment.getReceiverName() + ")");
            }
        } catch (SQLException e) {
            System.err.println("Error populating shipment combo box: " + e.getMessage());
        }
    }

    /**
     * Populates the Personnel ID JComboBox with all personnel.
     */
    private void populatePersonnelComboBox() {
        assignedDriverIdComboBox.removeAllItems();
        assignedDriverIdComboBox.addItem(""); // Add a blank default option
        try {
            List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
            for (DeliveryPersonnel personnel : personnelList) {
                assignedDriverIdComboBox.addItem(personnel.getPersonnelId() + " (" + personnel.getName() + ")");
            }
        } catch (SQLException e) {
            System.err.println("Error populating personnel combo box: " + e.getMessage());
        }
    }

    /**
     * Extracts the pure ID from a JComboBox item (e.g., "SHP-123 (Sender Name)" -> "SHP-123").
     */
    private String extractIdFromComboBoxItem(String comboBoxItem) {
        if (comboBoxItem != null && !comboBoxItem.isEmpty()) {
            int parenIndex = comboBoxItem.indexOf(" (");
            if (parenIndex != -1) {
                return comboBoxItem.substring(0, parenIndex);
            }
            return comboBoxItem; // If no parenthesis, assume it's just the ID
        }
        return null;
    }

    /**
     * Helper method to set selected item in a JComboBox based on ID prefix.
     */
    private void setComboBoxSelectionByPrefix(JComboBox<String> comboBox, String idPrefix) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            String item = comboBox.getItemAt(i);
            if (item != null && item.startsWith(idPrefix + " (")) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.setSelectedIndex(-1); // No match found
    }
}
