package services;

import db.FoodListingDAO;
import db.RequestDAO;
import models.FoodListing;
import models.User;
import java.sql.Timestamp;
import java.util.List;

/**
 * Food Listing Service
 * Handles business logic for food listings
 */
public class ListingService {
    
    private FoodListingDAO listingDAO;
    private RequestDAO requestDAO;
    
    public ListingService() {
        this.listingDAO = new FoodListingDAO();
        this.requestDAO = new RequestDAO();
    }
    
    /**
     * Create new food listing
     * @return Error message if failed, null if successful
     */
    public String createListing(User restaurant, String foodName, String quantity, 
                                String description, Timestamp expiryTime) {
        // Validate restaurant
        if (restaurant == null || !restaurant.isRestaurant()) {
            return "Only restaurants can create food listings";
        }
        
        // Validate food name
        if (foodName == null || foodName.trim().isEmpty()) {
            return "Food name is required";
        }
        
        foodName = foodName.trim();
        if (foodName.length() < 3) {
            return "Food name must be at least 3 characters";
        }
        
        if (foodName.length() > 200) {
            return "Food name is too long (max 200 characters)";
        }
        
        // Validate quantity
        if (quantity == null || quantity.trim().isEmpty()) {
            return "Quantity is required";
        }
        
        quantity = quantity.trim();
        if (quantity.length() > 100) {
            return "Quantity description is too long (max 100 characters)";
        }
        
        // Validate description (optional but limit length)
        if (description != null && description.trim().length() > 1000) {
            return "Description is too long (max 1000 characters)";
        }
        
        // Validate expiry time
        if (expiryTime == null) {
            return "Expiry time is required";
        }
        
        // Check if expiry time is in the future
        if (expiryTime.before(new Timestamp(System.currentTimeMillis()))) {
            return "Expiry time must be in the future";
        }
        
        // Check if expiry time is reasonable (not more than 24 hours)
        long timeDiff = expiryTime.getTime() - System.currentTimeMillis();
        long hours = timeDiff / (60 * 60 * 1000);
        if (hours > 24) {
            return "Expiry time cannot be more than 24 hours from now";
        }
        
        // Create listing
        FoodListing listing = new FoodListing(
            restaurant.getFssaiNumber(),
            foodName,
            quantity,
            description != null ? description.trim() : "",
            expiryTime
        );
        
        boolean created = listingDAO.createListing(listing);
        if (!created) {
            return "Failed to create listing. Please try again.";
        }
        
        System.out.println("✓ Listing created: " + foodName);
        return null; // Success
    }
    
    /**
     * Get all listings for a restaurant
     */
    public List<FoodListing> getRestaurantListings(String restaurantFssai) {
        return listingDAO.getListingsByRestaurant(restaurantFssai);
    }
    
    /**
     * Get all available listings (for NGOs)
     */
    public List<FoodListing> getAvailableListings() {
        // Mark expired listings first
        listingDAO.markExpiredListings();
        
        return listingDAO.getAllAvailableListings();
    }
    
    /**
     * Get listing by ID
     */
    public FoodListing getListingById(int listingId) {
        return listingDAO.getListingById(listingId);
    }
    
    /**
     * Cancel a listing (Restaurant only)
     * @return Error message if failed, null if successful
     */
    public String cancelListing(User restaurant, int listingId) {
        // Validate restaurant
        if (restaurant == null || !restaurant.isRestaurant()) {
            return "Only restaurants can cancel listings";
        }
        
        // Get listing
        FoodListing listing = listingDAO.getListingById(listingId);
        if (listing == null) {
            return "Listing not found";
        }
        
        // Check ownership
        if (!listing.getRestaurantFssai().equals(restaurant.getFssaiNumber())) {
            return "You can only cancel your own listings";
        }
        
        // Check if already accepted
        if (listing.isAccepted()) {
            return "Cannot cancel - listing already accepted by an NGO";
        }
        
        // Update status to cancelled
        boolean updated = listingDAO.updateListingStatus(listingId, "cancelled");
        if (!updated) {
            return "Failed to cancel listing. Please try again.";
        }
        
        System.out.println("✓ Listing cancelled: " + listingId);
        return null; // Success
    }
    
    /**
     * Get restaurant statistics
     */
    public int[] getRestaurantStats(String restaurantFssai) {
        return listingDAO.getRestaurantStats(restaurantFssai);
    }
    
    /**
     * Check if listing can be accepted (is available and not expired)
     */
    public boolean canAcceptListing(int listingId) {
        FoodListing listing = listingDAO.getListingById(listingId);
        if (listing == null) {
            return false;
        }
        
        // Check if available
        if (!listing.isAvailable()) {
            return false;
        }
        
        // Check if expired
        if (listing.getExpiryTime().before(new Timestamp(System.currentTimeMillis()))) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Refresh listings (mark expired ones)
     */
    public int refreshListings() {
        return listingDAO.markExpiredListings();
    }
    
    /**
     * Validate expiry hours input
     */
    public static String validateExpiryHours(String hoursStr) {
        if (hoursStr == null || hoursStr.trim().isEmpty()) {
            return "Expiry hours is required";
        }
        
        try {
            double hours = Double.parseDouble(hoursStr.trim());
            
            if (hours <= 0) {
                return "Expiry hours must be greater than 0";
            }
            
            if (hours > 24) {
                return "Expiry hours cannot exceed 24 hours";
            }
            
            return null; // Valid
        } catch (NumberFormatException e) {
            return "Please enter a valid number for hours";
        }
    }
    
    /**
     * Convert hours to Timestamp
     */
    public static Timestamp hoursToTimestamp(double hours) {
        long milliseconds = (long) (hours * 60 * 60 * 1000);
        return new Timestamp(System.currentTimeMillis() + milliseconds);
    }
}