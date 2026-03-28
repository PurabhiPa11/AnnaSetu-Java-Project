package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Homepage - Welcome screen with login options
 */
public class Homepage extends JPanel {
    
    private AppFrame appFrame;
    
    public Homepage(AppFrame appFrame) {
        this.appFrame = appFrame;
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
        
        // Logo/Title section
        JPanel titlePanel = createTitlePanel();
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titlePanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_XXLARGE)));
        
        // Description
        JPanel descPanel = createDescriptionPanel();
        descPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_XXLARGE)));
        
        // Login buttons
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(buttonPanel);
        
        contentPanel.add(Box.createVerticalGlue());
        
        // Footer
        JPanel footerPanel = createFooterPanel();
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(footerPanel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_LARGE)));
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Create title panel with app name
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_PRIMARY);
        
        // App name
        JLabel titleLabel = new JLabel("Anna Setu");
        titleLabel.setFont(new Font(UIConstants.FONT_FAMILY_PRIMARY, Font.BOLD, 56));
        titleLabel.setForeground(UIConstants.PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_SMALL)));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Food Distribution System");
        subtitleLabel.setFont(UIConstants.FONT_TITLE);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitleLabel);
        
        return panel;
    }
    
    /**
     * Create description panel
     */
    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_PRIMARY);
        panel.setMaximumSize(new Dimension(600, 200));
        
        String[] descriptions = {
            "Connecting restaurants with NGOs to reduce food waste",
            "and help those in need.",
            "",
            "Restaurants can list leftover food.",
            "NGOs can accept and collect food donations."
        };
        
        for (String text : descriptions) {
            JLabel label = new JLabel(text);
            label.setFont(UIConstants.FONT_SUBHEADING);
            label.setForeground(UIConstants.TEXT_PRIMARY);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_SMALL)));
        }
        
        return panel;
    }
    
    /**
     * Create button panel with login options
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_PRIMARY);
        
        // Restaurant login button
        JButton restaurantBtn = ComponentFactory.createPrimaryButton("Login as Restaurant");
        restaurantBtn.setFont(new Font(UIConstants.FONT_FAMILY_PRIMARY, Font.BOLD, 18));
        restaurantBtn.setPreferredSize(new Dimension(350, 60));
        restaurantBtn.setMaximumSize(new Dimension(350, 60));
        restaurantBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        restaurantBtn.addActionListener(e -> showLoginPage("restaurant"));
        panel.add(restaurantBtn);
        
        panel.add(Box.createRigidArea(new Dimension(0, UIConstants.SPACING_NORMAL)));
        
        // NGO login button
        JButton ngoBtn = ComponentFactory.createSecondaryButton("Login as NGO");
        ngoBtn.setFont(new Font(UIConstants.FONT_FAMILY_PRIMARY, Font.BOLD, 18));
        ngoBtn.setPreferredSize(new Dimension(350, 60));
        ngoBtn.setMaximumSize(new Dimension(350, 60));
        ngoBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        ngoBtn.addActionListener(e -> showLoginPage("ngo"));
        panel.add(ngoBtn);
        
        return panel;
    }
    
    /**
     * Create footer panel
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.BG_PRIMARY);
        
        JLabel footerLabel = ComponentFactory.createSecondaryLabel(
            "© 2025 Anna Setu - Bridging the gap between surplus and need"
        );
        panel.add(footerLabel);
        
        return panel;
    }
    
    /**
     * Show login page for selected user type
     */
    private void showLoginPage(String userType) {
        // Create and show login panel
        LoginPanel loginPanel = new LoginPanel(appFrame, userType);
        appFrame.addPanel("login", loginPanel);
        appFrame.showPanel("login");
        appFrame.setCustomTitle("Login - " + (userType.equals("restaurant") ? "Restaurant" : "NGO"));
    }
}