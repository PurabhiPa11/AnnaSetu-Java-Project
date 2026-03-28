package db;

import models.FoodListing;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FoodListing operations
 * Handles all database operations related to food listings
 */
public class FoodListingDAO {
    
    private Connection connection;
    
    public FoodListingDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Create new food listing
     */
    public boolean createListing(FoodListing listing) {
        String sql = "INSERT INTO food_listings (listing_id, restaurant_fssai, food_name, " +
                     "description, quantity, expiry_time, status) " +
                     "VALUES (listing_id_seq.NEXTVAL, ?, ?, ?, ?, ?, 'available')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, listing.getRestaurantFssai());
            pstmt.setString(2, listing.getFoodName());
            pstmt.setString(3, listing.getDescription());
            pstmt.setString(4, listing.getQuantity());
            pstmt.setTimestamp(5, listing.getExpiryTime());
            
            int inserted = pstmt.executeUpdate();
            
            if (inserted > 0) {
                System.out.println("✓ Food listing created: " + listing.getFoodName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating food listing: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all listings by restaurant FSSAI
     */
    public List<FoodListing> getListingsByRestaurant(String restaurantFssai) {
        List<FoodListing> listings = new ArrayList<>();
        
        String sql = "SELECT fl.*, u.organization_name as restaurant_name, " +
                     "ngo.organization_name as ngo_name, ngo.phone_number as ngo_phone " +
                     "FROM food_listings fl " +
                     "JOIN users u ON fl.restaurant_fssai = u.fssai_number " +
                     "LEFT JOIN requests r ON fl.listing_id = r.listing_id AND r.status = 'accepted' " +
                     "LEFT JOIN users ngo ON r.ngo_fssai = ngo.fssai_number " +
                     "WHERE fl.restaurant_fssai = ? " +
                     "ORDER BY fl.created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, restaurantFssai);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                listings.add(extractListingFromResultSet(rs, true));
            }
        } catch (SQLException e) {
            System.err.println("Error getting restaurant listings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listings;
    }
    
    /**
     * Get all available listings (for NGOs)
     */
    public List<FoodListing> getAllAvailableListings() {
        List<FoodListing> listings = new ArrayList<>();
        
        String sql = "SELECT fl.*, u.organization_name as restaurant_name, u.phone_number " +
                     "FROM food_listings fl " +
                     "JOIN users u ON fl.restaurant_fssai = u.fssai_number " +
                     "WHERE fl.status = 'available' AND fl.expiry_time > SYSTIMESTAMP " +
                     "ORDER BY fl.created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                listings.add(extractListingFromResultSet(rs, false));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available listings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listings;
    }
    
    /**
     * Get listing by ID
     */
    public FoodListing getListingById(int listingId) {
        String sql = "SELECT fl.*, u.organization_name as restaurant_name " +
                     "FROM food_listings fl " +
                     "JOIN users u ON fl.restaurant_fssai = u.fssai_number " +
                     "WHERE fl.listing_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, listingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractListingFromResultSet(rs, false);
            }
        } catch (SQLException e) {
            System.err.println("Error getting listing by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Update listing status
     */
    public boolean updateListingStatus(int listingId, String status) {
        String sql = "UPDATE food_listings SET status = ?, updated_at = SYSTIMESTAMP " +
                     "WHERE listing_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, listingId);
            
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✓ Listing status updated: " + listingId + " → " + status);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating listing status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Delete listing
     */
    public boolean deleteListing(int listingId) {
        String sql = "DELETE FROM food_listings WHERE listing_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, listingId);
            
            int deleted = pstmt.executeUpdate();
            
            if (deleted > 0) {
                System.out.println("✓ Listing deleted: " + listingId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting listing: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mark expired listings
     */
    public int markExpiredListings() {
        // Get a fresh connection to ensure we're connected
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            
            // Verify connection is valid
            if (conn == null || conn.isClosed()) {
                System.err.println("Error: Database connection is not valid");
                return 0;
            }
            
            String sql = "UPDATE food_listings SET status = 'expired', updated_at = SYSTIMESTAMP " +
                        "WHERE status = 'available' AND expiry_time <= SYSTIMESTAMP";
            
            pstmt = conn.prepareStatement(sql);
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✓ Marked " + updated + " listing(s) as expired");
            }
            
            return updated;
        } catch (SQLException e) {
            System.err.println("Error marking expired listings: " + e.getMessage());
            // Print more details for debugging
            System.err.println("  SQL State: " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get statistics for restaurant
     */
    public int[] getRestaurantStats(String restaurantFssai) {
        int[] stats = new int[4]; // [total, available, accepted, expired]
        
        String sql = "SELECT status, COUNT(*) as count FROM food_listings " +
                     "WHERE restaurant_fssai = ? GROUP BY status";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, restaurantFssai);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                stats[0] += count; // total
                
                switch (status.toLowerCase()) {
                    case "available":
                        stats[1] = count;
                        break;
                    case "accepted":
                        stats[2] = count;
                        break;
                    case "expired":
                        stats[3] = count;
                        break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting restaurant stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Extract FoodListing from ResultSet
     */
    private FoodListing extractListingFromResultSet(ResultSet rs, boolean includeNGO) throws SQLException {
        FoodListing listing = new FoodListing();
        listing.setListingId(rs.getInt("listing_id"));
        listing.setRestaurantFssai(rs.getString("restaurant_fssai"));
        listing.setRestaurantName(rs.getString("restaurant_name"));
        listing.setFoodName(rs.getString("food_name"));
        listing.setDescription(rs.getString("description"));
        listing.setQuantity(rs.getString("quantity"));
        listing.setExpiryTime(rs.getTimestamp("expiry_time"));
        listing.setStatus(rs.getString("status"));
        listing.setCreatedAt(rs.getTimestamp("created_at"));
        listing.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        if (includeNGO) {
            try {
                listing.setAcceptedByNGO(rs.getString("ngo_name"));
                listing.setNgoPhone(rs.getString("ngo_phone"));
            } catch (SQLException e) {
                // NGO fields might not be present if not accepted
            }
        }
        
        return listing;
    }
}