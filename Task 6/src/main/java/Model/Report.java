// model/Report.java
package Model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a report generated within the FastTrack Logistics system.
 * This POJO corresponds directly to the 'Reports' table in the database.
 */
public class Report {

    private String reportId;
    private String reportName;
    private Timestamp generatedDate;
    private Date startDate; // Can be null
    private Date endDate; // Can be null
    private String generatedBy;
    private String reportData; // Actual content or summary of the report
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public Report() {
    }

    /**
     * Parameterized constructor for creating a Report object with all fields.
     *
     * @param reportId Unique identifier for the report.
     * @param reportName Name or title of the report.
     * @param generatedDate Timestamp when the report was generated.
     * @param startDate The start date for the data included in the report (can be null).
     * @param endDate The end date for the data included in the report (can be null).
     * @param generatedBy The user or system that generated the report.
     * @param reportData The actual data content or summary of the report.
     * @param createdAt Timestamp when this record was created.
     * @param updatedAt Timestamp when this record was last updated.
     */
    public Report(String reportId, String reportName, Timestamp generatedDate,
                  Date startDate, Date endDate, String generatedBy, String reportData,
                  Timestamp createdAt, Timestamp updatedAt) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.generatedDate = generatedDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedBy = generatedBy;
        this.reportData = reportData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // NEW CONSTRUCTOR ADDED: Matches the 7 arguments from ReportController's generateReport
    /**
     * Parameterized constructor for creating a Report object,
     * typically used for generation where createdAt/updatedAt timestamps are
     * handled by the database or implicitly.
     *
     * @param reportId Unique identifier for the report.
     * @param reportName Name or title of the report.
     * @param generatedDate Timestamp when the report was generated.
     * @param startDate The start date for the data included in the report (can be null).
     * @param endDate The end date for the data included in the report (can be null).
     * @param generatedBy The user or system that generated the report.
     * @param reportData The actual data content or summary of the report.
     */
    public Report(String reportId, String reportName, Timestamp generatedDate,
                  Date startDate, Date endDate, String generatedBy, String reportData) {
        this.reportId = reportId;
        this.reportName = reportName;
        this.generatedDate = generatedDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.generatedBy = generatedBy;
        this.reportData = reportData;
        this.createdAt = null; // Set to null, expecting DAO/DB to handle actual timestamp
        this.updatedAt = null; // Set to null, expecting DAO/DB to handle actual timestamp
    }

    // --- Getters ---
    public String getReportId() {
        return reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public Timestamp getGeneratedDate() {
        return generatedDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public String getReportData() {
        return reportData;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setGeneratedDate(Timestamp generatedDate) {
        this.generatedDate = generatedDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the Report object.
     * @return A string containing all report details.
     */
    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", reportName='" + reportName + '\'' +
                ", generatedDate=" + generatedDate +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", generatedBy='" + generatedBy + '\'' +
                ", reportData='" + reportData + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

