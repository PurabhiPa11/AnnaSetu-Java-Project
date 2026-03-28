package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 * Database Setup Script for Restaurant-NGO Food Distribution System
 * Run this file once to create all tables, sequences, and sample data
 * Creates 50 total accounts (25 restaurants + 25 NGOs)
 */
public class DatabaseSetup {
    
    // Database connection details for Oracle 10g XE
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE?internal_logon=SYSDBA";
    private static final String DB_USER = "SYS";
    private static final String DB_PASSWORD = "J001";
    
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("Restaurant-NGO Food Distribution System");
        System.out.println("Database Setup Starting...");
        System.out.println("==============================================\n");
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Load Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("✓ Oracle JDBC Driver loaded successfully");
            
            // Establish connection
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Manual commit for transaction control
            System.out.println("✓ Connected to Oracle Database\n");
            
            stmt = conn.createStatement();
            
            // Step 1: Drop existing tables (if they exist)
            System.out.println("Step 1: Cleaning up existing tables...");
            dropTablesIfExist(stmt);
            
            // Step 2: Create tables
            System.out.println("\nStep 2: Creating tables...");
            createTables(stmt);
            
            // Step 3: Insert sample data
            System.out.println("\nStep 3: Inserting sample data...");
            insertSampleData(conn);
            
            // Commit all changes
            conn.commit();
            System.out.println("\n==============================================");
            System.out.println("✓ Database setup completed successfully!");
            System.out.println("==============================================\n");
            
