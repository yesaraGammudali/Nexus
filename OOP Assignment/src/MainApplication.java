// MainApplication.java
// This file will be placed directly under the 'src' directory.

import DAO.ShipmentDAO;
import DAO.DeliveryPersonnelDAO;
import DAO.NotificationDAO;
import DAO.DeliveryDAO;
import DAO.ReportDAO;

import controller.ShipmentController;
import controller.DeliveryPersonnelController;
import controller.NotificationController;
import controller.DeliveryController;
import controller.ReportController;

// Import all 8 new JPanel view classes
import view.ShipmentPanel;
import view.PersonnelPanel;
import view.ScheduleDeliveriesPanel;
import view.TrackShipmentsPanel;
import view.AssignDriversPanel;
import view.ReportPanel;
import view.CustomerNotificationsPanel;
import view.PersonnelNotificationsPanel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException; // For handling potential SQL exceptions from DAOs
// import javax.swing.table.DefaultTableModel; // This import is not used here, only in panels, so removed to clean up

/**
 * Main application class for the FastTrack Logistics Management System.
 * This class sets up the main JFrame, initializes DAOs and Controllers,
 * and organizes the various UI panels (Views) into an 8-tab JTabbedPane
 * to align with project task divisions.
 */
public class MainApplication extends JFrame {

    // --- DAO Instances ---
    private ShipmentDAO shipmentDAO;
    private DeliveryPersonnelDAO personnelDAO;
    private NotificationDAO notificationDAO;
    private DeliveryDAO deliveryDAO;
    private ReportDAO reportDAO;

    // --- View Panel Instances ---
    private ShipmentPanel shipmentPanel;
    private PersonnelPanel personnelPanel;
    private ScheduleDeliveriesPanel scheduleDeliveriesPanel;
    private TrackShipmentsPanel trackShipmentsPanel;
    private AssignDriversPanel assignDriversPanel;
    private ReportPanel reportPanel;
    private CustomerNotificationsPanel customerNotificationsPanel;
    private PersonnelNotificationsPanel personnelNotificationsPanel;

    // --- Main UI Component ---
    private JTabbedPane tabbedPane;

    /**
     * Constructor for the MainApplication.
     * Initializes DAOs, and sets up the main UI frame with all 8 panels.
     */
    public MainApplication() {
        setTitle("FastTrack Logistics Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800); // Increased size to accommodate more tabs and content
        setLocationRelativeTo(null); // Center the window

        // Initialize DAOs
        try {
            shipmentDAO = new ShipmentDAO();
            personnelDAO = new DeliveryPersonnelDAO();
            notificationDAO = new NotificationDAO();
            deliveryDAO = new DeliveryDAO();
            reportDAO = new ReportDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize DAOs. Database connection issue or driver not found: " + e.getMessage(),
                    "Initialization Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1); // Exit if DAOs cannot be initialized
        }

        initComponents();
    }

    /**
     * Initializes the UI components, including the 8 panels.
     * Each panel is now instantiated, and DAOs are passed to them
     * so they can manage their own controllers and UI logic.
     */
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // Instantiate all 8 Panels, passing necessary DAOs.
        // Each Panel creates its own DefaultTableModel and Controller internally,
        // and then sets that table model in its controller.

        shipmentPanel = new ShipmentPanel(new ShipmentController(shipmentDAO, null)); // TableModel set inside panel
        tabbedPane.addTab("Manage Shipments", shipmentPanel);

        personnelPanel = new PersonnelPanel(new DeliveryPersonnelController(personnelDAO, null)); // TableModel set inside panel
        tabbedPane.addTab("Manage Personnel", personnelPanel);

        scheduleDeliveriesPanel = new ScheduleDeliveriesPanel(new DeliveryController(deliveryDAO, shipmentDAO, personnelDAO, null), shipmentDAO, personnelDAO); // TableModel set inside panel
        tabbedPane.addTab("Schedule Deliveries", scheduleDeliveriesPanel);

        trackShipmentsPanel = new TrackShipmentsPanel(new ShipmentController(shipmentDAO, null)); // TableModel set inside panel
        tabbedPane.addTab("Track Shipments", trackShipmentsPanel);

        // AssignDriversPanel instantiation - KEPT AS IS FROM YOUR PROVIDED CODE
        // since you cannot change its constructor in the submitted assignment.
        assignDriversPanel = new AssignDriversPanel(
                new ShipmentController(shipmentDAO, null),
                new DeliveryPersonnelController(personnelDAO, null),
                shipmentDAO,
                personnelDAO
        );
        tabbedPane.addTab("Assign Drivers", assignDriversPanel);

        // FIX APPLIED HERE: ReportController now correctly initialized with all required DAOs
        reportPanel = new ReportPanel(new ReportController(reportDAO, deliveryDAO, shipmentDAO, null));
        tabbedPane.addTab("Reports", reportPanel);

        customerNotificationsPanel = new CustomerNotificationsPanel(new NotificationController(notificationDAO, shipmentDAO, personnelDAO, null), shipmentDAO); // TableModel set inside panel
        tabbedPane.addTab("Customer Notifications", customerNotificationsPanel);

        personnelNotificationsPanel = new PersonnelNotificationsPanel(new NotificationController(notificationDAO, shipmentDAO, personnelDAO, null), personnelDAO); // TableModel set inside panel
        tabbedPane.addTab("Personnel Notifications", personnelNotificationsPanel);


        // Add a ChangeListener to the tabbedPane to refresh relevant panels
        // when a tab is selected. This ensures data is up-to-date.
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(selectedIndex);
            switch (title) {
                case "Manage Shipments":
                    shipmentPanel.refreshShipmentTable();
                    break;
                case "Manage Personnel":
                    personnelPanel.refreshPersonnelTable();
                    break;
                case "Schedule Deliveries":
                    scheduleDeliveriesPanel.refreshComboBoxes();
                    break;
                case "Track Shipments":
                    trackShipmentsPanel.refreshShipmentTable();
                    break;
                case "Assign Drivers":
                    assignDriversPanel.refreshComboBoxes();
                    // Assuming refreshDeliveryTable() exists and is desired on tab switch
                    // If this line causes an error "cannot find symbol", then remove it as it means your submitted AssignDriversPanel doesn't have it.
                    // assignDriversPanel.refreshDeliveryTable();
                    break;
                case "Reports":
                    reportPanel.refreshReportTable();
                    reportPanel.clearFields(); // Now callable as it was made public in ReportPanel
                    break;
                case "Customer Notifications":
                    customerNotificationsPanel.refreshAllNotificationData();
                    break;
                case "Personnel Notifications":
                    personnelNotificationsPanel.refreshAllNotificationData();
                    break;
            }
        });
    }

    /**
     * Main method to run the application.
     * Creates and displays the MainApplication frame.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Ensure that Swing components are created and updated on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainApplication app = new MainApplication();
            app.setVisible(true);
        });
    }
}
