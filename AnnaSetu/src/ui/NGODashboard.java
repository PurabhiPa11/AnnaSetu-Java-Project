package ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import models.FoodListing;
import models.Request;
import services.AuthService;
import services.ListingService;
import services.RequestService;

/**
 * NGO Dashboard - Complete Implementation
 * Allows NGOs to browse and accept food listings
 */
public class NGODashboard extends JPanel {
    
    private AppFrame appFrame;
    private AuthService authService;
    private ListingService listingService;
    private RequestService requestService;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable availableListingsTable;
    private JTable myRequestsTable;
    private DefaultTableModel availableListingsModel;
    private DefaultTableModel myRequestsModel;
    private JLabel statsLabel;
    private JLabel lastUpdateLabel;
    private JButton refreshButton;
    private JButton logoutButton;
    
    // Auto-refresh
    private Timer refreshTimer;
    private SimpleDateFormat dateFormat;
    
    public NGODashboard(AppFrame appFrame, AuthService authService) {
        this.appFrame = appFrame;
        this.authService = authService;
        this.listingService = new ListingService();
        this.requestService = new RequestService();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        
        initializeUI();
        loadData();
        startAutoRefresh();
    }
    
    /**
     * Initialize the UI
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_PRIMARY);
        
        // Header
        add(createHeader(), BorderLayout.NORTH);
        
        // Main content (tabbed pane)
        add(createMainContent(), BorderLayout.CENTER);
        
        // Footer (stats)
        add(createFooter(), BorderLayout.SOUTH);
    }
    
    /**
     * Create header panel
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.BG_SECONDARY);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, UIConstants.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        header.setPreferredSize(new Dimension(0, 100));
        header.setMinimumSize(new Dimension(0, 100));
        
        // Left: Title and subtitle
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(UIConstants.BG_SECONDARY);
        
        JLabel titleLabel = ComponentFactory.createTitle("NGO Dashboard");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(titleLabel);
        
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        JLabel subtitleLabel = ComponentFactory.createSecondaryLabel(
            "Welcome, " + authService.getOrganizationName()
        );
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(subtitleLabel);
        
        header.add(leftPanel, BorderLayout.WEST);
        
        // Right: Action buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setBackground(UIConstants.BG_SECONDARY);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        refreshButton = ComponentFactory.createOutlineButton("🔄 Refresh");
        refreshButton.setMinimumSize(new Dimension(120, 40));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setMaximumSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> refreshData());
        rightPanel.add(refreshButton);
        
        rightPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        logoutButton = ComponentFactory.createSecondaryButton("Logout");
        logoutButton.setMinimumSize(new Dimension(100, 40));
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.setMaximumSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> logout());
        rightPanel.add(logoutButton);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Create main content panel with tabbed interface
     */
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BG_PRIMARY);
        mainPanel.setBorder(UIConstants.PADDING_NORMAL);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.FONT_NORMAL_BOLD);
        tabbedPane.setBackground(UIConstants.BG_SECONDARY);
        
        // Tab 1: Available Food Listings
        JPanel availableTab = createAvailableListingsTab();
        tabbedPane.addTab("Available Food", null, availableTab, "Browse available food listings");
        
        // Tab 2: My Accepted Requests
        JPanel requestsTab = createMyRequestsTab();
        tabbedPane.addTab("My Accepted Requests", null, requestsTab, "View your accepted food requests");
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    /**
     * Create Available Listings tab
     */
    private JPanel createAvailableListingsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_PRIMARY);
        panel.setBorder(UIConstants.PADDING_SMALL);
        
        // Title with last update time
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.BG_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitle = ComponentFactory.createHeading("Available Food from Restaurants");
        titlePanel.add(tableTitle, BorderLayout.WEST);
        
        lastUpdateLabel = ComponentFactory.createSecondaryLabel("Last updated: Never");
        titlePanel.add(lastUpdateLabel, BorderLayout.EAST);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"ID", "Restaurant", "Food Name", "Quantity", 
                           "Description", "Expiry Time", "Status", "Actions"};
        
        availableListingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };
        
        availableListingsTable = new JTable(availableListingsModel);
        availableListingsTable.setFont(UIConstants.FONT_NORMAL);
        availableListingsTable.setRowHeight(50);
        availableListingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        availableListingsTable.getTableHeader().setFont(UIConstants.FONT_NORMAL_BOLD);
        availableListingsTable.getTableHeader().setBackground(UIConstants.BG_SECONDARY);
        availableListingsTable.getTableHeader().setForeground(UIConstants.TEXT_PRIMARY);
        availableListingsTable.setGridColor(UIConstants.BORDER_LIGHT);
        availableListingsTable.setShowGrid(true);
        
        // Set column widths
        TableColumnModel columnModel = availableListingsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // Restaurant
        columnModel.getColumn(2).setPreferredWidth(150); // Food Name
        columnModel.getColumn(3).setPreferredWidth(100); // Quantity
        columnModel.getColumn(4).setPreferredWidth(200); // Description
        columnModel.getColumn(5).setPreferredWidth(150); // Expiry Time
        columnModel.getColumn(6).setPreferredWidth(100); // Status
        columnModel.getColumn(7).setPreferredWidth(150); // Actions
        
        // Custom renderer for Status column
        availableListingsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                label.setFont(UIConstants.FONT_SMALL_BOLD);
                label.setForeground(UIConstants.TEXT_LIGHT);
                label.setBackground(UIConstants.getStatusColor(status));
                label.setText(UIConstants.getStatusDisplay(status));
                
                return label;
            }
        });
        
        // Custom renderer for Actions column
        availableListingsTable.getColumnModel().getColumn(7).setCellRenderer(
            new AvailableButtonRenderer()
        );
        availableListingsTable.getColumnModel().getColumn(7).setCellEditor(
            new AvailableButtonEditor(new JCheckBox())
        );
        
        // Center align some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        availableListingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        availableListingsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Quantity
        
        JScrollPane scrollPane = new JScrollPane(availableListingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create My Requests tab
     */
    private JPanel createMyRequestsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BG_PRIMARY);
        panel.setBorder(UIConstants.PADDING_SMALL);
        
        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.BG_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitle = ComponentFactory.createHeading("My Accepted Food Requests");
        titlePanel.add(tableTitle, BorderLayout.WEST);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Request ID", "Food Name", "Quantity", "Restaurant", 
                           "Restaurant Phone", "Accepted On", "Status", "Actions"};
        
        myRequestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };
        
        myRequestsTable = new JTable(myRequestsModel);
        myRequestsTable.setFont(UIConstants.FONT_NORMAL);
        myRequestsTable.setRowHeight(50);
        myRequestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myRequestsTable.getTableHeader().setFont(UIConstants.FONT_NORMAL_BOLD);
        myRequestsTable.getTableHeader().setBackground(UIConstants.BG_SECONDARY);
        myRequestsTable.getTableHeader().setForeground(UIConstants.TEXT_PRIMARY);
        myRequestsTable.setGridColor(UIConstants.BORDER_LIGHT);
        myRequestsTable.setShowGrid(true);
        
        // Set column widths
        TableColumnModel columnModel = myRequestsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Request ID
        columnModel.getColumn(1).setPreferredWidth(150); // Food Name
        columnModel.getColumn(2).setPreferredWidth(100); // Quantity
        columnModel.getColumn(3).setPreferredWidth(150); // Restaurant
        columnModel.getColumn(4).setPreferredWidth(120); // Phone
        columnModel.getColumn(5).setPreferredWidth(150); // Accepted On
        columnModel.getColumn(6).setPreferredWidth(100); // Status
        columnModel.getColumn(7).setPreferredWidth(150); // Actions
        
        // Custom renderer for Status column
        myRequestsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                String status = value.toString();
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                label.setFont(UIConstants.FONT_SMALL_BOLD);
                label.setForeground(UIConstants.TEXT_LIGHT);
                label.setBackground(UIConstants.getStatusColor(status));
                label.setText(UIConstants.getStatusDisplay(status));
                
                return label;
            }
        });
        
        // Custom renderer for Actions column
        myRequestsTable.getColumnModel().getColumn(7).setCellRenderer(
            new RequestButtonRenderer()
        );
        myRequestsTable.getColumnModel().getColumn(7).setCellEditor(
            new RequestButtonEditor(new JCheckBox())
        );
        
        // Center align some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        myRequestsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Request ID
        myRequestsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Quantity
        
        JScrollPane scrollPane = new JScrollPane(myRequestsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create footer panel with statistics
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UIConstants.BG_SECONDARY);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, UIConstants.BORDER_LIGHT),
            UIConstants.PADDING_NORMAL
        ));
        footer.setPreferredSize(new Dimension(0, UIConstants.HEIGHT_FOOTER));
        
        statsLabel = ComponentFactory.createLabel("Loading statistics...");
        footer.add(statsLabel, BorderLayout.WEST);
        
        JLabel autoRefreshLabel = ComponentFactory.createSecondaryLabel(
            "Auto-refresh: Every 5 seconds"
        );
        footer.add(autoRefreshLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    /**
     * Load all data
     */
    private void loadData() {
        loadAvailableListings();
        loadMyRequests();
        updateStatistics();
    }
    
    /**
     * Load available listings
     */
    private void loadAvailableListings() {
        availableListingsModel.setRowCount(0);
        
        List<FoodListing> listings = listingService.getAvailableListings();
        
        for (FoodListing listing : listings) {
            Object[] row = {
                listing.getListingId(),
                listing.getRestaurantName(),
                listing.getFoodName(),
                listing.getQuantity(),
                truncateText(listing.getDescription(), 50),
                dateFormat.format(listing.getExpiryTime()),
                listing.getStatus(),
                "Actions"
            };
            availableListingsModel.addRow(row);
        }
        
        lastUpdateLabel.setText("Last updated: " + 
            new SimpleDateFormat("hh:mm:ss a").format(new java.util.Date()));
    }
    
    /**
     * Load my accepted requests
     */
    private void loadMyRequests() {
        myRequestsModel.setRowCount(0);
        
        List<Request> requests = requestService.getNGORequests(authService.getFssaiNumber());
        
        for (Request request : requests) {
            Object[] row = {
                request.getRequestId(),
                request.getFoodName(),
                request.getQuantity(),
                request.getRestaurantName(),
                "Contact Restaurant", // Will show phone in details
                dateFormat.format(request.getRequestedAt()),
                request.getStatus(),
                "Actions"
            };
            myRequestsModel.addRow(row);
        }
    }
    
    /**
     * Update statistics
     */
    private void updateStatistics() {
        int[] stats = requestService.getNGOStats(authService.getFssaiNumber());
        int availableCount = availableListingsModel.getRowCount();
        
        statsLabel.setText(String.format(
            "📊 Statistics: Available Listings: %d  |  My Total Requests: %d  |  Accepted: %d  |  Completed: %d",
            availableCount, stats[0], stats[1], stats[2]
        ));
    }
    
    /**
     * Refresh data manually
     */
    private void refreshData() {
        refreshButton.setEnabled(false);
        refreshButton.setText("Refreshing...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                listingService.refreshListings();
                return null;
            }
            
            @Override
            protected void done() {
                loadData();
                refreshButton.setEnabled(true);
                refreshButton.setText("🔄 Refresh");
            }
        };
        
        worker.execute();
    }
    
    /**
     * Start auto-refresh timer
     */
    private void startAutoRefresh() {
        refreshTimer = new Timer(UIConstants.REFRESH_INTERVAL, e -> {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    listingService.refreshListings();
                    return null;
                }
                
                @Override
                protected void done() {
                    loadData();
                }
            };
            worker.execute();
        });
        refreshTimer.start();
    }
    
    /**
     * Stop auto-refresh timer
     */
    private void stopAutoRefresh() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }
    
    /**
     * Accept a food listing
     */
    private void acceptListing(int row) {
        int listingId = (int) availableListingsModel.getValueAt(row, 0);
        String foodName = (String) availableListingsModel.getValueAt(row, 2);
        String restaurant = (String) availableListingsModel.getValueAt(row, 1);
        
        // Show confirmation dialog with notes input
        String notes = ComponentFactory.showInput(
            this,
            "Accept food listing from " + restaurant + "?\n\n" +
            "Food: " + foodName + "\n\n" +
            "Enter any notes or pickup details (optional):",
            ""
        );
        
        if (notes != null) { // User clicked OK
            String error = requestService.acceptListing(
                authService.getCurrentUser(),
                listingId,
                notes
            );
            
            if (error == null) {
                ComponentFactory.showSuccess(this, 
                    "Food listing accepted successfully!\n\n" +
                    "Please contact the restaurant to coordinate pickup.");
                refreshData();
                
                // Switch to My Requests tab
                tabbedPane.setSelectedIndex(1);
            } else {
                ComponentFactory.showError(this, error);
            }
        }
    }
    
    /**
     * View listing details
     */
    private void viewListingDetails(int row) {
        int listingId = (int) availableListingsModel.getValueAt(row, 0);
        FoodListing listing = listingService.getListingById(listingId);
        
        if (listing == null) {
            ComponentFactory.showError(this, "Listing not found!");
            return;
        }
        
        String details = String.format(
            "Food: %s\n" +
            "Restaurant: %s\n" +
            "Quantity: %s\n" +
            "Description: %s\n" +
            "Expiry: %s\n" +
            "Status: %s\n" +
            "Listed: %s",
            listing.getFoodName(),
            listing.getRestaurantName(),
            listing.getQuantity(),
            listing.getDescription(),
            dateFormat.format(listing.getExpiryTime()),
            UIConstants.getStatusDisplay(listing.getStatus()),
            dateFormat.format(listing.getCreatedAt())
        );
        
        JOptionPane.showMessageDialog(
            this,
            details,
            "Listing Details - #" + listingId,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * View request details
     */
    private void viewRequestDetails(int row) {
        int requestId = (int) myRequestsModel.getValueAt(row, 0);
        List<Request> requests = requestService.getNGORequests(authService.getFssaiNumber());
        
        Request request = null;
        for (Request r : requests) {
            if (r.getRequestId() == requestId) {
                request = r;
                break;
            }
        }
        
        if (request == null) {
            ComponentFactory.showError(this, "Request not found!");
            return;
        }
        
        // Get full listing details
        FoodListing listing = listingService.getListingById(request.getListingId());
        
        String details = String.format(
            "Request ID: %d\n" +
            "Food: %s\n" +
            "Quantity: %s\n" +
            "Restaurant: %s\n" +
            "Restaurant Phone: %s\n" +
            "Accepted On: %s\n" +
            "Status: %s\n" +
            "%s",
            request.getRequestId(),
            request.getFoodName(),
            request.getQuantity(),
            request.getRestaurantName(),
            listing != null ? "Contact via app" : "N/A",
            dateFormat.format(request.getRequestedAt()),
            UIConstants.getStatusDisplay(request.getStatus()),
            request.getNotes() != null && !request.getNotes().isEmpty() ? 
                "\nNotes: " + request.getNotes() : ""
        );
        
        JOptionPane.showMessageDialog(
            this,
            details,
            "Request Details - #" + requestId,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Mark request as completed
     */
    private void completeRequest(int row) {
        int requestId = (int) myRequestsModel.getValueAt(row, 0);
        String foodName = (String) myRequestsModel.getValueAt(row, 1);
        
        boolean confirmed = ComponentFactory.showConfirm(
            this,
            "Mark this request as completed?\n\n" + foodName
        );
        
        if (confirmed) {
            String error = requestService.completeRequest(
                authService.getCurrentUser(),
                requestId
            );
            
            if (error == null) {
                ComponentFactory.showSuccess(this, "Request marked as completed!");
                refreshData();
            } else {
                ComponentFactory.showError(this, error);
            }
        }
    }
    
    /**
     * Logout
     */
    private void logout() {
        boolean confirmed = ComponentFactory.showConfirm(
            this,
            "Are you sure you want to logout?"
        );
        
        if (confirmed) {
            stopAutoRefresh();
            authService.logout();
            appFrame.showPanel("homepage");
            appFrame.setCustomTitle(null);
        }
    }
    
    /**
     * Truncate text
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    // ==================== BUTTON RENDERERS/EDITORS ====================
    
    /**
     * Button renderer for available listings
     */
    class AvailableButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        
        private JButton viewBtn;
        private JButton acceptBtn;
        
        public AvailableButtonRenderer() {
            setLayout(new GridLayout(1, 2, 5, 0));
            setBackground(UIConstants.BG_SECONDARY);
            setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.FONT_SMALL);
            viewBtn.setBackground(UIConstants.INFO);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setOpaque(true);
            viewBtn.setBorderPainted(false);
            
            acceptBtn = new JButton("Accept");
            acceptBtn.setFont(UIConstants.FONT_SMALL_BOLD);
            acceptBtn.setBackground(UIConstants.SUCCESS);
            acceptBtn.setForeground(Color.WHITE);
            acceptBtn.setOpaque(true);
            acceptBtn.setBorderPainted(false);
            
            add(viewBtn);
            add(acceptBtn);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Button editor for available listings
     */
    class AvailableButtonEditor extends DefaultCellEditor {
        
        private JPanel panel;
        private JButton viewBtn;
        private JButton acceptBtn;
        private int currentRow;
        
        public AvailableButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setBackground(UIConstants.BG_SECONDARY);
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.FONT_SMALL);
            viewBtn.setPreferredSize(new Dimension(65, 30));
            viewBtn.setBackground(UIConstants.INFO);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setOpaque(true);
            viewBtn.setBorderPainted(false);
            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                viewListingDetails(currentRow);
            });
            
            acceptBtn = new JButton("Accept");
            acceptBtn.setFont(UIConstants.FONT_SMALL_BOLD);
            acceptBtn.setPreferredSize(new Dimension(80, 30));
            acceptBtn.setBackground(UIConstants.SUCCESS);
            acceptBtn.setForeground(Color.WHITE);
            acceptBtn.setOpaque(true);
            acceptBtn.setBorderPainted(false);
            acceptBtn.addActionListener(e -> {
                fireEditingStopped();
                acceptListing(currentRow);
            });
            
            panel.add(viewBtn);
            panel.add(acceptBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
    
    /**
     * Button renderer for my requests
     */
    class RequestButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        
        private JButton viewBtn;
        private JButton completeBtn;
        
        public RequestButtonRenderer() {
            setLayout(new GridLayout(1, 2, 5, 0));
            setBackground(UIConstants.BG_SECONDARY);
            setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.FONT_SMALL);
            viewBtn.setBackground(UIConstants.INFO);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setOpaque(true);
            viewBtn.setBorderPainted(false);
            
            completeBtn = new JButton("Complete");
            completeBtn.setFont(UIConstants.FONT_SMALL);
            completeBtn.setBackground(UIConstants.PRIMARY);
            completeBtn.setForeground(Color.WHITE);
            completeBtn.setOpaque(true);
            completeBtn.setBorderPainted(false);
            
            add(viewBtn);
            add(completeBtn);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Enable/disable complete button based on status
            String status = (String) table.getValueAt(row, 6);
            completeBtn.setEnabled("accepted".equalsIgnoreCase(status));
            
            return this;
        }
    }
    
    /**
     * Button editor for my requests
     */
    class RequestButtonEditor extends DefaultCellEditor {
        
        private JPanel panel;
        private JButton viewBtn;
        private JButton completeBtn;
        private int currentRow;
        
        public RequestButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            
            panel = new JPanel(new GridLayout(1, 2, 5, 0));
            panel.setBackground(UIConstants.BG_SECONDARY);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.FONT_SMALL);
            viewBtn.setBackground(UIConstants.INFO);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setOpaque(true);
            viewBtn.setBorderPainted(false);
            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                viewRequestDetails(currentRow);
            });
            
            completeBtn = new JButton("Complete");
            completeBtn.setFont(UIConstants.FONT_SMALL);
            completeBtn.setBackground(UIConstants.PRIMARY);
            completeBtn.setForeground(Color.WHITE);
            completeBtn.setOpaque(true);
            completeBtn.setBorderPainted(false);
            completeBtn.addActionListener(e -> {
                fireEditingStopped();
                completeRequest(currentRow);
            });
            
            panel.add(viewBtn);
            panel.add(completeBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            currentRow = row;
            
            // Enable/disable complete button based on status
            String status = (String) table.getValueAt(row, 6);
            completeBtn.setEnabled("accepted".equalsIgnoreCase(status));
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}