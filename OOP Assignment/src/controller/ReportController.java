// controller/ReportController.java
package controller;

import DAO.ReportDAO;
import Model.Report;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp; // Needed for new Timestamp(System.currentTimeMillis())

/**
 * Controller for managing Report-related operations.
 * This class handles generating and deleting reports.
 */
public class ReportController {

    private ReportDAO reportDAO;
    private DefaultTableModel reportTableModel; // Can be null initially, set by the view

    /**
     * Constructor for ReportController.
     * @param reportDAO The DAO for Report operations.
     * @param reportTableModel The table model for displaying reports in the UI (can be null if set later).
     */
    public ReportController(ReportDAO reportDAO, DefaultTableModel reportTableModel) {
        this.reportDAO = reportDAO;
        this.reportTableModel = reportTableModel;
    }

    /**
     * Sets the table model for this controller. This is useful when the table model
     * is created by the view panel and then passed to the controller.
     * @param reportTableModel The DefaultTableModel to use.
     */
    public void setTableModel(DefaultTableModel reportTableModel) {
        this.reportTableModel = reportTableModel;
    }

    /**
     * Generates and adds a new report record.
     * @param reportName The name of the report.
     * @param startDate The start date for the report data (can be null).
     * @param endDate The end date for the report data (can be null).
     * @param generatedBy The entity or user who generated the report.
     * @param reportData The actual data or summary of the report.
     * @return true if the report was successfully generated and added, false otherwise.
     */
    public boolean generateReport(String reportName, Date startDate, Date endDate,
                                  String generatedBy, String reportData) {
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        try {
            // FIXED: Explicitly cast nullable Date arguments for constructor
            Report report = new Report(reportId, reportName, new Timestamp(System.currentTimeMillis()),
                    (Date) startDate, (Date) endDate, generatedBy, reportData);
            boolean success = reportDAO.addReport(report);
            if (success) {
                // No table refresh here; handled by panel's refreshReportTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error generating report: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a report record from the system.
     * @param reportId The ID of the report to delete.
     * @return true if the report was successfully deleted, false otherwise.
     */
    public boolean deleteReport(String reportId) {
        try {
            boolean success = reportDAO.deleteReport(reportId);
            if (success) {
                // No table refresh here; handled by panel's refreshReportTable
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error deleting report: " + e.getMessage());
            return false;
        }
    }

    /**
     * Refreshes the report table in the UI by fetching all reports from the database.
     */
    public void refreshReportTable() {
        if (reportTableModel == null) { // Defensive check
            System.err.println("ReportTableModel is null in controller. Cannot refresh table.");
            return;
        }
        try {
            List<Report> reports = reportDAO.getAllReports();
            // Clear existing data from the table model
            reportTableModel.setRowCount(0);
            // Add new data
            for (Report report : reports) {
                reportTableModel.addRow(new Object[]{
                        report.getReportId(),
                        report.getReportName(),
                        report.getGeneratedDate(),
                        report.getStartDate(),
                        report.getEndDate(),
                        report.getGeneratedBy(),
                        report.getReportData()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing report table: " + e.getMessage());
        }
    }
}
