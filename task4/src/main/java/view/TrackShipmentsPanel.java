// view/TrackShipmentsPanel.java
package view;

import controller.ShipmentController;
import Model.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * JPanel for tracking shipment status and location (Task: "Track Shipments").
 * This panel displays shipment data with full tracking capabilities including:
 * - Location updates
 * - ETA adjustments
 * - Delay recording
 * - Comprehensive status tracking
 */
public class TrackShipmentsPanel extends JPanel {

    private ShipmentController shipmentController;
    private DefaultTableModel shipmentTableModel;
    private JTable shipmentTable;

    // UI Components for search/filter
    private JTextField searchShipmentIdField;
    private JComboBox<String> filterStatusComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;
    private JButton refreshTableButton;

    // UI Components for tracking updates
    private JComboBox<String> locationComboBox;
    private JButton updateLocationButton;
    private JSpinner etaDateSpinner;
    private JSpinner etaTimeSpinner;
    private JButton updateEtaButton;
    private JTextField delayReasonField;
    private JSpinner delayDurationSpinner;
    private JButton recordDelayButton;
    private JButton viewHistoryButton;

    /**
     * Constructor for TrackShipmentsPanel.
     * @param shipmentController The controller responsible for shipment data.
     */
    public TrackShipmentsPanel(ShipmentController shipmentController) {
        this.shipmentController = shipmentController;
        this.shipmentTableModel = new DefaultTableModel(new String[]{
                "ID", "Sender", "Receiver", "Contents", "Status",
                "Location", "Driver ID", "Scheduled Date",
                "Estimated Time", "Delay Info"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells read-only
            }
        };
        this.shipmentController.setTableModel(this.shipmentTableModel);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshShipmentTable(); // Initial load
    }

