// view/ReportPanel.java
package view;

import controller.ReportController;
import Model.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.stream.IntStream;

/**
 * JPanel for managing Report generation and management (Task: "Reports").
 * This panel provides the UI for generating and deleting structured reports.
 */
public class ReportPanel extends JPanel {

    private ReportController reportController;
    private DefaultTableModel reportTableModel;

    // UI Components for input and display
    private JTextField reportIdField;
    private JTextField reportNameField;
    private JTextField generatedByField;
    private JComboBox<String> monthComboBox; // New: Month selection
    private JComboBox<Integer> yearComboBox; // New: Year selection
    private JTextArea reportDataArea; // Now for displaying generated structured report

    /**
     * Constructor for ReportPanel.
     * @param reportController The controller responsible for report logic.
     */
    public ReportPanel(ReportController reportController) {
        this.reportController = reportController;
        // Table columns updated to reflect report metadata + the data
        this.reportTableModel = new DefaultTableModel(new String[]{"Report ID", "Name", "Generated Date", "Month/Year", "Generated By", "Report Data"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.reportController.setTableModel(this.reportTableModel);

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
        reportTable.getColumnModel().getColumn(5).setPreferredWidth(400); // Make Report Data column wider
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Input Fields Panel (Top part for metadata and period selection)
        JPanel topInputPanel = new JPanel(new GridLayout(3, 4, 10, 10)); // 3 rows, 4 columns
        topInputPanel.setBorder(BorderFactory.createTitledBorder("Report Details and Period Selection"));

        reportIdField = new JTextField(20);
        reportIdField.setEditable(false);
        reportNameField = new JTextField(20);
        generatedByField = new JTextField(20);

        monthComboBox = new JComboBox<>(new String[] {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });
        // Populate years: current year +/- 5 years, adjust as needed
        int currentYear = Year.now().getValue();
        Integer[] years = IntStream.rangeClosed(currentYear - 5, currentYear + 1).boxed().toArray(Integer[]::new);
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem(currentYear); // Set current year as default

        topInputPanel.add(new JLabel("Report ID:")); topInputPanel.add(reportIdField);
        topInputPanel.add(new JLabel("Report Name:")); topInputPanel.add(reportNameField);
        topInputPanel.add(new JLabel("Generated By:")); topInputPanel.add(generatedByField);

        topInputPanel.add(new JLabel("Select Month:")); topInputPanel.add(monthComboBox);
        topInputPanel.add(new JLabel("Select Year:")); topInputPanel.add(yearComboBox);
        // Add empty cells to fill the grid if not using all 4 columns for input
        topInputPanel.add(new JLabel("")); topInputPanel.add(new JLabel(""));


        // Report Display Area (Middle part)
        JPanel reportDisplayPanel = new JPanel(new BorderLayout());
        reportDisplayPanel.setBorder(BorderFactory.createTitledBorder("Generated Report Output"));
        reportDataArea = new JTextArea(15, 60); // Increased rows and columns
        reportDataArea.setLineWrap(true);
        reportDataArea.setWrapStyleWord(true);
        reportDataArea.setEditable(false); // Make it read-only
        JScrollPane reportDataScrollPane = new JScrollPane(reportDataArea);
        reportDisplayPanel.add(reportDataScrollPane, BorderLayout.CENTER);

        // Combine top input and report display panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(topInputPanel, BorderLayout.NORTH);
        northPanel.add(reportDisplayPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);


        // Button Panel (Bottom part)
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
     * Handles the action for generating a new structured report.
     */
    private void generateReportAction() {
        String reportName = reportNameField.getText();
        String generatedBy = generatedByField.getText();
        int month = monthComboBox.getSelectedIndex() + 1; // Month index is 0-11, SQL month is 1-12
        int year = (int) yearComboBox.getSelectedItem();

        if (reportName.isEmpty() || generatedBy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Report Name and Generated By fields cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Generate report content (this now handles all calculations)
            String generatedContent = reportController.generateStructuredReport(
                    reportName,
                    month,
                    year,
                    generatedBy
            );

            if (generatedContent != null && !generatedContent.isEmpty()) {
                reportDataArea.setText(generatedContent);
                JOptionPane.showMessageDialog(this, "Report generated and saved!");
                clearFields();
                refreshReportTable();
            } else {
                reportDataArea.setText("Failed to generate report content.");
                JOptionPane.showMessageDialog(this, "Failed to generate report content.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            reportDataArea.setText("Error during report generation: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error during report generation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
        generatedByField.setText((String) reportTableModel.getValueAt(selectedRow, 4)); // Get from "Generated By" column

        // Extract month and year from "Month/Year" column
        String monthYear = (String) reportTableModel.getValueAt(selectedRow, 3);
        if (monthYear != null && monthYear.contains("/")) {
            try {
                String[] parts = monthYear.split("/");
                monthComboBox.setSelectedIndex(Integer.parseInt(parts[0]) - 1); // Month is 1-indexed in string, 0-indexed in combobox
                yearComboBox.setSelectedItem(Integer.parseInt(parts[1]));
            } catch (NumberFormatException ex) {
                System.err.println("Error parsing month/year from table: " + ex.getMessage());
            }
        } else {
            monthComboBox.setSelectedIndex(0);
            yearComboBox.setSelectedItem(Year.now().getValue());
        }

        reportDataArea.setText((String) reportTableModel.getValueAt(selectedRow, 5)); // Get from "Report Data" column
    }

    /**
     * Clears all input fields on the Report panel.
     */
    public void clearFields() { // CHANGED FROM PRIVATE TO PUBLIC
        reportIdField.setText("");
        reportNameField.setText("");
        generatedByField.setText("");
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1); // Set to current month
        yearComboBox.setSelectedItem(Year.now().getValue()); // Set to current year
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
