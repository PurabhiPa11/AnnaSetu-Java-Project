package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Database Connection Manager
 * Handles connections to Oracle 10g XE database
 */
public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:oracle:thin:sys/J001@localhost:1521:XE?internal_logon=SYSDBA";
    private static final String DB_USER = "SYS";
    private static final String DB_PASSWORD = "J001";
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Database connection established");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Oracle JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database");
            e.printStackTrace();
        }
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null || !isConnectionValid()) {
            synchronized (DatabaseConnection.class) {
                if (instance == null || !isConnectionValid()) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    private static boolean isConnectionValid() {
        try {
            return instance != null && 
                   instance.connection != null && 
                   !instance.connection.isClosed() &&
                   instance.connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Database connection re-established");
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to get valid connection");
            e.printStackTrace();
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection");
            e.printStackTrace();
        }
    }
    
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }
}