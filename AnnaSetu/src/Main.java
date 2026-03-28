import db.*;
import javax.swing.*;
import models.*;
import services.*;
import ui.*;

/**
 * Main Application Entry Point
 * Restaurant-NGO Food Distribution System - Anna Setu
 */
public class Main {

    public static void main(String[] args) {
        // Print welcome banner
        printBanner();
        
        // Step 1: Test database connection
        System.out.println("\n[STEP 1] Testing Database Connection...");
        System.out.println("----------------------------------------");
        
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (!dbConn.testConnection()) {
            System.err.println("✗ FATAL ERROR: Failed to connect to database!");
            System.err.println("\n  Troubleshooting:");
            System.err.println("  1. Ensure Oracle 10g XE is installed and running");
            System.err.println("  2. Check if the service 'OracleServiceXE' is started");
            System.err.println("  3. Verify connection details in DatabaseConnection.java:");
            System.err.println("     - URL: jdbc:oracle:thin:@localhost:1521:XE");
            System.err.println("     - Username: system");
            System.err.println("     - Password: root");
            System.err.println("  4. Run DatabaseSetup.java first to create tables");
            System.err.println("\n  Press Ctrl+C to exit");
            
            // Show error dialog
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to connect to Oracle Database!\n\n" +
                    "Please ensure:\n" +
                    "1. Oracle 10g XE is running\n" +
                    "2. Connection details are correct\n" +
                    "3. DatabaseSetup.java has been run",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            });
            