            // Display summary
            displaySummary();
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Error: Oracle JDBC Driver not found!");
            System.err.println("  Please add ojdbc6.jar or ojdbc14.jar to your classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database Error: " + e.getMessage());
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
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("\n✓ Database connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Drop existing tables and sequences if they exist
     */
    private static void dropTablesIfExist(Statement stmt) throws SQLException {
        String[] dropStatements = {
            "DROP TABLE requests CASCADE CONSTRAINTS",
            "DROP TABLE food_listings CASCADE CONSTRAINTS",
            "DROP TABLE users CASCADE CONSTRAINTS",
            "DROP SEQUENCE listing_id_seq",
            "DROP SEQUENCE request_id_seq"
        };
        
        for (String sql : dropStatements) {
            try {
                stmt.execute(sql);
                System.out.println("  ✓ Dropped: " + sql.split(" ")[2]);
            } catch (SQLException e) {
                // Ignore errors if table/sequence doesn't exist
                if (!e.getMessage().contains("does not exist")) {
                    System.out.println("  ⚠ Warning: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Create all required tables and sequences
     */
    private static void createTables(Statement stmt) throws SQLException {
        
        // Create USERS table
        String createUsersTable = 
            "CREATE TABLE users (" +
            "    fssai_number VARCHAR2(14) PRIMARY KEY," +
            "    user_type VARCHAR2(20) NOT NULL CHECK (user_type IN ('restaurant', 'ngo'))," +
            "    organization_name VARCHAR2(200) NOT NULL," +
            "    phone_number VARCHAR2(15) NOT NULL," +
            "    email VARCHAR2(100)," +
            "    address VARCHAR2(500)," +
            "    otp VARCHAR2(6)," +
            "    otp_generated_at TIMESTAMP," +
            "    otp_verified CHAR(1) DEFAULT 'N' CHECK (otp_verified IN ('Y', 'N'))," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    last_login TIMESTAMP" +
            ")";
        stmt.execute(createUsersTable);
        System.out.println("  ✓ Created: users table");
        
        // Create indexes for users
        stmt.execute("CREATE INDEX idx_users_phone ON users(phone_number)");
        stmt.execute("CREATE INDEX idx_users_type ON users(user_type)");
        System.out.println("  ✓ Created: users indexes");
        
        // Create sequence for listing_id
        stmt.execute("CREATE SEQUENCE listing_id_seq START WITH 1 INCREMENT BY 1");
        System.out.println("  ✓ Created: listing_id_seq sequence");
        
        // Create FOOD_LISTINGS table
        String createFoodListingsTable = 
            "CREATE TABLE food_listings (" +
            "    listing_id NUMBER PRIMARY KEY," +
            "    restaurant_fssai VARCHAR2(14) NOT NULL," +
            "    food_name VARCHAR2(200) NOT NULL," +
            "    description VARCHAR2(1000)," +
            "    quantity VARCHAR2(100) NOT NULL," +
            "    expiry_time TIMESTAMP NOT NULL," +
            "    status VARCHAR2(20) DEFAULT 'available' " +
            "        CHECK (status IN ('available', 'accepted', 'expired', 'cancelled'))," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    CONSTRAINT fk_listing_restaurant FOREIGN KEY (restaurant_fssai) " +
            "        REFERENCES users(fssai_number) ON DELETE CASCADE" +
            ")";
        stmt.execute(createFoodListingsTable);
        System.out.println("  ✓ Created: food_listings table");
        
        // Create indexes for food_listings
        stmt.execute("CREATE INDEX idx_listings_restaurant ON food_listings(restaurant_fssai)");
        stmt.execute("CREATE INDEX idx_listings_status ON food_listings(status)");
        stmt.execute("CREATE INDEX idx_listings_expiry ON food_listings(expiry_time)");
        System.out.println("  ✓ Created: food_listings indexes");
        
        // Create sequence for request_id
        stmt.execute("CREATE SEQUENCE request_id_seq START WITH 1 INCREMENT BY 1");
        System.out.println("  ✓ Created: request_id_seq sequence");
        
        // Create REQUESTS table
        String createRequestsTable = 
            "CREATE TABLE requests (" +
            "    request_id NUMBER PRIMARY KEY," +
            "    listing_id NUMBER NOT NULL," +
            "    ngo_fssai VARCHAR2(14) NOT NULL," +
            "    status VARCHAR2(20) DEFAULT 'accepted' " +
            "        CHECK (status IN ('accepted', 'declined', 'completed'))," +
            "    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    notes VARCHAR2(500)," +
            "    CONSTRAINT fk_request_listing FOREIGN KEY (listing_id) " +
            "        REFERENCES food_listings(listing_id) ON DELETE CASCADE," +
            "    CONSTRAINT fk_request_ngo FOREIGN KEY (ngo_fssai) " +
            "        REFERENCES users(fssai_number) ON DELETE CASCADE" +
            ")";
        stmt.execute(createRequestsTable);
        System.out.println("  ✓ Created: requests table");
        
        // Create indexes for requests
        stmt.execute("CREATE INDEX idx_requests_listing ON requests(listing_id)");
        stmt.execute("CREATE INDEX idx_requests_ngo ON requests(ngo_fssai)");
        stmt.execute("CREATE INDEX idx_requests_status ON requests(status)");
        System.out.println("  ✓ Created: requests indexes");
    }
    
    /**
     * Insert sample data for testing using PreparedStatement
     */
    private static void insertSampleData(Connection conn) throws SQLException {
        
        // Arrays for generating diverse data
        String[] restaurantNames = {
            "The Golden Spoon", "Tasty Bites Restaurant", "Royal Feast Kitchen",
            "Spice Garden", "Urban Tadka", "Flavors of India", "The Hungry Chef",
            "Curry House", "Fusion Delight", "Paradise Restaurant", "Food Junction",
            "Green Leaf Cafe", "Heritage Kitchen", "Masala Magic", "Desi Dhaba",
            "Continental Cuisine", "Saffron Restaurant", "Pearl Dining", "Lotus Kitchen",
            "The Food Factory", "Bistro Express", "Corner Cafe", "Gourmet Hub",
            "Palace Kitchen", "Metro Bites"
        };
        
        String[] ngoNames = {
            "Feed The Needy Foundation", "Hope For Hunger NGO", "Care & Share Trust",
            "Helping Hands Foundation", "Food For All Mission", "Serve The Poor Trust",
            "Compassion Care NGO", "United We Feed", "Hunger Relief Society",
            "Share A Meal Foundation", "Community Kitchen Trust", "Feeding Hope NGO",
            "Charity Meals Foundation", "Nourish India Trust", "Food Angels NGO",
            "Support & Sustain Foundation", "Kindness Kitchen", "Feed Forward NGO",
            "Meal Share Trust", "Humanity First Foundation", "Food Bridge NGO",
            "Care Givers Trust", "Nutrition For All", "Seva Foundation",
            "Food Distribution Network"
        };
        
        String[] areas = {
            "MG Road", "FC Road", "Koregaon Park", "Shivaji Nagar", "Kothrud",
            "Deccan", "Camp Area", "Wakad", "Hinjewadi", "Baner", "Aundh",
            "Viman Nagar", "Kharadi", "Hadapsar", "Katraj", "Warje", "Karve Nagar",
            "Pimpri", "Chinchwad", "Nigdi", "Akurdi", "Bhosari", "Dhanori",
            "Wagholi", "Kondhwa"
        };
        
        // Insert 25 Restaurants using PreparedStatement
        System.out.println("\n  Inserting 25 Restaurants...");
        String insertUserSql = "INSERT INTO users (fssai_number, user_type, organization_name, " +
                              "phone_number, email, address, otp_verified) VALUES (?, ?, ?, ?, ?, ?, 'Y')";
        
        PreparedStatement pstmt = conn.prepareStatement(insertUserSql);
        
        for (int i = 0; i < 25; i++) {
            String fssai = String.format("100123456789%02d", i + 1);
            String phone = String.format("98765432%02d", i + 10);
            String email = restaurantNames[i].toLowerCase().replaceAll("[^a-z]", "") + "@restaurant.com";
            String address = (100 + i * 5) + " " + areas[i % areas.length] + ", Pune";
            
            pstmt.setString(1, fssai);
            pstmt.setString(2, "restaurant");
            pstmt.setString(3, restaurantNames[i]);
            pstmt.setString(4, phone);
            pstmt.setString(5, email);
            pstmt.setString(6, address);
            pstmt.executeUpdate();
            
            if ((i + 1) % 5 == 0) {
                System.out.println("    ✓ Added " + (i + 1) + " restaurants...");
            }
        }
        System.out.println("    ✓ All 25 restaurants added successfully!");
        
        // Insert 25 NGOs
        System.out.println("\n  Inserting 25 NGOs...");
        for (int i = 0; i < 25; i++) {
            String fssai = String.format("200123456789%02d", i + 1);
            String phone = String.format("91234567%02d", i + 80);
            String email = ngoNames[i].toLowerCase().replaceAll("[^a-z]", "") + "@ngo.org";
            String address = (200 + i * 5) + " " + areas[i % areas.length] + ", Pune";
            
            pstmt.setString(1, fssai);
            pstmt.setString(2, "ngo");
            pstmt.setString(3, ngoNames[i]);
            pstmt.setString(4, phone);
            pstmt.setString(5, email);
            pstmt.setString(6, address);
            pstmt.executeUpdate();
            
            if ((i + 1) % 5 == 0) {
                System.out.println("    ✓ Added " + (i + 1) + " NGOs...");
            }
        }
        System.out.println("    ✓ All 25 NGOs added successfully!");
        pstmt.close();
        
        // Insert Food Listings (from all 25 restaurants with varying amounts)
        System.out.println("\n  Inserting Food Listings...");
        String[] foodItems = {
            "Vegetable Biryani", "Mixed Curry", "Fresh Rotis", "Dal Tadka",
            "Pasta Arrabiata", "Paneer Butter Masala", "Fried Rice", "Noodles",
            "Mixed Vegetables", "Pulao Rice", "Sambar Rice", "Curd Rice",
            "Chapati", "Puri Sabzi", "Idli Sambar", "Dosa", "Vada", "Upma",
            "Khichdi", "Rajma Chawal", "Veg Pulao", "Jeera Rice", "Plain Rice",
            "Butter Naan", "Garlic Naan", "Tandoori Roti", "Paratha", "Aloo Paratha",
            "Chole Bhature", "Pav Bhaji", "Misal Pav", "Vada Pav", "Sandwich",
            "Poha", "Upma", "Sabudana Khichdi", "Masala Dosa", "Uttapam",
            "Idli", "Medu Vada", "Sambar", "Rasam", "Tomato Rice", "Lemon Rice"
        };
        
        String[] descriptions = {
            "Fresh and aromatic", "Home-style preparation", "Freshly made today",
            "Traditional recipe", "Chef special dish", "Authentic taste",
            "Healthy and nutritious", "Spicy and flavorful", "Mild and tasty",
            "Perfect for distribution", "Made with fresh ingredients", "Hot and ready",
            "Surplus from lunch service", "Evening batch ready", "Fresh from kitchen",
            "Buffet leftovers - good quality", "Special event surplus", "Catering extra"
        };
        
        String[] quantities = {
            "5 kg", "3 liters", "50 pieces", "4 liters", "2 kg", "6 kg",
            "100 pieces", "8 liters", "10 kg", "4 kg", "15 kg", "7 liters",
            "75 pieces", "12 kg", "5 liters", "150 pieces", "20 kg", "9 liters",
            "200 pieces", "25 kg"
        };
        
        String insertListingSql = "INSERT INTO food_listings (listing_id, restaurant_fssai, " +
                                 "food_name, description, quantity, expiry_time, status) " +
                                 "VALUES (listing_id_seq.NEXTVAL, ?, ?, ?, ?, " +
                                 "SYSTIMESTAMP + NUMTODSINTERVAL(?, 'HOUR'), 'available')";
        
        pstmt = conn.prepareStatement(insertListingSql);
        
        int totalListings = 0;
        
        // Add 2-4 listings per restaurant (varying amounts)
        for (int r = 1; r <= 25; r++) {
            String restaurantFssai = String.format("100123456789%02d", r);
            int listingsPerRestaurant = 2 + (r % 3); // 2, 3, or 4 listings per restaurant
            
            for (int l = 0; l < listingsPerRestaurant; l++) {
                int foodIndex = (r * 3 + l) % foodItems.length;
                int descIndex = (r + l) % descriptions.length;
                int qtyIndex = (r * 2 + l) % quantities.length;
                int hoursValid = 2 + ((r + l) % 6); // 2 to 7 hours validity
                
                pstmt.setString(1, restaurantFssai);
                pstmt.setString(2, foodItems[foodIndex]);
                pstmt.setString(3, descriptions[descIndex]);
                pstmt.setString(4, quantities[qtyIndex]);
                pstmt.setInt(5, hoursValid);
                pstmt.executeUpdate();
                totalListings++;
            }
            
            if (r % 5 == 0) {
                System.out.println("    ✓ Added listings from " + r + " restaurants...");
            }
        }
        System.out.println("    ✓ Total " + totalListings + " food listings added from all 25 restaurants");
        pstmt.close();
        
        // Insert Sample Requests (NGOs accepting food listings)
        System.out.println("\n  Inserting Sample Requests...");
        String insertRequestSql = "INSERT INTO requests (request_id, listing_id, ngo_fssai, " +
                                 "status, notes) VALUES (request_id_seq.NEXTVAL, ?, ?, ?, ?)";
        
        String updateListingSql = "UPDATE food_listings SET status = ? WHERE listing_id = ?";
        
        PreparedStatement reqPstmt = conn.prepareStatement(insertRequestSql);
        PreparedStatement updPstmt = conn.prepareStatement(updateListingSql);
        
        String[] requestNotes = {
            "Will pick up within 1 hour",
            "Our vehicle is on the way",
            "Team ready for pickup in 30 minutes",
            "Scheduled pickup at 6 PM",
            "Emergency distribution - urgent pickup",
            "Regular pickup schedule",
            "Will collect by evening",
            "Pickup arranged for today",
            "Coming to collect soon",
            "Distribution planned for tonight"
        };
        
        // Create 20 accepted requests (some NGOs take multiple listings)
        int requestCount = 0;
        for (int i = 1; i <= 20; i++) {
            int ngoIndex = ((i - 1) % 15) + 1; // First 15 NGOs get requests
            String ngoFssai = String.format("200123456789%02d", ngoIndex);
            String notes = requestNotes[i % requestNotes.length];
            
            reqPstmt.setInt(1, i);
            reqPstmt.setString(2, ngoFssai);
            reqPstmt.setString(3, "accepted");
            reqPstmt.setString(4, notes);
            reqPstmt.executeUpdate();
            
            updPstmt.setString(1, "accepted");
            updPstmt.setInt(2, i);
            updPstmt.executeUpdate();
            requestCount++;
        }
        
        // Create 5 completed requests (picked up and distributed)
        for (int i = 21; i <= 25; i++) {
            int ngoIndex = ((i - 1) % 10) + 1;
            String ngoFssai = String.format("200123456789%02d", ngoIndex);
            
            reqPstmt.setInt(1, i);
            reqPstmt.setString(2, ngoFssai);
            reqPstmt.setString(3, "completed");
            reqPstmt.setString(4, "Food collected and distributed successfully");
            reqPstmt.executeUpdate();
            
            updPstmt.setString(1, "accepted");
            updPstmt.setInt(2, i);
            updPstmt.executeUpdate();
            requestCount++;
        }
        
        System.out.println("    ✓ Added " + requestCount + " sample requests");
        System.out.println("      - 20 accepted (pending pickup)");
        System.out.println("      - 5 completed (distributed)");
        
        reqPstmt.close();
        updPstmt.close();
    }
    
    /**
     * Display summary
     */
    private static void displaySummary() {
        System.out.println("\n==============================================");
        System.out.println("Database Setup Summary:");
        System.out.println("==============================================");
        System.out.println("✓ Total Accounts Created: 50");
        System.out.println("  - Restaurants: 25 (each with 2-4 food listings)");
        System.out.println("  - NGOs: 25");
        System.out.println("✓ Food Listings: ~70+ (from all restaurants)");
        System.out.println("✓ Sample Requests: 25");
        System.out.println("  - 20 Accepted (pending pickup)");
        System.out.println("  - 5 Completed (distributed)");
        System.out.println("\n==============================================");
        System.out.println("Sample Login Credentials:");
        System.out.println("==============================================");
        System.out.println("\nRESTAURANTS (All have active listings):");
        System.out.println("  1. The Golden Spoon");
        System.out.println("     FSSAI: 10012345678901  Phone: 9876543210");
        System.out.println("  2. Tasty Bites Restaurant");
        System.out.println("     FSSAI: 10012345678902  Phone: 9876543211");
        System.out.println("  3. Royal Feast Kitchen");
        System.out.println("     FSSAI: 10012345678903  Phone: 9876543212");
        System.out.println("  4. Spice Garden");
        System.out.println("     FSSAI: 10012345678904  Phone: 9876543213");
        System.out.println("  5. Urban Tadka");
        System.out.println("     FSSAI: 10012345678905  Phone: 9876543214");
        System.out.println("  ... (20 more restaurants)");
        
        System.out.println("\nNGOs (First 15 have active requests):");
        System.out.println("  1. Feed The Needy Foundation");
        System.out.println("     FSSAI: 20012345678901  Phone: 9123456780");
        System.out.println("  2. Hope For Hunger NGO");
        System.out.println("     FSSAI: 20012345678902  Phone: 9123456781");
        System.out.println("  3. Care & Share Trust");
        System.out.println("     FSSAI: 20012345678903  Phone: 9123456782");
        System.out.println("  4. Helping Hands Foundation");
        System.out.println("     FSSAI: 20012345678904  Phone: 9123456783");
        System.out.println("  5. Food For All Mission");
        System.out.println("     FSSAI: 20012345678905  Phone: 9123456784");
        System.out.println("  ... (20 more NGOs)");
        
        System.out.println("\n==============================================");
        System.out.println("Data Distribution:");
        System.out.println("==============================================");
        System.out.println("• All 50 accounts are OTP verified and ready to use");
        System.out.println("• All 25 restaurants have active food listings");
        System.out.println("• 25 food listings are 'available' for NGOs to accept");
        System.out.println("• 20 listings are 'accepted' (NGO coming to pickup)");
        System.out.println("• 5 requests are 'completed' (food distributed)");
        System.out.println("• Listings expire between 2-7 hours from now");
        System.out.println("==============================================");
        System.out.println("\nTip: Login as any restaurant or NGO to see");
        System.out.println("     realistic data in the system!");
        System.out.println("==============================================");
    }
}