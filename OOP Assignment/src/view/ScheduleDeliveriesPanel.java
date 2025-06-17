// view/ScheduleDeliveriesPanel.java
package view;

import controller.DeliveryController;
import DAO.ShipmentDAO;
import DAO.DeliveryPersonnelDAO;
import Model.Shipment;
import Model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Still needed for the controller's table model, though not used in this view directly
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.List;

/**
 * JPanel for scheduling new deliveries (Task: "Schedule Deliveries").
 * This panel allows users to select a shipment and personnel to schedule a delivery.
 */
public class ScheduleDeliveriesPanel extends JPanel {

    private DeliveryController deliveryController;
    private ShipmentDAO shipmentDAO; // Needed to populate combo boxes
    private DeliveryPersonnelDAO personnelDAO; // Needed to populate combo boxes

    // UI Components for input
    private JComboBox<String> shipmentComboBox;
    private JComboBox<String> personnelComboBox;
    private JTextField scheduledDateField;
    private JTextField estimatedTimeField;

    /**
     * Constructor for ScheduleDeliveriesPanel.
     * @param deliveryController The controller responsible for delivery logic.
     * @param shipmentDAO The DAO for Shipment data (to populate combo box).
     * @param personnelDAO The DAO for Personnel data (to populate combo box).
     */
    public ScheduleDeliveriesPanel(DeliveryController deliveryController, ShipmentDAO shipmentDAO, DeliveryPersonnelDAO personnelDAO) {
        this.deliveryController = deliveryController;
        this.shipmentDAO = shipmentDAO;
        this.personnelDAO = personnelDAO;
        // The table model is owned by a different DeliveryPanel (e.g., TrackDeliveriesPanel)
        // so no table model setup here.

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshComboBoxes(); // Initial load for combo boxes
    }

    /**
     * Initializes the UI components for the Schedule Deliveries panel.
     */
    private void initComponents() {
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // Simpler grid for scheduling
        inputPanel.setBorder(BorderFactory.createTitledBorder("Schedule New Delivery"));

        shipmentComboBox = new JComboBox<>();
        personnelComboBox = new JComboBox<>();
        scheduledDateField = new JTextField(20);
        estimatedTimeField = new JTextField(20);

        inputPanel.add(new JLabel("Select Shipment:")); inputPanel.add(shipmentComboBox);
        inputPanel.add(new JLabel("Select Personnel:")); inputPanel.add(personnelComboBox);
        inputPanel.add(new JLabel("Scheduled Date (YYYY-MM-DD):")); inputPanel.add(scheduledDateField);
        inputPanel.add(new JLabel("Estimated Time (HH:MM:SS):")); inputPanel.add(estimatedTimeField);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton scheduleButton = new JButton("Schedule Delivery");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshDropdownsButton = new JButton("Refresh Dropdowns"); // Button to refresh combo boxes

        buttonPanel.add(scheduleButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshDropdownsButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        scheduleButton.addActionListener(e -> scheduleDeliveryAction());
        clearButton.addActionListener(e -> clearFields());
        refreshDropdownsButton.addActionListener(e -> refreshComboBoxes());
    }

    /**
     * Handles the action for scheduling a new delivery.
     */
    private void scheduleDeliveryAction() {
        try {
            String selectedShipmentIdItem = (String) shipmentComboBox.getSelectedItem();
            String selectedPersonnelIdItem = (String) personnelComboBox.getSelectedItem();

            String selectedShipmentId = extractIdFromComboBoxItem(selectedShipmentIdItem);
            String selectedPersonnelId = extractIdFromComboBoxItem(selectedPersonnelIdItem);

            if (selectedShipmentId == null || selectedShipmentId.isEmpty() ||
                    selectedPersonnelId == null || selectedPersonnelId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select both a Shipment and a Personnel.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date scheduledDate = Date.valueOf(scheduledDateField.getText());
            Time estimatedTime = Time.valueOf(estimatedTimeField.getText());

            // Schedule Delivery via controller (which also updates shipment/personnel status)
            boolean success = deliveryController.scheduleDelivery(
                    selectedShipmentId,
                    selectedPersonnelId,
                    scheduledDate,
                    estimatedTime
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Delivery scheduled successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to schedule delivery.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. Use YYYY-MM-DD and HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all input fields on the panel.
     */
    private void clearFields() {
        shipmentComboBox.setSelectedIndex(-1);
        personnelComboBox.setSelectedIndex(-1);
        scheduledDateField.setText("");
        estimatedTimeField.setText("");
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
     * Populates the Shipment ID JComboBox.
     */
    private void populateShipmentComboBox() {
        shipmentComboBox.removeAllItems();
        shipmentComboBox.addItem(""); // Add a blank default option
        try {
            List<Shipment> shipments = shipmentDAO.getAllShipments();
            for (Shipment shipment : shipments) {
                shipmentComboBox.addItem(shipment.getShipmentId() + " (" + shipment.getSenderName() + " to " + shipment.getReceiverName() + ")");
            }
        } catch (SQLException e) {
            System.err.println("Error populating shipment combo box: " + e.getMessage());
        }
    }

    /**
     * Populates the Personnel ID JComboBox.
     */
    private void populatePersonnelComboBox() {
        personnelComboBox.removeAllItems();
        personnelComboBox.addItem(""); // Add a blank default option
        try {
            List<DeliveryPersonnel> personnelList = personnelDAO.getAllPersonnel();
            for (DeliveryPersonnel personnel : personnelList) {
                personnelComboBox.addItem(personnel.getPersonnelId() + " (" + personnel.getName() + ")");
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
}
