package models;

import java.sql.Timestamp;

/**
 * User Model - Represents both Restaurant and NGO users
 */
public class User {
    
    private String fssaiNumber;
    private String userType; // "restaurant" or "ngo"
    private String organizationName;
    private String phoneNumber;
    private String email;
    private String address;
    private String otp;
    private Timestamp otpGeneratedAt;
    private char otpVerified; // 'Y' or 'N'
    private Timestamp createdAt;
    private Timestamp lastLogin;
    
    // Constructors
    public User() {}
    
    public User(String fssaiNumber, String userType, String organizationName, String phoneNumber) {
        this.fssaiNumber = fssaiNumber;
        this.userType = userType;
        this.organizationName = organizationName;
        this.phoneNumber = phoneNumber;
        this.otpVerified = 'N';
    }
    
    // Getters and Setters
    public String getFssaiNumber() {
        return fssaiNumber;
    }
    
    public void setFssaiNumber(String fssaiNumber) {
        this.fssaiNumber = fssaiNumber;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getOtp() {
        return otp;
    }
    
    public void setOtp(String otp) {
        this.otp = otp;
    }
    
    public Timestamp getOtpGeneratedAt() {
        return otpGeneratedAt;
    }
    
    public void setOtpGeneratedAt(Timestamp otpGeneratedAt) {
        this.otpGeneratedAt = otpGeneratedAt;
    }
    
    public char getOtpVerified() {
        return otpVerified;
    }
    
    public void setOtpVerified(char otpVerified) {
        this.otpVerified = otpVerified;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public boolean isRestaurant() {
        return "restaurant".equalsIgnoreCase(userType);
    }
    
    public boolean isNGO() {
        return "ngo".equalsIgnoreCase(userType);
    }
    
    public boolean isOtpVerified() {
        return otpVerified == 'Y';
    }
    
    @Override
    public String toString() {
        return "User{" +
                "fssaiNumber='" + fssaiNumber + '\'' +
                ", userType='" + userType + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", otpVerified=" + otpVerified +
                '}';
    }
}
