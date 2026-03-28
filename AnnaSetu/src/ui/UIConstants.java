package ui;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * UI Constants - Colors, Fonts, Dimensions, and Styling
 * Centralized design system for consistent UI
 */
public class UIConstants {
    
    // ==================== COLORS ====================
    
    // Primary Colors
    public static final Color PRIMARY = new Color(34, 139, 34);        // Forest Green
    public static final Color PRIMARY_DARK = new Color(25, 100, 25);   // Darker Green
    public static final Color PRIMARY_LIGHT = new Color(144, 238, 144); // Light Green
    
    // Secondary Colors
    public static final Color SECONDARY = new Color(255, 140, 0);      // Dark Orange
    public static final Color SECONDARY_DARK = new Color(204, 102, 0); // Darker Orange
    public static final Color SECONDARY_LIGHT = new Color(255, 200, 124); // Light Orange
    
    // Background Colors
    public static final Color BG_PRIMARY = new Color(250, 250, 250);   // Off White
    public static final Color BG_SECONDARY = new Color(255, 255, 255); // Pure White
    public static final Color BG_DARK = new Color(245, 245, 245);      // Light Gray
    
    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);    // Almost Black
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117); // Gray
    public static final Color TEXT_LIGHT = new Color(255, 255, 255);   // White
    public static final Color TEXT_DISABLED = new Color(189, 189, 189); // Light Gray
    
    // Status Colors
    public static final Color SUCCESS = new Color(76, 175, 80);        // Green
    public static final Color WARNING = new Color(255, 152, 0);        // Orange
    public static final Color ERROR = new Color(244, 67, 54);          // Red
    public static final Color INFO = new Color(33, 150, 243);          // Blue
    
    // Special Colors
    public static final Color AVAILABLE = new Color(46, 125, 50);      // Available Green
    public static final Color ACCEPTED = new Color(251, 192, 45);      // Accepted Yellow/Orange
    public static final Color EXPIRED = new Color(198, 40, 40);        // Expired Red
    public static final Color CANCELLED = new Color(97, 97, 97);       // Cancelled Gray
    
    // Border Colors
    public static final Color BORDER_LIGHT = new Color(224, 224, 224); // Light Border
    public static final Color BORDER_MEDIUM = new Color(189, 189, 189); // Medium Border
    public static final Color BORDER_DARK = new Color(158, 158, 158);  // Dark Border
    
    // Hover/Focus Colors
    public static final Color HOVER_BG = new Color(245, 245, 245);     // Hover Background
    public static final Color FOCUS_BORDER = PRIMARY;                   // Focus Border
    
    // ==================== FONTS ====================
    
    // Font Families
    public static final String FONT_FAMILY_PRIMARY = "Segoe UI";
    public static final String FONT_FAMILY_SECONDARY = "Arial";
    public static final String FONT_FAMILY_MONO = "Courier New";
    
    // Font Sizes
    public static final int FONT_SIZE_LARGE = 24;
    public static final int FONT_SIZE_TITLE = 20;
    public static final int FONT_SIZE_HEADING = 18;
    public static final int FONT_SIZE_SUBHEADING = 16;
    public static final int FONT_SIZE_NORMAL = 14;
    public static final int FONT_SIZE_SMALL = 12;
    public static final int FONT_SIZE_TINY = 10;
    
    // Font Instances
    public static final Font FONT_LARGE = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_LARGE);
    public static final Font FONT_TITLE = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_TITLE);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_HEADING);
    public static final Font FONT_SUBHEADING = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_SUBHEADING);
    public static final Font FONT_NORMAL = new Font(FONT_FAMILY_PRIMARY, Font.PLAIN, FONT_SIZE_NORMAL);
    public static final Font FONT_NORMAL_BOLD = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_NORMAL);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY_PRIMARY, Font.PLAIN, FONT_SIZE_SMALL);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_SMALL);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY_PRIMARY, Font.BOLD, FONT_SIZE_NORMAL);
    
    // ==================== DIMENSIONS ====================
    
    // Spacing
    public static final int SPACING_TINY = 4;
    public static final int SPACING_SMALL = 8;
    public static final int SPACING_MEDIUM = 12;
    public static final int SPACING_NORMAL = 16;
    public static final int SPACING_LARGE = 24;
    public static final int SPACING_XLARGE = 32;
    public static final int SPACING_XXLARGE = 48;
    
    // Component Heights
    public static final int HEIGHT_BUTTON = 40;
    public static final int HEIGHT_INPUT = 40;
    public static final int HEIGHT_HEADER = 80;
    public static final int HEIGHT_FOOTER = 60;
    
    // Component Widths
    public static final int WIDTH_BUTTON_SMALL = 100;
    public static final int WIDTH_BUTTON_MEDIUM = 150;
    public static final int WIDTH_BUTTON_LARGE = 200;
    public static final int WIDTH_INPUT = 300;
    public static final int WIDTH_PANEL_SIDE = 300;
    
    // Border Radius
    public static final int RADIUS_SMALL = 4;
    public static final int RADIUS_MEDIUM = 8;
    public static final int RADIUS_LARGE = 12;
    public static final int RADIUS_XLARGE = 16;
    
    // ==================== BORDERS ====================
    
    public static final Border BORDER_NONE = BorderFactory.createEmptyBorder();
    public static final Border BORDER_THIN = BorderFactory.createLineBorder(BORDER_LIGHT, 1);
    public static final Border BORDER_THICK = BorderFactory.createLineBorder(BORDER_MEDIUM, 2);
    public static final Border BORDER_FOCUS = BorderFactory.createLineBorder(FOCUS_BORDER, 2);
    
    // Padding Borders
    public static final Border PADDING_SMALL = BorderFactory.createEmptyBorder(
        SPACING_SMALL, SPACING_SMALL, SPACING_SMALL, SPACING_SMALL
    );
    public static final Border PADDING_MEDIUM = BorderFactory.createEmptyBorder(
        SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM, SPACING_MEDIUM
    );
    public static final Border PADDING_NORMAL = BorderFactory.createEmptyBorder(
        SPACING_NORMAL, SPACING_NORMAL, SPACING_NORMAL, SPACING_NORMAL
    );
    public static final Border PADDING_LARGE = BorderFactory.createEmptyBorder(
        SPACING_LARGE, SPACING_LARGE, SPACING_LARGE, SPACING_LARGE
    );
    
    // Compound Borders
    public static final Border BORDER_WITH_PADDING = BorderFactory.createCompoundBorder(
        BORDER_THIN, PADDING_MEDIUM
    );
    
    // ==================== ANIMATION ====================
    
    public static final int ANIMATION_DURATION = 200; // milliseconds
    public static final int REFRESH_INTERVAL = 5000;  // 5 seconds
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Get status color based on status string
     */
    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_SECONDARY;
        
        switch (status.toLowerCase()) {
            case "available":
                return AVAILABLE;
            case "accepted":
                return ACCEPTED;
            case "expired":
                return EXPIRED;
            case "cancelled":
                return CANCELLED;
            case "completed":
                return SUCCESS;
            default:
                return TEXT_SECONDARY;
        }
    }
    
    /**
     * Get status display text
     */
    public static String getStatusDisplay(String status) {
        if (status == null) return "Unknown";
        
        switch (status.toLowerCase()) {
            case "available":
                return "Available";
            case "accepted":
                return "Accepted";
            case "expired":
                return "Expired";
            case "cancelled":
                return "Cancelled";
            case "completed":
                return "Completed";
            default:
                return status;
        }
    }
    
    /**
     * Create a lighter version of a color
     */
    public static Color lighter(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
    
    /**
     * Create a darker version of a color
     */
    public static Color darker(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * factor));
        int g = Math.max(0, (int)(color.getGreen() * factor));
        int b = Math.max(0, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }
}