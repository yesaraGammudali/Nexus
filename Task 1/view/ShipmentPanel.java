// view/ShipmentPanel.java
package view;

import controller.ShipmentController;
import Model.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException; // Needed for try-catch blocks

/**
 * JPanel for managing Shipment-related operations (Add, Update, Remove).
 * This panel provides the UI for creating, modifying, and deleting shipment records.
 */
public class ShipmentPanel extends JPanel {

    private ShipmentController shipmentController;
    private DefaultTableModel shipmentTableModel;

    // UI Components for input
    private JTextField shipmentIdField;
    private JTextField senderNameField;
    private JTextField senderAddressField;
    private JTextField receiverNameField;
    private JTextField receiverAddressField;
    private JTextField packageContentsField;
    private JTextField weightKgField;
    private JTextField dimensionsCmField;
    private JTextField deliveryStatusField;
    private JTextField currentLocationField;
    private JTextField scheduledDateField;
    private JTextField estimatedTimeField;
    private JTextField assignedDriverIdField;

    /**
     * Constructor for ShipmentPanel.
     * @param shipmentController The controller responsible for shipment logic.
     */
    public ShipmentPanel(ShipmentController shipmentController) {
        this.shipmentController = shipmentController;
        // The table model needs to be set in the controller that this panel owns.
        // It's better to create it here and pass it.
        this.shipmentTableModel = new DefaultTableModel(new String[]{"ID", "Sender", "Receiver", "Contents", "Status", "Location", "Driver ID", "Scheduled Date", "Estimated Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        this.shipmentController.setTableModel(this.shipmentTableModel); // Set the table model in the controller

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshShipmentTable(); // Initial load of data
    }

    /**
     * Initializes the UI components for the Shipment Management panel.
     */
    private void initComponents() {
        // Table Setup
        JTable shipmentTable = new JTable(shipmentTableModel);
        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input Fields Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Shipment Details"));

        shipmentIdField = new JTextField(20);
        shipmentIdField.setEditable(false);
        senderNameField = new JTextField(20);
        senderAddressField = new JTextField(20);
        receiverNameField = new JTextField(20);
        receiverAddressField = new JTextField(20);
        packageContentsField = new JTextField(20);
        weightKgField = new JTextField(20);
        dimensionsCmField = new JTextField(20);
        deliveryStatusField = new JTextField(20);
        currentLocationField = new JTextField(20);
        scheduledDateField = new JTextField(20);
        estimatedTimeField = new JTextField(20);
        assignedDriverIdField = new JTextField(20);

        inputPanel.add(new JLabel("Shipment ID:")); inputPanel.add(shipmentIdField);
        inputPanel.add(new JLabel("Sender Name:")); inputPanel.add(senderNameField);
        inputPanel.add(new JLabel("Sender Address:")); inputPanel.add(senderAddressField);
        inputPanel.add(new JLabel("Receiver Name:")); inputPanel.add(receiverNameField);
        inputPanel.add(new JLabel("Receiver Address:")); inputPanel.add(receiverAddressField);
        inputPanel.add(new JLabel("Contents:")); inputPanel.add(packageContentsField);
        inputPanel.add(new JLabel("Weight (kg):")); inputPanel.add(weightKgField);
        inputPanel.add(new JLabel("Dimensions (cm):")); inputPanel.add(dimensionsCmField);
        inputPanel.add(new JLabel("Delivery Status:")); inputPanel.add(deliveryStatusField);
        inputPanel.add(new JLabel("Current Location:")); inputPanel.add(currentLocationField);
        inputPanel.add(new JLabel("Scheduled Date (YYYY-MM-DD):")); inputPanel.add(scheduledDateField);
        inputPanel.add(new JLabel("Estimated Time (HH:MM:SS):")); inputPanel.add(estimatedTimeField);
        inputPanel.add(new JLabel("Assigned Driver ID:")); inputPanel.add(assignedDriverIdField);

        add(inputPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Shipment");
        JButton updateButton = new JButton("Update Shipment");
        JButton deleteButton = new JButton("Delete Shipment");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh Table");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> addShipmentAction());
        updateButton.addActionListener(e -> updateShipmentAction());
        deleteButton.addActionListener(e -> deleteShipmentAction());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> refreshShipmentTable());

        // Table Selection Listener
        shipmentTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && shipmentTable.getSelectedRow() != -1) {
                populateFieldsFromTable(shipmentTable.getSelectedRow());
            }
        });
    }

    /**
     * Handles the action for adding a new shipment.
     */
    private void addShipmentAction() {
        try {
            boolean success = shipmentController.addShipment(
                    senderNameField.getText(),
                    senderAddressField.getText(),
                    receiverNameField.getText(),
                    receiverAddressField.getText(),
                    packageContentsField.getText(),
                    new BigDecimal(weightKgField.getText()),
                    dimensionsCmField.getText(),
                    "Pending" // Initial status for new shipment
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Shipment added successfully!");
                clearFields();
                refreshShipmentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add shipment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid weight format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action for updating an existing shipment.
     */
    private void updateShipmentAction() {
        if (shipmentIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to update or enter its ID.", "Error", JOptionPane.ERROR_MESSAGE);
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

            boolean success = shipmentController.updateShipment(
                    shipmentIdField.getText(),
                    senderNameField.getText(),
                    senderAddressField.getText(),
                    receiverNameField.getText(),
                    receiverAddressField.getText(),
                    packageContentsField.getText(),
                    new BigDecimal(weightKgField.getText()),
                    dimensionsCmField.getText(),
                    deliveryStatusField.getText(),
                    currentLocationField.getText(),
                    scheduledDate,
                    estimatedTime,
                    assignedDriverIdField.getText().isEmpty() ? null : assignedDriverIdField.getText()
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Shipment updated successfully!");
                clearFields();
                refreshShipmentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update shipment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid weight format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. UseYYYY-MM-DD and HH:MM:SS.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action for deleting a shipment.
     */
    private void deleteShipmentAction() {
        String shipmentIdToDelete = shipmentIdField.getText();
        if (shipmentIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to delete or enter its ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete shipment " + shipmentIdToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = shipmentController.deleteShipment(shipmentIdToDelete);
            if (success) {
                JOptionPane.showMessageDialog(this, "Shipment deleted successfully!");
                clearFields();
                refreshShipmentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete shipment. It might be linked to deliveries.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Populates input fields from the selected table row.
     * @param selectedRow The index of the selected row.
     */
    private void populateFieldsFromTable(int selectedRow) {
        shipmentIdField.setText((String) shipmentTableModel.getValueAt(selectedRow, 0));
        senderNameField.setText((String) shipmentTableModel.getValueAt(selectedRow, 1));
        receiverNameField.setText((String) shipmentTableModel.getValueAt(selectedRow, 2));
        packageContentsField.setText((String) shipmentTableModel.getValueAt(selectedRow, 3));
        deliveryStatusField.setText((String) shipmentTableModel.getValueAt(selectedRow, 4));
        currentLocationField.setText((String) shipmentTableModel.getValueAt(selectedRow, 5));

        Object driverIdObj = shipmentTableModel.getValueAt(selectedRow, 6);
        assignedDriverIdField.setText(driverIdObj != null ? driverIdObj.toString() : "");

        Object scheduledDateObj = shipmentTableModel.getValueAt(selectedRow, 7);
        scheduledDateField.setText(scheduledDateObj != null ? scheduledDateObj.toString() : "");

        Object estimatedTimeObj = shipmentTableModel.getValueAt(selectedRow, 8);
        estimatedTimeField.setText(estimatedTimeObj != null ? estimatedTimeObj.toString() : "");

        // Fetch full shipment object to get all details not directly in table
        Shipment selectedShipment = shipmentController.getShipmentById(shipmentIdField.getText());
        if (selectedShipment != null) {
            senderAddressField.setText(selectedShipment.getSenderAddress());
            receiverAddressField.setText(selectedShipment.getReceiverAddress());
            weightKgField.setText(selectedShipment.getWeightKg() != null ? selectedShipment.getWeightKg().toString() : "");
            dimensionsCmField.setText(selectedShipment.getDimensionsCm());
        }
    }

    /**
     * Clears all input fields on the Shipment panel.
     */
    private void clearFields() {
        shipmentIdField.setText("");
        senderNameField.setText("");
        senderAddressField.setText("");
        receiverNameField.setText("");
        receiverAddressField.setText("");
        packageContentsField.setText("");
        weightKgField.setText("");
        dimensionsCmField.setText("");
        deliveryStatusField.setText("");
        currentLocationField.setText("");
        scheduledDateField.setText("");
        estimatedTimeField.setText("");
        assignedDriverIdField.setText("");
    }

    /**
     * Refreshes the shipment table by fetching data from the controller.
     * This method is public so MainApplication can call it when the tab is selected.
     */
    public void refreshShipmentTable() {
        shipmentController.refreshShipmentTable();
    }
}
