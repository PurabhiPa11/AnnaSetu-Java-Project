package services;

import db.RequestDAO;
import db.FoodListingDAO;
import models.Request;
import models.FoodListing;
import models.User;
import java.util.List;

/**
 * Request Service
 * Handles business logic for NGO requests
 */
public class RequestService {
    
    private RequestDAO requestDAO;
    private FoodListingDAO listingDAO;
    
    public RequestService() {
        this.requestDAO = new RequestDAO();
        this.listingDAO = new FoodListingDAO();
    }
    
    /**
     * Accept a food listing (NGO)
     * @return Error message if failed, null if successful
     */
    public String acceptListing(User ngo, int listingId, String notes) {
        // Validate NGO
        if (ngo == null || !ngo.isNGO()) {
            return "Only NGOs can accept food listings";
        }
        
        // Check if listing exists
        FoodListing listing = listingDAO.getListingById(listingId);
        if (listing == null) {
            return "Listing not found";
        }
        
        // Check if listing is available
        if (!listing.isAvailable()) {
            return "This listing is no longer available";
        }
        
        // Check if listing is expired
        if (listing.getExpiryTime().before(new java.sql.Timestamp(System.currentTimeMillis()))) {
            return "This listing has expired";
        }
        
        // Check if already has an accepted request
        if (requestDAO.hasAcceptedRequest(listingId)) {
            return "This listing has already been accepted by another NGO";
        }
        
        // Validate notes (optional but limit length)
        if (notes != null && notes.trim().length() > 500) {
            return "Notes are too long (max 500 characters)";
        }
        
        // Create request
        Request request = new Request(
            listingId,
            ngo.getFssaiNumber(),
            "accepted",
            notes != null ? notes.trim() : ""
        );
        
        boolean created = requestDAO.createRequest(request);
        if (!created) {
            return "Failed to accept listing. Please try again.";
        }
        
        System.out.println("✓ Listing accepted by: " + ngo.getOrganizationName());
        return null; // Success
    }
    
    /**
     * Get all requests by NGO
     */
    public List<Request> getNGORequests(String ngoFssai) {
        return requestDAO.getRequestsByNGO(ngoFssai);
    }
    
    /**
     * Get requests for a specific listing
     */
    public List<Request> getListingRequests(int listingId) {
        return requestDAO.getRequestsByListing(listingId);
    }
    
    /**
     * Mark request as completed
     * @return Error message if failed, null if successful
     */
    public String completeRequest(User ngo, int requestId) {
        // Validate NGO
        if (ngo == null || !ngo.isNGO()) {
            return "Only NGOs can complete requests";
        }
        
        // Update request status
        boolean updated = requestDAO.updateRequestStatus(requestId, "completed");
        if (!updated) {
            return "Failed to update request status. Please try again.";
        }
        
        System.out.println("✓ Request marked as completed: " + requestId);
        return null; // Success
    }
    
    /**
     * Get NGO statistics
     */
    public int[] getNGOStats(String ngoFssai) {
        return requestDAO.getNGOStats(ngoFssai);
    }
    
    /**
     * Check if NGO has already accepted a listing
     */
    public boolean hasNGOAcceptedListing(String ngoFssai, int listingId) {
        List<Request> requests = requestDAO.getRequestsByListing(listingId);
        
        for (Request request : requests) {
            if (request.getNgoFssai().equals(ngoFssai) && 
                request.isAccepted()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get accepted listings count for NGO
     */
    public int getAcceptedListingsCount(String ngoFssai) {
        List<Request> requests = requestDAO.getRequestsByNGO(ngoFssai);
        int count = 0;
        
        for (Request request : requests) {
            if (request.isAccepted()) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Validate notes input
     */
    public static String validateNotes(String notes) {
        if (notes != null && notes.trim().length() > 500) {
            return "Notes are too long (max 500 characters)";
        }
        return null; // Valid
    }
}