package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Main Application Frame
 * Manages the main window and panel switching
 */
public class AppFrame extends JFrame {
    
    private static final String APP_TITLE = "Anna Setu - Food Distribution System";
    
    private JPanel contentPane;
    private CardLayout cardLayout;
    
    public AppFrame() {
        initializeFrame();
        setupLayout();
    }
    
    /**
     * Initialize frame properties
     */
    private void initializeFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized
        setMinimumSize(new Dimension(1024, 768));
        
        // Set icon (optional - would need an icon file)
        // setIconImage(new ImageIcon("assets/icon.png").getImage());
        
        // Center on screen if not maximized
        setLocationRelativeTo(null);
    }
    
    /**
     * Setup main layout with CardLayout for panel switching
     */
    private void setupLayout() {
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        contentPane.setBackground(UIConstants.BG_PRIMARY);
        
        setContentPane(contentPane);
    }
    
    /**
     * Add a panel to the card layout
     */
    public void addPanel(String name, JPanel panel) {
        contentPane.add(panel, name);
    }
    
    /**
     * Show a specific panel by name
     */
    public void showPanel(String name) {
        cardLayout.show(contentPane, name);
    }
    
    /**
     * Get the content pane (for direct access if needed)
     */
    public JPanel getContentPanel() {
        return contentPane;
    }
    
    /**
     * Update window title
     */
    public void setCustomTitle(String subtitle) {
        if (subtitle == null || subtitle.isEmpty()) {
            setTitle(APP_TITLE);
        } else {
            setTitle(APP_TITLE + " - " + subtitle);
        }
    }
    
    /**
     * Show the frame
     */
    public void display() {
        setVisible(true);
    }
}