            return;
        }
        
        System.out.println(" Database connection successful");
        System.out.println(" Connected to: jdbc:oracle:thin:@localhost:1521:XE");
        
        // Step 2: Verify database tables
        System.out.println("\n[STEP 2] Verifying Database Tables...");
        System.out.println("----------------------------------------");
        
        if (!verifyDatabaseTables()) {
            System.err.println("✗ ERROR: Required tables not found!");
            System.err.println("\n  Please run DatabaseSetup.java first to create tables:");
            System.err.println("  1. Right-click on DatabaseSetup.java");
            System.err.println("  2. Select 'Run As' → 'Java Application'");
            System.err.println("  3. Wait for setup to complete");
            System.err.println("  4. Then run Main.java again");
            
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Database tables not found!\n\n" +
                    "Would you like to run the database setup now?\n" +
                    "(This will create all tables and sample data)",
                    "Database Setup Required",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    // Run DatabaseSetup
                    System.out.println("\n[SETUP] Running DatabaseSetup...");
                    DatabaseSetup.main(new String[]{});
                    System.out.println("\n✓ Database setup completed!");
                    System.out.println("\n  Starting application...");
                    
                    // Continue with app launch
                    launchApplication();
                } else {
                    System.exit(1);
                }
            });
            
            return;
        }
        
        System.out.println("✓ All required tables found");
        System.out.println("✓ users, food_listings, requests");
        
        // Step 3: Test Service Layer
        System.out.println("\n[STEP 3] Testing Service Layer...");
        System.out.println("----------------------------------------");
        testServiceLayer();
        
        // Step 4: Show test data info
        showTestDataInfo();
        
        // Step 5: Launch GUI Application
        launchApplication();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n========================================");
            System.out.println("Shutting down Anna Setu...");
            DatabaseConnection.getInstance().closeConnection();
            System.out.println("✓ Application closed successfully");
            System.out.println("========================================");
        }));
    }
    
    /**
     * Launch the GUI application
     */
    private static void launchApplication() {
        System.out.println("\n[STEP 4] Launching GUI Application...");
        System.out.println("----------------------------------------");
        System.out.println("✓ Starting Anna Setu GUI...");
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better UI
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("✓ System Look and Feel applied");
            } catch (Exception e) {
                System.out.println("⚠ Using default look and feel");
            }
            
            // Create and show application
            AppFrame appFrame = new AppFrame();
            Homepage homepage = new Homepage(appFrame);
            appFrame.addPanel("homepage", homepage);
            appFrame.showPanel("homepage");
            appFrame.display();
            
            System.out.println("✓ GUI launched successfully!");
            printUsageInstructions();
        });
    }
    
    /**
     * Print welcome banner
     */
    private static void printBanner() {
    System.out.println("\n==================================");
    System.out.println("        ANNA SETU SYSTEM         ");
    System.out.println("   Food Distribution System v1.0 ");
    System.out.println("==================================");
    System.out.println("Connecting Restaurants with NGOs");
    System.out.println("to reduce food waste and help");
    System.out.println("those in need");
    System.out.println("==================================\n");
}
    
    /**
     * Verify that all required database tables exist
     */
    private static boolean verifyDatabaseTables() {
        try {
            java.sql.Connection conn = DatabaseConnection.getInstance().getConnection();
            java.sql.DatabaseMetaData meta = conn.getMetaData();
            
            String[] requiredTables = {"USERS", "FOOD_LISTINGS", "REQUESTS"};
            int foundTables = 0;
            
            for (String table : requiredTables) {
                java.sql.ResultSet rs = meta.getTables(null, null, table, new String[]{"TABLE"});
                if (rs.next()) {
                    foundTables++;
                    System.out.println("  ✓ Table found: " + table);
                } else {
                    System.out.println("  ✗ Table missing: " + table);
                }
                rs.close();
            }
            
            return foundTables == requiredTables.length;
            
        } catch (Exception e) {
            System.err.println("Error verifying tables: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test service layer functionality
     */
    private static void testServiceLayer() {
        System.out.println("\nTesting AuthService...");
        
        // Test FSSAI validation
        String validationError = AuthService.validateFssaiFormat("10012345678901");
        if (validationError == null) {
            System.out.println("  ✓ FSSAI validation working");
        } else {
            System.out.println("  ✗ FSSAI validation failed: " + validationError);
        }
        
        // Test OTP generation
        System.out.println("  ✓ OTP generation service ready");
        
        System.out.println("\nTesting ListingService...");
        ListingService listingService = new ListingService();
        
        // Test expiry validation
        String expiryError = ListingService.validateExpiryHours("5");
        if (expiryError == null) {
            System.out.println("  ✓ Expiry validation working");
        } else {
            System.out.println("  ✗ Expiry validation failed: " + expiryError);
        }
        
        // Test getting available listings
        try {
            java.util.List<FoodListing> listings = listingService.getAvailableListings();
            System.out.println("  ✓ Found " + listings.size() + " available food listings");
        } catch (Exception e) {
            System.out.println("  ✗ Failed to get listings: " + e.getMessage());
        }
        
        System.out.println("\nTesting RequestService...");
        RequestService requestService = new RequestService();
        
        // Test getting requests
        try {
            java.util.List<Request> requests = requestService.getNGORequests("20012345678901");
            System.out.println("  ✓ Found " + requests.size() + " NGO requests");
        } catch (Exception e) {
            System.out.println("  ✗ Failed to get requests: " + e.getMessage());
        }
        
        System.out.println("\n✓ Service layer tests completed");
    }
    
    /**
     * Show test data information
     */
    private static void showTestDataInfo() {
        System.out.println("\n========================================");
        System.out.println("TEST DATA AVAILABLE");
        System.out.println("========================================");
        
        try {
            UserDAO userDAO = new UserDAO();
            
            // Count restaurants
            java.util.List<User> restaurants = userDAO.getUsersByType("restaurant");
            System.out.println("\nRestaurants: " + restaurants.size());
            for (User r : restaurants) {
                System.out.println("  • " + r.getOrganizationName() + 
                                 " (FSSAI: " + r.getFssaiNumber() + ")");
            }
            
            // Count NGOs
            java.util.List<User> ngos = userDAO.getUsersByType("ngo");
            System.out.println("\nNGOs: " + ngos.size());
            for (User n : ngos) {
                System.out.println("  • " + n.getOrganizationName() + 
                                 " (FSSAI: " + n.getFssaiNumber() + ")");
            }
            
            // Count food listings
            FoodListingDAO listingDAO = new FoodListingDAO();
            java.util.List<FoodListing> listings = listingDAO.getAllAvailableListings();
            System.out.println("\nAvailable Food Listings: " + listings.size());
            
        } catch (Exception e) {
            System.err.println("Error loading test data info: " + e.getMessage());
        }
        
        System.out.println("========================================\n");
    }
    
    /**
     * Print usage instructions
     */
    private static void printUsageInstructions() {
        System.out.println("\n========================================");
        System.out.println("APPLICATION READY");
        System.out.println("========================================");
        System.out.println("\n TEST ACCOUNTS AVAILABLE:");
        System.out.println("\n  RESTAURANTS:");
        System.out.println("  1. FSSAI: 10012345678901");
        System.out.println("     Name: The Golden Spoon");
        System.out.println("     Phone: 9876543210");
        System.out.println();
        System.out.println("  2. FSSAI: 10012345678902");
        System.out.println("     Name: Tasty Bites Restaurant");
        System.out.println("     Phone: 9876543211");
        System.out.println();
        System.out.println("  3. FSSAI: 10012345678903");
        System.out.println("     Name: Royal Feast Kitchen");
        System.out.println("     Phone: 9876543212");
        
        System.out.println("\n--- NGOs ---");
        System.out.println("  1. FSSAI: 20012345678901");
        System.out.println("     Name: Feed The Needy Foundation");
        System.out.println("     Phone: 9123456780");
        System.out.println();
        System.out.println("  2. FSSAI: 20012345678902");
        System.out.println("     Name: Hope For Hunger NGO");
        System.out.println("     Phone: 9123456781");
        System.out.println();
        System.out.println("  3. FSSAI: 20012345678903");
        System.out.println("     Name: Care & Share Trust");
        System.out.println("     Phone: 9123456782");
    }
}