// view/PersonnelPanel.java
package Model.view;

import Model.controller.DeliveryPersonnelController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * JPanel for managing Delivery Personnel (Add, Update, Remove).
 * This panel provides the UI for creating, modifying, and deleting personnel records.
 */
public class PersonnelPanel extends JPanel {

    private DeliveryPersonnelController personnelController;
    private DefaultTableModel personnelTableModel;

    // UI Components for input
    private JTextField personnelIdField;
    private JTextField personnelNameField;
    private JTextField contactNumberField;
    private JTextField emailField;
    private JTextField vehicleDetailsField;
    private JTextField availabilityStatusField;
    private JTextField currentRouteField;

    /**
     * Constructor for PersonnelPanel.
     * @param personnelController The controller responsible for personnel logic.
     */
    public PersonnelPanel(DeliveryPersonnelController personnelController) {
        this.personnelController = personnelController;
        // The table model needs to be set in the controller that this panel owns.
        this.personnelTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Contact", "Email", "Vehicle", "Availability", "Current Route"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.personnelController.setTableModel(this.personnelTableModel); // Set the table model in the controller

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshPersonnelTable(); // Initial load of data
    }

    /**
     * Initializes the UI components for the Personnel Management panel.
     */
    private void initComponents() {
        // Table Setup
        JTable personnelTable = new JTable(personnelTableModel);
        JScrollPane scrollPane = new JScrollPane(personnelTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input Fields Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Personnel Details"));

        personnelIdField = new JTextField(20);
        personnelIdField.setEditable(false);
        personnelNameField = new JTextField(20);
        contactNumberField = new JTextField(20);
        emailField = new JTextField(20);
        vehicleDetailsField = new JTextField(20);
        availabilityStatusField = new JTextField(20);
        currentRouteField = new JTextField(20);

        inputPanel.add(new JLabel("Personnel ID:")); inputPanel.add(personnelIdField);
        inputPanel.add(new JLabel("Name:")); inputPanel.add(personnelNameField);
        inputPanel.add(new JLabel("Contact Number:")); inputPanel.add(contactNumberField);
        inputPanel.add(new JLabel("Email:")); inputPanel.add(emailField);
        inputPanel.add(new JLabel("Vehicle Details:")); inputPanel.add(vehicleDetailsField);
        inputPanel.add(new JLabel("Availability Status:")); inputPanel.add(availabilityStatusField);
        inputPanel.add(new JLabel("Current Route:")); inputPanel.add(currentRouteField);

        add(inputPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Personnel");
        JButton updateButton = new JButton("Update Personnel");
        JButton deleteButton = new JButton("Delete Personnel");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh Table");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        addButton.addActionListener(e -> addPersonnelAction());
        updateButton.addActionListener(e -> updatePersonnelAction());
        deleteButton.addActionListener(e -> deletePersonnelAction());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> refreshPersonnelTable());

        // Table Selection Listener
        personnelTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && personnelTable.getSelectedRow() != -1) {
                populateFieldsFromTable(personnelTable.getSelectedRow());
            }
        });
    }

    /**
     * Handles the action for adding new personnel.
     */
    private void addPersonnelAction() {
        boolean success = personnelController.addPersonnel(
                personnelNameField.getText(),
                contactNumberField.getText(),
                emailField.getText(),
                vehicleDetailsField.getText(),
                availabilityStatusField.getText()
        );
        if (success) {
            JOptionPane.showMessageDialog(this, "Personnel added successfully!");
            clearFields();
            refreshPersonnelTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add personnel.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action for updating existing personnel.
     */
    private void updatePersonnelAction() {
        if (personnelIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select personnel to update or enter ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = personnelController.updatePersonnel(
                personnelIdField.getText(),
                personnelNameField.getText(),
                contactNumberField.getText(),
                emailField.getText(),
                vehicleDetailsField.getText(),
                availabilityStatusField.getText(),
                currentRouteField.getText().isEmpty() ? null : currentRouteField.getText()
        );
        if (success) {
            JOptionPane.showMessageDialog(this, "Personnel updated successfully!");
            clearFields();
            refreshPersonnelTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update personnel.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action for deleting personnel.
     */
    private void deletePersonnelAction() {
        String personnelIdToDelete = personnelIdField.getText();
        if (personnelIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select personnel to delete or enter ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete personnel " + personnelIdToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = personnelController.deletePersonnel(personnelIdToDelete);
            if (success) {
                JOptionPane.showMessageDialog(this, "Personnel deleted successfully!");
                clearFields();
                refreshPersonnelTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete personnel. They might be assigned to deliveries.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Populates input fields from the selected table row.
     * @param selectedRow The index of the selected row.
     */
    private void populateFieldsFromTable(int selectedRow) {
        personnelIdField.setText((String) personnelTableModel.getValueAt(selectedRow, 0));
        personnelNameField.setText((String) personnelTableModel.getValueAt(selectedRow, 1));
        contactNumberField.setText((String) personnelTableModel.getValueAt(selectedRow, 2));
        emailField.setText((String) personnelTableModel.getValueAt(selectedRow, 3));
        vehicleDetailsField.setText((String) personnelTableModel.getValueAt(selectedRow, 4));
        availabilityStatusField.setText((String) personnelTableModel.getValueAt(selectedRow, 5));

        Object routeObj = personnelTableModel.getValueAt(selectedRow, 6);
        currentRouteField.setText(routeObj != null ? routeObj.toString() : "");
    }

    /**
     * Clears all input fields on the Personnel panel.
     */
    private void clearFields() {
        personnelIdField.setText("");
        personnelNameField.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        vehicleDetailsField.setText("");
        availabilityStatusField.setText("");
        currentRouteField.setText("");
    }

    /**
     * Refreshes the personnel table by fetching data from the controller.
     * This method is public so MainApplication can call it when the tab is selected.
     */
    public void refreshPersonnelTable() {
        personnelController.refreshPersonnelTable();
    }
}
