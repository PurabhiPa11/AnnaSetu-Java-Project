package ui;
import db.UserDAO;
import java.awt.*;
import javax.swing.*;
import models.User;
import services.AuthService;

/**
 * Login Panel - FSSAI and OTP verification
 */
public class LoginPanel extends JPanel {
    
   private AppFrame appFrame;;
   private String userType;
   private AuthService authService;
    
    // UI Components
    private JTextField fssaiField;
    private JPasswordField otpField;
    private JButton sendOtpButton;
    private JButton verifyButton;
    private JButton backButton;
    private JLabel statusLabel;
    private JPanel otpPanel;
    
    private String currentFssai = null;
    
    public LoginPanel(AppFrame appFrame, String userType) {
        this.appFrame = appFrame;
        this.userType = userType;
        this.authService = new AuthService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_PRIMARY);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIConstants.BG_PRIMARY);
        contentPanel.setBorder(UIConstants.PADDING_LARGE);
        
        // Add vertical glue for centering
        contentPanel.add(Box.createVerticalGlue());
        
        // Login card
        JPanel loginCard = createLoginCard();
        loginCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(loginCard);
        
        contentPanel.add(Box.createVerticalGlue());
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create login card
     */
    private JPanel createLoginCard() {
        JPanel card = ComponentFactory.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(500, 600));
        card.setPreferredSize(new Dimension(500, 600));
        
        // Title
        JLabel titleLabel = ComponentFactory.createTitle(
            "Login as " + (userType.equals("restaurant") ? "Restaurant" : "NGO")
        );
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_SMALL)));
        
        JLabel subtitleLabel = ComponentFactory.createSecondaryLabel(
            "Enter your FSSAI number to continue"
        );
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_LARGE)));
        
        // FSSAI Input
        JPanel fssaiPanel = createFssaiPanel();
        fssaiPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(fssaiPanel);
        
        card.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_NORMAL)));
        
        // OTP Panel (hidden initially)
        otpPanel = createOtpPanel();
        otpPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        otpPanel.setVisible(false);
        card.add(otpPanel);
        
        card.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_NORMAL)));
        
        // Status label
        statusLabel = ComponentFactory.createSecondaryLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_LARGE)));
        
        // Back button
        backButton = ComponentFactory.createOutlineButton("Back to Home");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> goBackToHome());
        card.add(backButton);
        
        return card;
    }
    
    /**
     * Create FSSAI input panel
     */
    private JPanel createFssaiPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_SECONDARY);
        
        JLabel label = ComponentFactory.createLabel("FSSAI Number (14 digits)");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        
        panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_SMALL)));
        
        fssaiField = ComponentFactory.createTextField("Enter 14-digit FSSAI number");
        fssaiField.setMaximumSize(new Dimension(UIConstants.WIDTH_INPUT, UIConstants.HEIGHT_INPUT));
        fssaiField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(fssaiField);
        
        panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_NORMAL)));
        
        sendOtpButton = ComponentFactory.createPrimaryButton("Send OTP");
        sendOtpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendOtpButton.addActionListener(e -> handleSendOtp());
        panel.add(sendOtpButton);
        
        return panel;
    }
    
    /**
     * Create OTP input panel
     */
    private JPanel createOtpPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_SECONDARY);
        
        JLabel label = ComponentFactory.createLabel("Enter OTP (6 digits)");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        
        panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_SMALL)));
        
        otpField = ComponentFactory.createPasswordField("Enter 6-digit OTP");
        otpField.setMaximumSize(new Dimension(UIConstants.WIDTH_INPUT, UIConstants.HEIGHT_INPUT));
        otpField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(otpField);
        
        panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_NORMAL)));
        
        verifyButton = ComponentFactory.createPrimaryButton("Verify & Login");
        verifyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verifyButton.addActionListener(e -> handleVerifyOtp());
        panel.add(verifyButton);
        
        return panel;
    }
    
    /**
     * Handle Send OTP button click
     */
    private void handleSendOtp() {
        String fssai = fssaiField.getText().trim();
        
        // Validate format
        String error = AuthService.validateFssaiFormat(fssai);
        if (error != null) {
            showError(error);
            return;
        }
        
        // Disable button and show loading
        sendOtpButton.setEnabled(false);
        sendOtpButton.setText("Sending...");
        statusLabel.setText("Validating FSSAI number...");
        statusLabel.setForeground(UIConstants.INFO);
        
        // Validate and send OTP in background thread
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                // First check if user exists and validate user type
                UserDAO userDAO = new UserDAO();
                User user = userDAO.findByFssai(fssai);
                
                if (user == null) {
                    return "FSSAI number not registered. Please contact administrator.";
                }
                
                // CRITICAL: Check if user type matches login type
                if (userType.equals("restaurant") && !user.isRestaurant()) {
                    return "This FSSAI number belongs to an NGO. Please use 'Login as NGO'.";
                }
                
                if (userType.equals("ngo") && !user.isNGO()) {
                    return "This FSSAI number belongs to a Restaurant. Please use 'Login as Restaurant'";
                }
                
                // User type matches, now request OTP
                return authService.requestOTP(fssai);
            }
            
            @Override
            protected void done() {
                try {
                    String error = get();
                    
                    if (error == null) {
                        // Success
                        currentFssai = fssai;
                        otpPanel.setVisible(true);
                        fssaiField.setEnabled(false);
                        sendOtpButton.setText("OTP Sent ✓");
                        statusLabel.setText("✓ OTP sent successfully! Check your phone.");
                        statusLabel.setForeground(UIConstants.SUCCESS);
                        otpField.requestFocus();
                    } else {
                        // Error
                        showError(error);
                        sendOtpButton.setEnabled(true);
                        sendOtpButton.setText("Send OTP");
                    }
                } catch (Exception e) {
                    showError("Failed to send OTP. Please try again.");
                    sendOtpButton.setEnabled(true);
                    sendOtpButton.setText("Send OTP");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handle Verify OTP button click
     */
    private void handleVerifyOtp() {
        if (currentFssai == null) {
            showError("Please send OTP first");
            return;
        }
        
        String otp = new String(otpField.getPassword()).trim();
        
        if (otp.isEmpty()) {
            showError("Please enter OTP");
            return;
        }
        
        // Disable button and show loading
        verifyButton.setEnabled(false);
        verifyButton.setText("Verifying...");
        statusLabel.setText("Verifying OTP...");
        statusLabel.setForeground(UIConstants.INFO);
        
        // Verify OTP in background thread
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return authService.verifyOTPAndLogin(currentFssai, otp);
            }
            
            @Override
            protected void done() {
                try {
                    String error = get();
                    
                    if (error == null) {
                        // Double-check user type after login (extra safety)
                        User currentUser = authService.getCurrentUser();
                        if (currentUser != null) {
                            boolean typeMatches = (userType.equals("restaurant") && currentUser.isRestaurant()) ||
                                                 (userType.equals("ngo") && currentUser.isNGO());
                            
                            if (!typeMatches) {
                                showError("Login error: User type mismatch. Please try again.");
                                authService.logout();
                                verifyButton.setEnabled(true);
                                verifyButton.setText("Verify & Login");
                                return;
                            }
                        }
                        
                        // Success - navigate to dashboard
                        statusLabel.setText("✓ Login successful! Loading dashboard...");
                        statusLabel.setForeground(UIConstants.SUCCESS);
                        
                        // Delay for user to see success message
                        Timer timer = new Timer(1000, evt -> showDashboard());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        // Error
                        showError(error);
                        verifyButton.setEnabled(true);
                        verifyButton.setText("Verify & Login");
                        otpField.setText("");
                        otpField.requestFocus();
                    }
                } catch (Exception e) {
                    showError("Failed to verify OTP. Please try again.");
                    verifyButton.setEnabled(true);
                    verifyButton.setText("Verify & Login");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(UIConstants.ERROR);
    }
    
    /**
     * Navigate to appropriate dashboard
     */
    private void showDashboard() {
        if (authService.isRestaurant()) {
            RestaurantDashboard dashboard = new RestaurantDashboard(appFrame, authService);
            appFrame.addPanel("restaurant_dashboard", dashboard);
            appFrame.showPanel("restaurant_dashboard");
            appFrame.setCustomTitle("Restaurant Dashboard - " + authService.getOrganizationName());
        } else if (authService.isNGO()) {
            NGODashboard dashboard = new NGODashboard(appFrame, authService);
            appFrame.addPanel("ngo_dashboard", dashboard);
            appFrame.showPanel("ngo_dashboard");
            appFrame.setCustomTitle("NGO Dashboard - " + authService.getOrganizationName());
        }
    }
    
    /**
     * Go back to homepage
     */
    private void goBackToHome() {
        appFrame.showPanel("homepage");
        appFrame.setCustomTitle(null);
    }
}