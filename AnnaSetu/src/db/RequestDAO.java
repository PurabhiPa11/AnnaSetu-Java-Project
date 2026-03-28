package db;

import models.Request;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Request operations
 * Handles all database operations related to NGO requests
 */
public class RequestDAO {
    
    private Connection connection;
    
    public RequestDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Create new request (NGO accepting food)
     */
    public boolean createRequest(Request request) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert request
            String insertSql = "INSERT INTO requests (request_id, listing_id, ngo_fssai, " +
                              "status, notes) VALUES (request_id_seq.NEXTVAL, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, request.getListingId());
                pstmt.setString(2, request.getNgoFssai());
                pstmt.setString(3, request.getStatus());
                pstmt.setString(4, request.getNotes());
                
                pstmt.executeUpdate();
            }
            
            // Update listing status to 'accepted'
            String updateSql = "UPDATE food_listings SET status = 'accepted', " +
                              "updated_at = CURRENT_TIMESTAMP WHERE listing_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, request.getListingId());
                pstmt.executeUpdate();
            }
            
            conn.commit(); // Commit transaction
            System.out.println("✓ Request created and listing accepted: " + request.getListingId());
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating request: " + e.getMessage());
            e.printStackTrace();
            
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("  Transaction rolled back");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset to default
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    
    /**
     * Get all requests by NGO
     */
    public List<Request> getRequestsByNGO(String ngoFssai) {
        List<Request> requests = new ArrayList<>();
        
        String sql = "SELECT r.*, fl.food_name, fl.quantity, fl.expiry_time, " +
                     "u.organization_name as restaurant_name, u.phone_number as restaurant_phone " +
                     "FROM requests r " +
                     "JOIN food_listings fl ON r.listing_id = fl.listing_id " +
                     "JOIN users u ON fl.restaurant_fssai = u.fssai_number " +
                     "WHERE r.ngo_fssai = ? " +
                     "ORDER BY r.requested_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ngoFssai);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requests.add(extractRequestFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting NGO requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * Get all requests for a specific listing
     */
    public List<Request> getRequestsByListing(int listingId) {
        List<Request> requests = new ArrayList<>();
        
        String sql = "SELECT r.*, u.organization_name as ngo_name, u.phone_number as ngo_phone " +
                     "FROM requests r " +
                     "JOIN users u ON r.ngo_fssai = u.fssai_number " +
                     "WHERE r.listing_id = ? " +
                     "ORDER BY r.requested_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, listingId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Request request = new Request();
                request.setRequestId(rs.getInt("request_id"));
                request.setListingId(rs.getInt("listing_id"));
                request.setNgoFssai(rs.getString("ngo_fssai"));
                request.setNgoName(rs.getString("ngo_name"));
                request.setStatus(rs.getString("status"));
                request.setRequestedAt(rs.getTimestamp("requested_at"));
                request.setNotes(rs.getString("notes"));
                
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error getting listing requests: " + e.getMessage());
            e.printStackTrace();
        }
        
        return requests;
    }
    
    /**
     * Update request status
     */
    public boolean updateRequestStatus(int requestId, String status) {
        String sql = "UPDATE requests SET status = ? WHERE request_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✓ Request status updated: " + requestId + " → " + status);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if listing already has an accepted request
     */
    public boolean hasAcceptedRequest(int listingId) {
        String sql = "SELECT COUNT(*) FROM requests " +
                     "WHERE listing_id = ? AND status = 'accepted'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, listingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking accepted request: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get statistics for NGO
     */
    public int[] getNGOStats(String ngoFssai) {
        int[] stats = new int[3]; // [total, accepted, completed]
        
        String sql = "SELECT status, COUNT(*) as count FROM requests " +
                     "WHERE ngo_fssai = ? GROUP BY status";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, ngoFssai);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("count");
                stats[0] += count; // total
                
                if ("accepted".equalsIgnoreCase(status)) {
                    stats[1] = count;
                } else if ("completed".equalsIgnoreCase(status)) {
                    stats[2] = count;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting NGO stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Extract Request from ResultSet
     */
    private Request extractRequestFromResultSet(ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setRequestId(rs.getInt("request_id"));
        request.setListingId(rs.getInt("listing_id"));
        request.setNgoFssai(rs.getString("ngo_fssai"));
        request.setStatus(rs.getString("status"));
        request.setRequestedAt(rs.getTimestamp("requested_at"));
        request.setNotes(rs.getString("notes"));
        
        // Additional fields for display
        try {
            request.setFoodName(rs.getString("food_name"));
            request.setQuantity(rs.getString("quantity"));
            request.setRestaurantName(rs.getString("restaurant_name"));
        } catch (SQLException e) {
            // These fields might not be present in all queries
        }
        
        return request;
    }
}