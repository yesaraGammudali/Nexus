// model/DeliveryPersonnel.java
package Model;

import java.sql.Timestamp;

/**
 * Represents a delivery personnel (driver or courier) within the FastTrack Logistics system.
 * This POJO corresponds directly to the 'DeliveryPersonnel' table in the database.
 */
public class DeliveryPersonnel {

    private String personnelId;
    private String name;
    private String contactNumber;
    private String email;
    private String vehicleDetails;
    private String availabilityStatus; // e.g., 'Available', 'On Duty', 'Off Duty', 'On Leave'
    private String currentRoute;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /**
     * Default constructor.
     */
    public DeliveryPersonnel() {
    }

    /**
     * Parameterized constructor for creating a DeliveryPersonnel object with all fields.
     *
     * @param personnelId Unique identifier for the personnel.
     * @param name Full name of the personnel.
     * @param contactNumber Contact phone number.
     * @param email Email address.
     * @param vehicleDetails Details of the vehicle used (e.g., "Van - ABC 123").
     * @param availabilityStatus Current availability status.
     * @param currentRoute Current assigned route (can be null).
     * @param createdAt Timestamp when this record was created.
     * @param updatedAt Timestamp when this record was last updated.
     */
    public DeliveryPersonnel(String personnelId, String name, String contactNumber, String email,
                             String vehicleDetails, String availabilityStatus, String currentRoute,
                             Timestamp createdAt, Timestamp updatedAt) {
        this.personnelId = personnelId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.vehicleDetails = vehicleDetails;
        this.availabilityStatus = availabilityStatus;
        this.currentRoute = currentRoute;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Parameterized constructor for creating a DeliveryPersonnel object,
     * typically used for initial creation or updates where createdAt/updatedAt
     * are handled by the database or implicitly.
     *
     * @param personnelId Unique identifier for the personnel.
     * @param name Full name of the personnel.
     * @param contactNumber Contact phone number.
     * @param email Email address.
     * @param vehicleDetails Details of the vehicle used (e.g., "Van - ABC 123").
     * @param availabilityStatus Current availability status.
     * @param currentRoute Current assigned route (can be null).
     */
    public DeliveryPersonnel(String personnelId, String name, String contactNumber, String email,
                             String vehicleDetails, String availabilityStatus, String currentRoute) {
        this.personnelId = personnelId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.vehicleDetails = vehicleDetails;
        this.availabilityStatus = availabilityStatus;
        this.currentRoute = currentRoute;
        this.createdAt = null; // Set to null, expecting DAO/DB to handle actual timestamp
        this.updatedAt = null; // Set to null, expecting DAO/DB to handle actual timestamp
    }


    // --- Getters ---
    public String getPersonnelId() {
        return personnelId;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns a string representation of the DeliveryPersonnel object.
     * @return A string containing all personnel details.
     */
    @Override
    public String toString() {
        return "DeliveryPersonnel{" +
                "personnelId='" + personnelId + '\'' +
                ", name='" + name + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", vehicleDetails='" + vehicleDetails + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                ", currentRoute='" + currentRoute + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