    /**
     * Initializes the UI components for the Shipment Tracking panel.
     */
    private void initComponents() {
        // Search/Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Shipments"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Search controls
        gbc.gridy = 0;
        gbc.gridx = 0;
        filterPanel.add(new JLabel("Shipment ID:"), gbc);

        gbc.gridx = 1;
        searchShipmentIdField = new JTextField(15);
        filterPanel.add(searchShipmentIdField, gbc);

        gbc.gridx = 2;
        searchButton = new JButton("Search by ID");
        filterPanel.add(searchButton, gbc);

        gbc.gridx = 3;
        filterPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 4;
        filterStatusComboBox = new JComboBox<>(new String[]{"All", "Pending", "In Transit", "Out for Delivery", "Delivered", "Failed", "Assigned", "Delayed"});
        filterPanel.add(filterStatusComboBox, gbc);

        gbc.gridx = 5;
        clearSearchButton = new JButton("Clear Filter");
        filterPanel.add(clearSearchButton, gbc);

        gbc.gridx = 6;
        refreshTableButton = new JButton("Refresh All");
        filterPanel.add(refreshTableButton, gbc);

        // Row 2: Tracking controls
        gbc.gridy = 1;
        gbc.gridx = 0;
        filterPanel.add(new JLabel("Update Location:"), gbc);

        gbc.gridx = 1;
        locationComboBox = new JComboBox<>(new String[]{
                "Warehouse", "In Transit", "Out for Delivery",
                "At Local Hub", "Delivery Attempted", "Delivered"
        });
        filterPanel.add(locationComboBox, gbc);

        gbc.gridx = 2;
        updateLocationButton = new JButton("Update Location");
        filterPanel.add(updateLocationButton, gbc);

        gbc.gridx = 3;
        filterPanel.add(new JLabel("New ETA Date:"), gbc);

        gbc.gridx = 4;
        etaDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(etaDateSpinner, "yyyy-MM-dd");
        etaDateSpinner.setEditor(dateEditor);
        filterPanel.add(etaDateSpinner, gbc);

        gbc.gridx = 5;
        filterPanel.add(new JLabel("Time:"), gbc);

        gbc.gridx = 6;
        etaTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(etaTimeSpinner, "HH:mm");
        etaTimeSpinner.setEditor(timeEditor);
        filterPanel.add(etaTimeSpinner, gbc);

        gbc.gridx = 7;
        updateEtaButton = new JButton("Update ETA");
        filterPanel.add(updateEtaButton, gbc);

        // Row 3: Delay controls
        gbc.gridy = 2;
        gbc.gridx = 0;
        filterPanel.add(new JLabel("Delay Reason:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        delayReasonField = new JTextField(20);
        filterPanel.add(delayReasonField, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 3;
        filterPanel.add(new JLabel("Hours:"), gbc);

        gbc.gridx = 4;
        delayDurationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 24, 1));
        filterPanel.add(delayDurationSpinner, gbc);

        gbc.gridx = 5;
        recordDelayButton = new JButton("Record Delay");
        filterPanel.add(recordDelayButton, gbc);

        gbc.gridx = 6;
        viewHistoryButton = new JButton("View History");
        filterPanel.add(viewHistoryButton, gbc);

        add(filterPanel, BorderLayout.NORTH);

        // Table Setup
        shipmentTable = new JTable(shipmentTableModel);
        shipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        searchButton.addActionListener(e -> searchShipmentByIdAction());
        clearSearchButton.addActionListener(e -> clearFilterAction());
        refreshTableButton.addActionListener(e -> refreshShipmentTable());
        filterStatusComboBox.addActionListener(e -> refreshShipmentTable());
        updateLocationButton.addActionListener(e -> updateLocationAction());
        updateEtaButton.addActionListener(e -> updateEtaAction());
        recordDelayButton.addActionListener(e -> recordDelayAction());
        viewHistoryButton.addActionListener(e -> viewHistoryAction());
    }

    /**
     * Handles searching for a shipment by ID.
     */
    private void searchShipmentByIdAction() {
        String shipmentId = searchShipmentIdField.getText().trim();
        if (shipmentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Shipment ID to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Shipment shipment = shipmentController.getShipmentById(shipmentId);
            shipmentTableModel.setRowCount(0); // Clear table
            if (shipment != null) {
                addShipmentToTable(shipment);
            } else {
                JOptionPane.showMessageDialog(this, "Shipment with ID '" + shipmentId + "' not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("Error searching shipment: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error searching shipment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears the search filter and refreshes the table.
     */
    private void clearFilterAction() {
        searchShipmentIdField.setText("");
        filterStatusComboBox.setSelectedItem("All");
        refreshShipmentTable();
    }

    /**
     * Updates the location of the selected shipment.
     */
    private void updateLocationAction() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = (String) shipmentTableModel.getValueAt(selectedRow, 0);
        String newLocation = (String) locationComboBox.getSelectedItem();

        try {
            Shipment shipment = shipmentController.getShipmentById(shipmentId);
            if (shipment != null) {
                // Determine appropriate status based on location
                String newStatus = shipment.getDeliveryStatus();
                if (newLocation.equals("Delivered")) {
                    newStatus = "Delivered";
                } else if (newLocation.equals("Warehouse")) {
                    newStatus = "Pending";
                } else if (!newStatus.equals("Delivered") && !newStatus.equals("Failed")) {
                    newStatus = "In Transit";
                }

                shipment.setCurrentLocation(newLocation);
                shipment.setDeliveryStatus(newStatus);

                boolean success = shipmentController.updateShipment(
                        shipment.getShipmentId(),
                        shipment.getSenderName(),
                        shipment.getSenderAddress(),
                        shipment.getReceiverName(),
                        shipment.getReceiverAddress(),
                        shipment.getPackageContents(),
                        shipment.getWeightKg(),
                        shipment.getDimensionsCm(),
                        newStatus,
                        newLocation,
                        shipment.getScheduledDeliveryDate(),
                        shipment.getEstimatedDeliveryTime(),
                        shipment.getAssignedDriverId()
                );

                if (success) {
                    refreshShipmentTable();
                    JOptionPane.showMessageDialog(this, "Location updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating location: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the ETA of the selected shipment.
     */
    private void updateEtaAction() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = (String) shipmentTableModel.getValueAt(selectedRow, 0);
        Date newDate = new Date(((java.util.Date)etaDateSpinner.getValue()).getTime());
        Time newTime = new Time(((java.util.Date)etaTimeSpinner.getValue()).getTime());

        try {
            Shipment shipment = shipmentController.getShipmentById(shipmentId);
            if (shipment != null) {
                boolean success = shipmentController.updateShipment(
                        shipment.getShipmentId(),
                        shipment.getSenderName(),
                        shipment.getSenderAddress(),
                        shipment.getReceiverName(),
                        shipment.getReceiverAddress(),
                        shipment.getPackageContents(),
                        shipment.getWeightKg(),
                        shipment.getDimensionsCm(),
                        shipment.getDeliveryStatus(),
                        shipment.getCurrentLocation(),
                        newDate,
                        newTime,
                        shipment.getAssignedDriverId()
                );

                if (success) {
                    refreshShipmentTable();
                    JOptionPane.showMessageDialog(this, "ETA updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating ETA: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Records a delay for the selected shipment.
     */
    private void recordDelayAction() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = (String) shipmentTableModel.getValueAt(selectedRow, 0);
        String reason = delayReasonField.getText().trim();
        int delayHours = (Integer) delayDurationSpinner.getValue();

        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a delay reason.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Shipment shipment = shipmentController.getShipmentById(shipmentId);
            if (shipment != null) {
                // Update the shipment status to indicate delay
                String newStatus = "Delayed";

                // Calculate new ETA by adding delay hours
                long originalTime = shipment.getScheduledDeliveryDate().getTime() + shipment.getEstimatedDeliveryTime().getTime();
                long newTime = originalTime + (delayHours * 60 * 60 * 1000);
                Date newDate = new Date(newTime);
                Time newTimeObj = new Time(newTime);

                // Add delay note to package contents
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String delayNote = "\n[DELAY at " + sdf.format(new java.util.Date()) + "]: " +
                        reason + " (" + delayHours + " hours)";
                String newContents = shipment.getPackageContents() + delayNote;

                boolean success = shipmentController.updateShipment(
                        shipment.getShipmentId(),
                        shipment.getSenderName(),
                        shipment.getSenderAddress(),
                        shipment.getReceiverName(),
                        shipment.getReceiverAddress(),
                        newContents,
                        shipment.getWeightKg(),
                        shipment.getDimensionsCm(),
                        newStatus,
                        shipment.getCurrentLocation(),
                        newDate,
                        newTimeObj,
                        shipment.getAssignedDriverId()
                );

                if (success) {
                    refreshShipmentTable();
                    JOptionPane.showMessageDialog(this, "Delay recorded successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear delay fields
                    delayReasonField.setText("");
                    delayDurationSpinner.setValue(0);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error recording delay: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Views the history of the selected shipment.
     */
    private void viewHistoryAction() {
        int selectedRow = shipmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String shipmentId = (String) shipmentTableModel.getValueAt(selectedRow, 0);
        try {
            Shipment shipment = shipmentController.getShipmentById(shipmentId);
            if (shipment != null) {
                // Extract history from package contents (where we've been storing updates)
                String history = shipment.getPackageContents();

                JTextArea textArea = new JTextArea(history, 15, 50);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);

                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane,
                        "History for Shipment " + shipmentId, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error viewing history: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the shipment table based on the selected filter.
     */
    public void refreshShipmentTable() {
        try {
            List<Shipment> allShipments = shipmentController.getShipmentDAO().getAllShipments();
            shipmentTableModel.setRowCount(0); // Clear existing data

            String selectedStatus = (String) filterStatusComboBox.getSelectedItem();
            String searchId = searchShipmentIdField.getText().trim();

            for (Shipment shipment : allShipments) {
                boolean matchesStatus = "All".equals(selectedStatus) || selectedStatus.equalsIgnoreCase(shipment.getDeliveryStatus());
                boolean matchesId = searchId.isEmpty() || shipment.getShipmentId().equalsIgnoreCase(searchId);

                if (matchesStatus && matchesId) {
                    addShipmentToTable(shipment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing shipment table for tracking: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading shipment data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper method to add a shipment to the table with proper formatting.
     */
    private void addShipmentToTable(Shipment shipment) {
        String delayInfo = "";
        if ("Delayed".equalsIgnoreCase(shipment.getDeliveryStatus())) {
            // Extract most recent delay info from package contents
            String contents = shipment.getPackageContents();
            if (contents.contains("[DELAY")) {
                int lastDelayIndex = contents.lastIndexOf("[DELAY");
                delayInfo = contents.substring(lastDelayIndex, contents.indexOf("]", lastDelayIndex) + 1);
            }
        }

        shipmentTableModel.addRow(new Object[]{
                shipment.getShipmentId(),
                shipment.getSenderName(),
                shipment.getReceiverName(),
                shipment.getPackageContents(),
                shipment.getDeliveryStatus(),
                shipment.getCurrentLocation(),
                shipment.getAssignedDriverId(),
                shipment.getScheduledDeliveryDate(),
                shipment.getEstimatedDeliveryTime(),
                delayInfo
        });
    }
}