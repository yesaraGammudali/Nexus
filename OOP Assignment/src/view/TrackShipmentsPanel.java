// view/TrackShipmentsPanel.java
package view;

import controller.ShipmentController;
import Model.Shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException; // Keep this import as other methods might use it, or for future expansion
import java.util.List;

/**
 * JPanel for tracking shipment status and location (Task: "Track Shipments").
 * This panel displays a read-only view of shipment data and allows filtering/refreshing.
 */
public class TrackShipmentsPanel extends JPanel {

    private ShipmentController shipmentController;
    private DefaultTableModel shipmentTableModel;

    // UI Components for search/filter
    private JTextField searchShipmentIdField;
    private JComboBox<String> filterStatusComboBox;
    private JButton searchButton;
    private JButton clearSearchButton;

    /**
     * Constructor for TrackShipmentsPanel.
     * @param shipmentController The controller responsible for shipment data.
     */
    public TrackShipmentsPanel(ShipmentController shipmentController) {
        this.shipmentController = shipmentController;
        this.shipmentTableModel = new DefaultTableModel(new String[]{"ID", "Sender", "Receiver", "Contents", "Status", "Location", "Driver ID", "Scheduled Date", "Estimated Time"}, 0) {
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
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Shipments"));

        searchShipmentIdField = new JTextField(15);
        filterStatusComboBox = new JComboBox<>(new String[]{"All", "Pending", "In Transit", "Out for Delivery", "Delivered", "Failed", "Assigned"});
        searchButton = new JButton("Search by ID");
        clearSearchButton = new JButton("Clear Filter");
        JButton refreshTableButton = new JButton("Refresh All Shipments");

        filterPanel.add(new JLabel("Shipment ID:"));
        filterPanel.add(searchShipmentIdField);
        filterPanel.add(searchButton);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(filterStatusComboBox);
        filterPanel.add(clearSearchButton);
        filterPanel.add(refreshTableButton);

        add(filterPanel, BorderLayout.NORTH);

        // Table Setup (same as ShipmentPanel, but conceptually for tracking)
        JTable shipmentTable = new JTable(shipmentTableModel);
        JScrollPane scrollPane = new JScrollPane(shipmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action Listeners
        searchButton.addActionListener(e -> searchShipmentByIdAction());
        clearSearchButton.addActionListener(e -> clearFilterAction());
        refreshTableButton.addActionListener(e -> refreshShipmentTable());
        filterStatusComboBox.addActionListener(e -> refreshShipmentTable()); // Refresh when status filter changes
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
            } else {
                // FIXED: Corrected JOptionPane syntax
                JOptionPane.showMessageDialog(this, "Shipment with ID '" + shipmentId + "' not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
            // FIXED: Changed to catch general Exception because getShipmentById does not declare throwing SQLException
        } catch (Exception ex) {
            System.err.println("Error searching shipment: " + ex.getMessage());
            // FIXED: Corrected JOptionPane syntax
            JOptionPane.showMessageDialog(this, "Error searching shipment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears the search filter and refreshes the table.
     */
    private void clearFilterAction() {
        searchShipmentIdField.setText("");
        filterStatusComboBox.setSelectedItem("All"); // Reset filter
        refreshShipmentTable();
    }

    /**
     * Refreshes the shipment table based on the selected filter.
     * This method is public so MainApplication can call it when the tab is selected.
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
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing shipment table for tracking: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading shipment data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
