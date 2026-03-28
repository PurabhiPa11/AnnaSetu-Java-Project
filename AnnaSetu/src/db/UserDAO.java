package db;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 * Handles all database operations related to users (restaurants and NGOs)
 */
public class UserDAO {
    
    private Connection connection;
    
    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Find user by FSSAI number
     */
    public User findByFssai(String fssaiNumber) {
        String sql = "SELECT * FROM users WHERE fssai_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fssaiNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by FSSAI: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Find user by phone number
     */
    public User findByPhone(String phoneNumber) {
        String sql = "SELECT * FROM users WHERE phone_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by phone: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Generate and save OTP for user
     */
    public boolean generateOTP(String fssaiNumber) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", (int)(Math.random() * 1000000));
        
        String sql = "UPDATE users SET otp = ?, otp_generated_at = CURRENT_TIMESTAMP, " +
                     "otp_verified = 'N' WHERE fssai_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, otp);
            pstmt.setString(2, fssaiNumber);
            
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✓ OTP generated for FSSAI: " + fssaiNumber + " → " + otp);
                // In production, send OTP via SMS API here
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error generating OTP: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Verify OTP for user login
     */
    public boolean verifyOTP(String fssaiNumber, String otp) {
        String sql = "SELECT otp, otp_generated_at FROM users WHERE fssai_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fssaiNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedOtp = rs.getString("otp");
                Timestamp generatedAt = rs.getTimestamp("otp_generated_at");
                
                // Check if OTP matches
                if (storedOtp != null && storedOtp.equals(otp)) {
                    // Check if OTP is still valid (within 10 minutes)
                    long timeDiff = System.currentTimeMillis() - generatedAt.getTime();
                    long minutes = timeDiff / (60 * 1000);
                    
                    if (minutes <= 10) {
                        // Mark OTP as verified and update last login
                        return markOTPVerified(fssaiNumber);
                    } else {
                        System.err.println("✗ OTP expired (valid for 10 minutes only)");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error verifying OTP: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Mark OTP as verified and update last login
     */
    private boolean markOTPVerified(String fssaiNumber) {
        String sql = "UPDATE users SET otp_verified = 'Y', last_login = CURRENT_TIMESTAMP " +
                     "WHERE fssai_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fssaiNumber);
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✓ OTP verified successfully for: " + fssaiNumber);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error marking OTP verified: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Create new user (for registration)
     */
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (fssai_number, user_type, organization_name, " +
                     "phone_number, email, address, otp_verified) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'N')";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getFssaiNumber());
            pstmt.setString(2, user.getUserType());
            pstmt.setString(3, user.getOrganizationName());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getAddress());
            
            int inserted = pstmt.executeUpdate();
            
            if (inserted > 0) {
                System.out.println("✓ User created: " + user.getOrganizationName());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Get all users of a specific type
     */
    public List<User> getUsersByType(String userType) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE user_type = ? ORDER BY organization_name";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting users by type: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Update user information
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET organization_name = ?, phone_number = ?, " +
                     "email = ?, address = ? WHERE fssai_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getOrganizationName());
            pstmt.setString(2, user.getPhoneNumber());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getFssaiNumber());
            
            int updated = pstmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✓ User updated: " + user.getFssaiNumber());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Check if FSSAI number exists
     */
    public boolean fssaiExists(String fssaiNumber) {
        String sql = "SELECT COUNT(*) FROM users WHERE fssai_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fssaiNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking FSSAI existence: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Extract User object from ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setFssaiNumber(rs.getString("fssai_number"));
        user.setUserType(rs.getString("user_type"));
        user.setOrganizationName(rs.getString("organization_name"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        user.setOtp(rs.getString("otp"));
        user.setOtpGeneratedAt(rs.getTimestamp("otp_generated_at"));
        user.setOtpVerified(rs.getString("otp_verified").charAt(0));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}