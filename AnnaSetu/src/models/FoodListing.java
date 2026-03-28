package models;

import java.sql.Timestamp;

/**
 * FoodListing Model - Represents food items listed by restaurants
 */
public class FoodListing {
    
    private int listingId;
    private String restaurantFssai;
    private String restaurantName; // For display purposes
    private String foodName;
    private String description;
    private String quantity;
    private Timestamp expiryTime;
    private String status; // available, accepted, expired, cancelled
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String acceptedByNGO; // NGO name if accepted
    private String ngoPhone; // NGO phone if accepted
    
    // Constructors
    public FoodListing() {}
    
    public FoodListing(String restaurantFssai, String foodName, String quantity, 
                       String description, Timestamp expiryTime) {
        this.restaurantFssai = restaurantFssai;
        this.foodName = foodName;
        this.quantity = quantity;
        this.description = description;
        this.expiryTime = expiryTime;
        this.status = "available";
    }
    
    // Getters and Setters
    public int getListingId() {
        return listingId;
    }
    
    public void setListingId(int listingId) {
        this.listingId = listingId;
    }
    
    public String getRestaurantFssai() {
        return restaurantFssai;
    }
    
    public void setRestaurantFssai(String restaurantFssai) {
        this.restaurantFssai = restaurantFssai;
    }
    
    public String getRestaurantName() {
        return restaurantName;
    }
    
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    
    public String getFoodName() {
        return foodName;
    }
    
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getQuantity() {
        return quantity;
    }
    
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    
    public Timestamp getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(Timestamp expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getAcceptedByNGO() {
        return acceptedByNGO;
    }
    
    public void setAcceptedByNGO(String acceptedByNGO) {
        this.acceptedByNGO = acceptedByNGO;
    }
    
    public String getNgoPhone() {
        return ngoPhone;
    }
    
    public void setNgoPhone(String ngoPhone) {
        this.ngoPhone = ngoPhone;
    }
    
    public boolean isAvailable() {
        return "available".equalsIgnoreCase(status);
    }
    
    public boolean isAccepted() {
        return "accepted".equalsIgnoreCase(status);
    }
    
    public boolean isExpired() {
        return "expired".equalsIgnoreCase(status);
    }
    
    @Override
    public String toString() {
        return "FoodListing{" +
                "listingId=" + listingId +
                ", foodName='" + foodName + '\'' +
                ", quantity='" + quantity + '\'' +
                ", status='" + status + '\'' +
                ", expiryTime=" + expiryTime +
                '}';
    }
}