package models;

import java.sql.Timestamp;

/**
 * Request Model - Represents NGO requests for food listings
 */
public class Request {
    
    private int requestId;
    private int listingId;
    private String ngoFssai;
    private String ngoName; // For display purposes
    private String status; // accepted, declined, completed
    private Timestamp requestedAt;
    private String notes;
    
    // Additional fields for display
    private String foodName;
    private String restaurantName;
    private String quantity;
    
    // Constructors
    public Request() {}
    
    public Request(int listingId, String ngoFssai, String status) {
        this.listingId = listingId;
        this.ngoFssai = ngoFssai;
        this.status = status;
    }
    
    public Request(int listingId, String ngoFssai, String status, String notes) {
        this.listingId = listingId;
        this.ngoFssai = ngoFssai;
        this.status = status;
        this.notes = notes;
    }
    
    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }
    
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }
    
    public int getListingId() {
        return listingId;
    }
    
    public void setListingId(int listingId) {
        this.listingId = listingId;
    }
    
    public String getNgoFssai() {
        return ngoFssai;
    }
    
    public void setNgoFssai(String ngoFssai) {
        this.ngoFssai = ngoFssai;
    }
    
    public String getNgoName() {
        return ngoName;
    }
    
    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(Timestamp requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getFoodName() {
        return foodName;
    }
    
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    
    public String getRestaurantName() {
        return restaurantName;
    }
    
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public boolean isAccepted() {
        return "accepted".equalsIgnoreCase(status);
    }
    
    public boolean isDeclined() {
        return "declined".equalsIgnoreCase(status);
    }
    
    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return "Request{" +
                "requestId=" + requestId +
                ", listingId=" + listingId +
                ", ngoFssai='" + ngoFssai + '\'' +
                ", status='" + status + '\'' +
                ", requestedAt=" + requestedAt +
                '}';
    }
}