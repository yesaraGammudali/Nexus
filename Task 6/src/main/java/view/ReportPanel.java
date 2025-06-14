// view/ReportPanel.java
package view;

import controller.ReportController;
import Model.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException; // Needed for try-catch blocks
import java.time.LocalDate;

/**
 * JPanel for managing Report generation and management (Task: "Reports").
 * This panel provides the UI for generating and deleting reports.
 */
public class ReportPanel extends JPanel {

    private ReportController reportController;
    private DefaultTableModel reportTableModel;

    // UI Components for input
    private JTextField reportIdField;
    private JTextField reportNameField;
    private JTextField generatedByField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea reportDataArea;

    /**
     * Constructor for ReportPanel.
     * @param reportController The controller responsible for report logic.
     */
    public ReportPanel(ReportController reportController) {
        this.reportController = reportController;
        // The table model needs to be set in the controller that this panel owns.
        this.reportTableModel = new DefaultTableModel(new String[]{"Report ID", "Name", "Generated Date", "Start Date", "End Date", "Generated By", "Data"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.reportController.setTableModel(this.reportTableModel); // Set the table model in the controller

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshReportTable(); // Initial load of data
    }

    /**
     * Initializes the UI components for the Report panel.
     */
    private void initComponents() {
        // Table Setup
        JTable reportTable = new JTable(reportTableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input Fields Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Report Details"));

        reportIdField = new JTextField(20);
        reportIdField.setEditable(false);
        reportNameField = new JTextField(20);
        generatedByField = new JTextField(20);
        startDateField = new JTextField(20);
        endDateField = new JTextField(20);
        reportDataArea = new JTextArea(5, 30);
        reportDataArea.setLineWrap(true);
        reportDataArea.setWrapStyleWord(true);
        JScrollPane reportDataScrollPane = new JScrollPane(reportDataArea);

        inputPanel.add(new JLabel("Report ID:")); inputPanel.add(reportIdField);
        inputPanel.add(new JLabel("Report Name:")); inputPanel.add(reportNameField);
        inputPanel.add(new JLabel("Generated By:")); inputPanel.add(generatedByField);
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD, optional):")); inputPanel.add(startDateField);
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD, optional):")); inputPanel.add(endDateField);
        inputPanel.add(new JLabel("Report Data/Summary:")); inputPanel.add(reportDataScrollPane);

        add(inputPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton generateButton = new JButton("Generate Report");
        JButton deleteButton = new JButton("Delete Report");
        JButton clearButton = new JButton("Clear Fields");
        JButton refreshButton = new JButton("Refresh Table");

        buttonPanel.add(generateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        generateButton.addActionListener(e -> generateReportAction());
        deleteButton.addActionListener(e -> deleteReportAction());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> refreshReportTable());

        // Table Selection Listener
        reportTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && reportTable.getSelectedRow() != -1) {
                populateFieldsFromTable(reportTable.getSelectedRow());
            }
        });
    }

    /**
     * Handles the action for generating a new report.
     */
    private void generateReportAction() {
        try {
            Date startDate = null;
            Date endDate = null;
            if (!startDateField.getText().isEmpty()) {
                startDate = Date.valueOf(startDateField.getText());
            }
            if (!endDateField.getText().isEmpty()) {
                endDate = Date.valueOf(endDateField.getText());
            }

            boolean success = reportController.generateReport(
                    reportNameField.getText(),
                    startDate,
                    endDate,
                    generatedByField.getText(),
                    reportDataArea.getText()
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Report generated and saved!");
                clearFields();
                refreshReportTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate report.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the action for deleting a report.
     */
    private void deleteReportAction() {
        String reportIdToDelete = reportIdField.getText();
        if (reportIdToDelete.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a report to delete or enter its ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete report " + reportIdToDelete + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = reportController.deleteReport(reportIdToDelete);
            if (success) {
                JOptionPane.showMessageDialog(this, "Report deleted successfully!");
                clearFields();
                refreshReportTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete report.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Populates input fields from the selected table row.
     * @param selectedRow The index of the selected row.
     */
    private void populateFieldsFromTable(int selectedRow) {
        reportIdField.setText((String) reportTableModel.getValueAt(selectedRow, 0));
        reportNameField.setText((String) reportTableModel.getValueAt(selectedRow, 1));

        Object startDateObj = reportTableModel.getValueAt(selectedRow, 3);
        startDateField.setText(startDateObj != null ? startDateObj.toString() : "");

        Object endDateObj = reportTableModel.getValueAt(selectedRow, 4);
        endDateField.setText(endDateObj != null ? endDateObj.toString() : "");

        Object generatedByObj = reportTableModel.getValueAt(selectedRow, 5);
        generatedByField.setText(generatedByObj != null ? generatedByObj.toString() : "");

        reportDataArea.setText((String) reportTableModel.getValueAt(selectedRow, 6));
    }

    /**
     * Clears all input fields on the Report panel.
     */
    private void clearFields() {
        reportIdField.setText("");
        reportNameField.setText("");
        generatedByField.setText("");
        startDateField.setText("");
        endDateField.setText("");
        reportDataArea.setText("");
    }

    /**
     * Refreshes the report table by fetching data from the controller.
     * This method is public so MainApplication can call it when the tab is selected.
     */
    public void refreshReportTable() {
        reportController.refreshReportTable();
    }
}
