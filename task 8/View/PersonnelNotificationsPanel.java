// view/PersonnelNotificationsPanel.java
package view;

import controller.NotificationController;
import DAO.DeliveryPersonnelDAO;
import Model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * JPanel for sending notifications to delivery personnel (Task: "Personnel Notifications").
 * This panel provides the UI for selecting personnel and sending a notification.
 */
public class PersonnelNotificationsPanel extends JPanel {

    private NotificationController notificationController;
    private DeliveryPersonnelDAO personnelDAO; // Needed to populate personnel combo box
    private DefaultTableModel notificationTableModel; // To show relevant personnel notifications

    // UI Components for input
    private JTextField notificationIdField; // For selection/deletion
    private JComboBox<String> personnelComboBox;
    private JTextArea messageContentArea;
    private JTextField notificationTypeField;
    private JTextField statusField; // Read-only, set by controller

    /**
     * Constructor for PersonnelNotificationsPanel.
     * @param notificationController The controller for notification logic.
     * @param personnelDAO The DAO for Personnel data (to populate combo box).
     */
    public PersonnelNotificationsPanel(NotificationController notificationController, DeliveryPersonnelDAO personnelDAO) {
        this.notificationController = notificationController;
        this.personnelDAO = personnelDAO;
        this.notificationTableModel = new DefaultTableModel(new String[]{"ID", "Personnel ID", "Contact (Name/Number)", "Message", "Type", "Status", "Timestamp"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.notificationController.setTableModel(this.notificationTableModel);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshAllNotificationData(); // Initial load
    }

    /**
     * Initializes the UI components for the Personnel Notifications panel.
     */
    private void initComponents() {
        // Input Fields Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Send Personnel Notification"));

        notificationIdField = new JTextField(20);
        notificationIdField.setEditable(false); // ID is generated or from table selection
        personnelComboBox = new JComboBox<>();
        messageContentArea = new JTextArea(3, 20);
        messageContentArea.setLineWrap(true);
        messageContentArea.setWrapStyleWord(true);
        JScrollPane msgScrollPane = new JScrollPane(messageContentArea);
        notificationTypeField = new JTextField(20);
        statusField = new JTextField(20);
        statusField.setEditable(false); // Status is set by controller

        inputPanel.add(new JLabel("Notification ID (from table):")); inputPanel.add(notificationIdField);
        inputPanel.add(new JLabel("Select Personnel:")); inputPanel.add(personnelComboBox);
        inputPanel.add(new JLabel("Message:")); inputPanel.add(msgScrollPane);
        inputPanel.add(new JLabel("Type (e.g., SMS, Email):")); inputPanel.add(notificationTypeField);
        inputPanel.add(new JLabel("Status:")); inputPanel.add(statusField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton sendButton = new JButton("Send Notification");
        JButton deleteButton = new JButton("Delete Notification");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh Table");

        buttonPanel.add(sendButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Table Setup to display personnel notifications (personnel_id is not null, shipment_id is null)
        JTable notificationTable = new JTable(notificationTableModel);
        JScrollPane scrollPane = new JScrollPane(notificationTable);
        add(scrollPane, BorderLayout.CENTER); // Add table after buttons

        // Action Listeners
        sendButton.addActionListener(e -> sendPersonnelNotificationAction());
        deleteButton.addActionListener(e -> deleteNotificationAction());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> refreshAllNotificationData());

        // Table Selection Listener
        notificationTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && notificationTable.getSelectedRow() != -1) {
                populateFieldsFromTable(notificationTable.getSelectedRow());
            }
        });
    }

    /**
     * Handles sending a personnel notification.
     */
    private void sendPersonnelNotificationAction() {
        String selectedPersonnelIdItem = (String) personnelComboBox.getSelectedItem();
        String selectedPersonnelId = extractIdFromComboBoxItem(selectedPersonnelIdItem);

        if (selectedPersonnelId == null || selectedPersonnelId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select Personnel.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (messageContentArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Message content cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = notificationController.sendPersonnelNotification(
                selectedPersonnelId,
                messageContentArea.getText(),
                notificationTypeField.getText()
        );
        if (success) {
            JOptionPane.showMessageDialog(this, "Personnel notification sent!");
            clearFields();
            refreshAllNotificationData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send personnel notification. Check personnel ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles deleting a notification.
     */
    private void deleteNotificationAction() {
        String notifIdToDelete = notificationIdField.getText();
        if (notifIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a notification to delete or enter its ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete notification " + notifIdToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = notificationController.deleteNotification(notifIdToDelete);
            if (success) {
                JOptionPane.showMessageDialog(this, "Notification deleted successfully!");
                clearFields();
                refreshAllNotificationData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete notification.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Populates input fields and combo boxes from the selected table row.
     * Only relevant fields for personnel notifications are populated.
     * @param selectedRow The index of the selected row.
     */
    private void populateFieldsFromTable(int selectedRow) {
        notificationIdField.setText((String) notificationTableModel.getValueAt(selectedRow, 0));

        String personnelIdFromTable = (String) notificationTableModel.getValueAt(selectedRow, 1);
        if (personnelIdFromTable != null) {
            setComboBoxSelectionByPrefix(personnelComboBox, personnelIdFromTable);
        } else {
            personnelComboBox.setSelectedIndex(-1);
        }

        messageContentArea.setText((String) notificationTableModel.getValueAt(selectedRow, 3));
        notificationTypeField.setText((String) notificationTableModel.getValueAt(selectedRow, 4));
        statusField.setText((String) notificationTableModel.getValueAt(selectedRow, 5));
    }

    /**
     * Clears all input fields on the panel.
     */
    private void clearFields() {
        notificationIdField.setText("");
        personnelComboBox.setSelectedIndex(-1);
        messageContentArea.setText("");
        notificationTypeField.setText("");
        statusField.setText("");
    }

    /**
     * Refreshes the notification table (only showing personnel notifications) and the personnel combo box.
     */
    public void refreshNotificationTable() {
        // Filter notifications to only show personnel-related ones (personnel_id present, shipment_id null)
        try {
            List<Model.Notification> allNotifications = notificationController.getNotificationDAO().getAllNotifications();
            notificationTableModel.setRowCount(0); // Clear existing data

            for (Model.Notification notif : allNotifications) {
                if (notif.getPersonnelId() != null && notif.getShipmentId() == null) { // Only personnel notifications
                    notificationTableModel.addRow(new Object[]{
                            notif.getNotificationId(),
                            notif.getPersonnelId(),
                            notif.getCustomerContact(), // This field holds personnel contact for personnel notifications
                            notif.getMessageContent(),
                            notif.getNotificationType(),
                            notif.getStatus(),
                            notif.getTimestamp()
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing personnel notification table: " + e.getMessage());
        }
    }

    public void refreshComboBoxes() {
        populatePersonnelComboBox();
    }

    // Making this method public so MainApplication can call it
    /**
     * Refreshes all data and combo boxes relevant to this panel.
     * This method is public so MainApplication can call it when the tab is selected.
     */
    public void refreshAllNotificationData() {
        refreshNotificationTable();
        refreshComboBoxes();
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
     * Extracts the pure ID from a JComboBox item (e.g., "DP-123 (Driver Name)" -> "DP-123").
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
