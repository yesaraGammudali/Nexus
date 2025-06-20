// controller/ReportController.java
package controller;

import DAO.ReportDAO;
import DAO.DeliveryDAO; // For delivery performance metrics
import DAO.ShipmentDAO; // For shipment volume metrics
import Model.Report;
import Model.Delivery;
import Model.Shipment;

import javax.swing.table.DefaultTableModel;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period; // For date difference
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.text.DecimalFormat; // For formatting percentages and averages

/**
 * Controller for managing Report-related operations.
 * This class handles generating and deleting structured reports based on database data.
 */
public class ReportController {

    private ReportDAO reportDAO;
    private DeliveryDAO deliveryDAO;
    private ShipmentDAO shipmentDAO;
    private DefaultTableModel reportTableModel;

    /**
     * Constructor for ReportController.
     * @param reportDAO The DAO for Report operations.
     * @param deliveryDAO The DAO for Delivery operations (for performance metrics).
     * @param shipmentDAO The DAO for Shipment operations (for volume metrics).
     * @param reportTableModel The table model for displaying reports in the UI (can be null if set later).
     */
    public ReportController(ReportDAO reportDAO, DeliveryDAO deliveryDAO, ShipmentDAO shipmentDAO, DefaultTableModel reportTableModel) {
        this.reportDAO = reportDAO;
        this.deliveryDAO = deliveryDAO;
        this.shipmentDAO = shipmentDAO;
        this.reportTableModel = reportTableModel;
    }

    /**
     * Sets the table model for this controller.
     * @param reportTableModel The DefaultTableModel to use.
     */
    public void setTableModel(DefaultTableModel reportTableModel) {
        this.reportTableModel = reportTableModel;
    }

