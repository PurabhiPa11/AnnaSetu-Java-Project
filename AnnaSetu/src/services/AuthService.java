package services;

import db.UserDAO;
import models.User;

/**
 * Authentication Service
 * Handles user authentication, OTP generation/verification, and login
 */
public class AuthService {
    
    private UserDAO userDAO;
    private User currentUser;
    
    public AuthService() {
        this.userDAO = new UserDAO();
        this.currentUser = null;
    }
    
    /**
     * Step 1: Request OTP for FSSAI number
     * @return Error message if failed, null if successful
     */
    public String requestOTP(String fssaiNumber) {
        // Validate FSSAI number format
        if (fssaiNumber == null || fssaiNumber.trim().isEmpty()) {
            return "FSSAI number cannot be empty";
        }
        
        fssaiNumber = fssaiNumber.trim();
        
        if (fssaiNumber.length() != 14) {
            return "FSSAI number must be exactly 14 digits";
        }
        
        if (!fssaiNumber.matches("\\d+")) {
            return "FSSAI number must contain only digits";
        }
        
        // Check if user exists
        User user = userDAO.findByFssai(fssaiNumber);
        if (user == null) {
            return "FSSAI number not registered. Please contact administrator.";
        }
        
        // Generate OTP
        boolean otpGenerated = userDAO.generateOTP(fssaiNumber);
        if (!otpGenerated) {
            return "Failed to generate OTP. Please try again.";
        }
        
        // In production, SMS would be sent here
        System.out.println("📱 OTP sent to: " + user.getPhoneNumber());
        
        return null; // Success
    }
    
    /**
     * Step 2: Verify OTP and login
     * @return Error message if failed, null if successful
     */
    public String verifyOTPAndLogin(String fssaiNumber, String otp) {
        // Validate inputs
        if (fssaiNumber == null || fssaiNumber.trim().isEmpty()) {
            return "FSSAI number cannot be empty";
        }
        
        if (otp == null || otp.trim().isEmpty()) {
            return "OTP cannot be empty";
        }
        
        otp = otp.trim();
        
        if (otp.length() != 6) {
            return "OTP must be 6 digits";
        }
        
        if (!otp.matches("\\d+")) {
            return "OTP must contain only digits";
        }
        
        // Verify OTP
        boolean verified = userDAO.verifyOTP(fssaiNumber, otp);
        if (!verified) {
            return "Invalid or expired OTP. Please try again.";
        }
        
        // Load user data
        currentUser = userDAO.findByFssai(fssaiNumber);
        if (currentUser == null) {
            return "Failed to load user data";
        }
        
        System.out.println("✓ User logged in: " + currentUser.getOrganizationName());
        
        return null; // Success
    }
    
    /**
     * Get currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user is a restaurant
     */
    public boolean isRestaurant() {
        return currentUser != null && currentUser.isRestaurant();
    }
    
    /**
     * Check if current user is an NGO
     */
    public boolean isNGO() {
        return currentUser != null && currentUser.isNGO();
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("✓ User logged out: " + currentUser.getOrganizationName());
            currentUser = null;
        }
    }
    
    /**
     * Get user type display string
     */
    public String getUserTypeDisplay() {
        if (currentUser == null) return "Guest";
        return currentUser.isRestaurant() ? "Restaurant" : "NGO";
    }
    
    /**
     * Get organization name
     */
    public String getOrganizationName() {
        return currentUser != null ? currentUser.getOrganizationName() : "";
    }
    
    /**
     * Get FSSAI number
     */
    public String getFssaiNumber() {
        return currentUser != null ? currentUser.getFssaiNumber() : "";
    }
    
    /**
     * Validate FSSAI format only (without checking existence)
     */
    public static String validateFssaiFormat(String fssai) {
        if (fssai == null || fssai.trim().isEmpty()) {
            return "FSSAI number is required";
        }
        
        fssai = fssai.trim();
        
        if (fssai.length() != 14) {
            return "FSSAI number must be 14 digits";
        }
        
        if (!fssai.matches("\\d+")) {
            return "FSSAI number must contain only digits";
        }
        
        return null; // Valid format
    }
}