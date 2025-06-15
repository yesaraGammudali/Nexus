// dao/ReportDAO.java
package DAO;

import Model.Report;
import util.DatabaseConnection; // Ensure this is correct based on your setup

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Data Access Object (DAO) for Report entities.
 * Handles all database operations related to reports.
 */
public class ReportDAO {

    /**
     * Adds a new report to the database.
     * @param report The Report object to add.
     * @return true if the report was added successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean addReport(Report report) throws SQLException {
        String sql = "INSERT INTO reports (report_id, report_name, generated_date, start_date, end_date, generated_by, report_data, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, report.getReportId());
            pstmt.setString(2, report.getReportName());
            pstmt.setTimestamp(3, report.getGeneratedDate());
            pstmt.setDate(4, report.getStartDate());
            pstmt.setDate(5, report.getEndDate());
            pstmt.setString(6, report.getGeneratedBy());
            pstmt.setString(7, report.getReportData());
            pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }

    /**
     * Retrieves a report by its ID.
     * @param reportId The ID of the report to retrieve.
     * @return The Report object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public Report getReportById(String reportId) throws SQLException {
        String sql = "SELECT * FROM reports WHERE report_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Report report = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reportId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                report = new Report();
                report.setReportId(rs.getString("report_id"));
                report.setReportName(rs.getString("report_name"));
                report.setGeneratedDate(rs.getTimestamp("generated_date"));
                report.setStartDate(rs.getDate("start_date"));
                report.setEndDate(rs.getDate("end_date"));
                report.setGeneratedBy(rs.getString("generated_by"));
                report.setReportData(rs.getString("report_data"));
                report.setCreatedAt(rs.getTimestamp("created_at"));
                report.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
        return report;
    }

    /**
     * Retrieves all reports from the database.
     * @return A list of Report objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Report report = new Report();
                report.setReportId(rs.getString("report_id"));
                report.setReportName(rs.getString("report_name"));
                report.setGeneratedDate(rs.getTimestamp("generated_date"));
                report.setStartDate(rs.getDate("start_date"));
                report.setEndDate(rs.getDate("end_date"));
                report.setGeneratedBy(rs.getString("generated_by"));
                report.setReportData(rs.getString("report_data"));
                report.setCreatedAt(rs.getTimestamp("created_at"));
                report.setUpdatedAt(rs.getTimestamp("updated_at"));
                reports.add(report);
            }
        } finally {
            DatabaseConnection.closeConnection(rs);
            DatabaseConnection.closeConnection(stmt);
            DatabaseConnection.closeConnection(conn);
        }
        return reports;
    }

    /**
     * Deletes a report from the database.
     * @param reportId The ID of the report to delete.
     * @return true if the report was deleted successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteReport(String reportId) throws SQLException { // <-- THIS IS THE METHOD
        String sql = "DELETE FROM reports WHERE report_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reportId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            DatabaseConnection.closeConnection(pstmt);
            DatabaseConnection.closeConnection(conn);
        }
    }
}