    /**
     * Generates a structured report based on selected month/year and saves it.
     * @param reportName The name of the report.
     * @param month The month for the report period (1-12).
     * @param year The year for the report period.
     * @param generatedBy The user generating the report.
     * @return The formatted report content string.
     */
    public String generateStructuredReport(String reportName, int month, int year, String generatedBy) {
        String reportContent = "";
        String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LocalDate periodStart = LocalDate.of(year, month, 1);
        LocalDate periodEnd = periodStart.plusMonths(1).minusDays(1);

        Date startDate = Date.valueOf(periodStart);
        Date endDate = Date.valueOf(periodEnd);

        try {
            // Fetch all data and filter in Java for simplicity (avoids complex SQL for ranges)
            List<Delivery> allDeliveries = deliveryDAO.getAllDeliveries();
            List<Shipment> allShipments = shipmentDAO.getAllShipments();

            // Filter data for the specified period
            List<Delivery> periodDeliveries = allDeliveries.stream()
                    .filter(d -> d.getActualDeliveryDate() != null) // Only consider completed deliveries
                    .filter(d -> {
                        LocalDate actualDate = d.getActualDeliveryDate().toLocalDate();
                        return !actualDate.isBefore(periodStart) && !actualDate.isAfter(periodEnd);
                    })
                    .collect(Collectors.toList());

            List<Shipment> periodShipments = allShipments.stream()
                    .filter(s -> s.getCreatedAt() != null) // Assuming created_at determines "processed" within period
                    .filter(s -> {
                        LocalDate createdAtDate = s.getCreatedAt().toLocalDateTime().toLocalDate();
                        return !createdAtDate.isBefore(periodStart) && !createdAtDate.isAfter(periodEnd);
                    })
                    .collect(Collectors.toList());

            // --- Generate Report Sections ---
            StringBuilder reportBuilder = new StringBuilder();
            DecimalFormat df = new DecimalFormat("0.00");

            reportBuilder.append("--- Monthly Logistics Report (").append(String.format("%02d", month)).append("/").append(year).append(") ---\n\n");

            // 1. Delivery Performance
            reportBuilder.append("Delivery Performance\n");
            long totalDeliveriesCompleted = periodDeliveries.stream()
                    .filter(d -> "Successful".equalsIgnoreCase(d.getDeliveryOutcome()))
                    .count();
            long onTimeDeliveries = periodDeliveries.stream()
                    .filter(d -> "Successful".equalsIgnoreCase(d.getDeliveryOutcome()))
                    .filter(d -> {
                        if (d.getActualDeliveryDate() != null && d.getScheduledDeliveryDate() != null) {
                            return d.getActualDeliveryDate().toLocalDate().isBefore(d.getScheduledDeliveryDate().toLocalDate()) ||
                                    d.getActualDeliveryDate().toLocalDate().isEqual(d.getScheduledDeliveryDate().toLocalDate());
                        }
                        return false;
                    })
                    .count();

            double onTimeDeliveryRate = (totalDeliveriesCompleted > 0) ? ((double) onTimeDeliveries / totalDeliveriesCompleted) * 100 : 0.00;
            reportBuilder.append("  - On-time Delivery Rate: ").append(df.format(onTimeDeliveryRate)).append(" %\n");

            // Calculate Average Delivery Time (for successful deliveries)
            // Using Duration for more precise time differences (assuming actual/scheduled dates have time components if using TIMESTAMP)
            // If only DATE is stored, then this calculation is less precise, just using days.
            long totalDurationSeconds = 0;
            long deliveriesWithTimes = 0;
            for (Delivery d : periodDeliveries) {
                if ("Successful".equalsIgnoreCase(d.getDeliveryOutcome()) &&
                        d.getScheduledDeliveryDate() != null && d.getEstimatedDeliveryTime() != null &&
                        d.getActualDeliveryDate() != null && d.getActualDeliveryTime() != null) {

                    LocalDateTime scheduledDateTime = LocalDateTime.of(d.getScheduledDeliveryDate().toLocalDate(), d.getEstimatedDeliveryTime().toLocalTime());
                    LocalDateTime actualDateTime = LocalDateTime.of(d.getActualDeliveryDate().toLocalDate(), d.getActualDeliveryTime().toLocalTime());

                    if (actualDateTime.isAfter(scheduledDateTime)) { // Only count positive duration for 'delivery time'
                        totalDurationSeconds += Duration.between(scheduledDateTime, actualDateTime).getSeconds();
                        deliveriesWithTimes++;
                    }
                }
            }
            double averageDeliveryTimeHours = (deliveriesWithTimes > 0) ? (double)totalDurationSeconds / deliveriesWithTimes / 3600.0 : 0.00; // Convert seconds to hours
            reportBuilder.append("  - Average Delivery Time (Hours): ").append(df.format(averageDeliveryTimeHours)).append("\n");
            reportBuilder.append("  - Total Deliveries Completed: ").append(totalDeliveriesCompleted).append("\n\n");

            // 2. Customer Satisfaction (Proxy)
            reportBuilder.append("Customer Satisfaction (Proxy)\n");
            double deliveriesWithoutDelaysRate = onTimeDeliveryRate; // Using same metric as on-time for proxy
            reportBuilder.append("  - Deliveries Without Delays: ").append(df.format(deliveriesWithoutDelaysRate)).append(" %\n\n");

            // 3. Shipment Volumes
            reportBuilder.append("Shipment Volumes\n");
            reportBuilder.append("  - Total Shipments Processed: ").append(periodShipments.size()).append("\n");

            // Volume by Package Type (based on simple keywords in package_contents)
            Map<String, Long> volumeByPackageType = periodShipments.stream()
                    .map(s -> {
                        String contents = s.getPackageContents() != null ? s.getPackageContents().toLowerCase() : "";
                        if (contents.contains("parcel") || contents.contains("box")) return "Parcels";
                        if (contents.contains("document") || contents.contains("letter")) return "Documents/Letters";
                        if (contents.contains("fragile")) return "Fragile Items";
                        return "Other";
                    })
                    .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
            reportBuilder.append("  - Volume by Package Type:\n");
            volumeByPackageType.forEach((type, count) -> reportBuilder.append("    - ").append(type).append(": ").append(count).append("\n"));
            reportBuilder.append("\n");

            // Volume by Route (based on Shipment.current_location as a proxy)
            Map<String, Long> volumeByRoute = periodShipments.stream()
                    .filter(s -> s.getCurrentLocation() != null && !s.getCurrentLocation().trim().isEmpty())
                    .collect(Collectors.groupingBy(Shipment::getCurrentLocation, Collectors.counting()));
            reportBuilder.append("  - Volume by Route:\n");
            if (volumeByRoute.isEmpty()) {
                reportBuilder.append("    - No specific routes recorded for this period.\n");
            } else {
                volumeByRoute.forEach((location, count) -> reportBuilder.append("    - ").append(location).append(": ").append(count).append("\n"));
            }
            reportBuilder.append("\n");


            reportContent = reportBuilder.toString();

            // Save the generated report
            Report report = new Report(reportId, reportName, new Timestamp(System.currentTimeMillis()),
                    startDate, endDate, generatedBy, "Monthly Logistics", reportContent); // "Monthly Logistics" as report type
            boolean success = reportDAO.addReport(report);
            if (!success) {
                System.err.println("Failed to save report to database after generation.");
                return "Report generated, but failed to save to database.";
            }

        } catch (SQLException e) {
            System.err.println("Database error during report generation: " + e.getMessage());
            e.printStackTrace();
            return "Error retrieving data for report: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during report generation: " + e.getMessage());
            e.printStackTrace();
            return "An unexpected error occurred: " + e.getMessage();
        }

        return reportContent;
    }

    /**
     * Deletes a report record from the system.
     * @param reportId The ID of the report to delete.
     * @return true if the report was successfully deleted, false otherwise.
     */
    public boolean deleteReport(String reportId) {
        try {
            boolean success = reportDAO.deleteReport(reportId);
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
        if (reportTableModel == null) {
            System.err.println("ReportTableModel is null in controller. Cannot refresh table.");
            return;
        }
        try {
            List<Report> reports = reportDAO.getAllReports();
            reportTableModel.setRowCount(0);
            for (Report report : reports) {
                // Adjusting columns for the new table model
                reportTableModel.addRow(new Object[]{
                        report.getReportId(),
                        report.getReportName(),
                        report.getGeneratedDate(),
                        (report.getStartDate() != null && report.getEndDate() != null) ?
                                report.getStartDate().toLocalDate().getMonthValue() + "/" + report.getStartDate().toLocalDate().getYear() : "N/A",
                        report.getGeneratedBy(),
                        report.getReportData()
                });
            }
        } catch (SQLException e) {
            System.err.println("Error refreshing report table: " + e.getMessage());
        }
    }
}
