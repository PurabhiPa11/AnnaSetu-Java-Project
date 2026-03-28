package ui;

import services.AuthService;
import services.ListingService;
import models.FoodListing;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Restaurant Dashboard - Full Implementation
 * Allows restaurants to manage their food listings
 */
public class RestaurantDashboard extends JPanel {
    
    private AppFrame appFrame;
    private AuthService authService;
    private ListingService listingService;
    
    // UI Components
    private JTable listingsTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;
    private JLabel lastUpdateLabel;
    private JButton refreshButton;
    private JButton addListingButton;
    private JButton logoutButton;
    
    // Auto-refresh
    private Timer refreshTimer;
    private SimpleDateFormat dateFormat;
    
    public RestaurantDashboard(AppFrame appFrame, AuthService authService) {
        this.appFrame = appFrame;
        this.authService = authService;
        this.listingService = new ListingService();
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        
        initializeUI();
        loadListings();
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
        
        // Main content (table)
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
        
        JLabel titleLabel = ComponentFactory.createTitle("Restaurant Dashboard");
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
        
        refreshButton = ComponentFactory.createOutlineButton("Refresh");
        refreshButton.setMinimumSize(new Dimension(120, 40));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.setMaximumSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> refreshListings());
        rightPanel.add(refreshButton);
        
        rightPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        addListingButton = ComponentFactory.createPrimaryButton("+ Add Food Listing");
        addListingButton.setMinimumSize(new Dimension(160, 40));
        addListingButton.setPreferredSize(new Dimension(160, 40));
        addListingButton.setMaximumSize(new Dimension(160, 40));
        addListingButton.addActionListener(e -> showAddListingDialog());
        rightPanel.add(addListingButton);
        
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
     * Create main content panel with table
     */
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BG_PRIMARY);
        mainPanel.setBorder(UIConstants.PADDING_NORMAL);
        
        // Table title with last update time
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIConstants.BG_PRIMARY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitle = ComponentFactory.createHeading("My Food Listings");
        titlePanel.add(tableTitle, BorderLayout.WEST);
        
        lastUpdateLabel = ComponentFactory.createSecondaryLabel("Last updated: Never");
        titlePanel.add(lastUpdateLabel, BorderLayout.EAST);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"ID", "Food Name", "Quantity", "Description", 
                           "Expiry Time", "Status", "Accepted By", "Actions"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };
        
        listingsTable = new JTable(tableModel);
        listingsTable.setFont(UIConstants.FONT_NORMAL);
        listingsTable.setRowHeight(50);
        listingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listingsTable.getTableHeader().setFont(UIConstants.FONT_NORMAL_BOLD);
        listingsTable.getTableHeader().setBackground(UIConstants.BG_SECONDARY);
        listingsTable.getTableHeader().setForeground(UIConstants.TEXT_PRIMARY);
        listingsTable.setGridColor(UIConstants.BORDER_LIGHT);
        listingsTable.setShowGrid(true);
        
        // Set column widths
        TableColumnModel columnModel = listingsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // ID
        columnModel.getColumn(1).setPreferredWidth(150); // Food Name
        columnModel.getColumn(2).setPreferredWidth(100); // Quantity
        columnModel.getColumn(3).setPreferredWidth(200); // Description
        columnModel.getColumn(4).setPreferredWidth(150); // Expiry Time
        columnModel.getColumn(5).setPreferredWidth(100); // Status
        columnModel.getColumn(6).setPreferredWidth(150); // Accepted By
        columnModel.getColumn(7).setPreferredWidth(120); // Actions
        
        // Custom renderer for Status column
        listingsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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
        
        // Custom renderer for Actions column (buttons)
        listingsTable.getColumnModel().getColumn(7).setCellRenderer(
            new ButtonRenderer()
        );
        listingsTable.getColumnModel().getColumn(7).setCellEditor(
            new ButtonEditor(new JCheckBox())
        );
        
        // Center align some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        listingsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        listingsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Quantity
        
        JScrollPane scrollPane = new JScrollPane(listingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
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
     * Load listings from database
     */
    private void loadListings() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get listings
        List<FoodListing> listings = listingService.getRestaurantListings(
            authService.getFssaiNumber()
        );
        
        // Populate table
        for (FoodListing listing : listings) {
            Object[] row = {
                listing.getListingId(),
                listing.getFoodName(),
                listing.getQuantity(),
                truncateText(listing.getDescription(), 50),
                dateFormat.format(listing.getExpiryTime()),
                listing.getStatus(),
                listing.getAcceptedByNGO() != null ? 
                    listing.getAcceptedByNGO() + "\n" + listing.getNgoPhone() : 
                    "-",
                "Actions"
            };
            tableModel.addRow(row);
        }
        
        // Update stats
        updateStatistics();
        
        // Update last update time
        lastUpdateLabel.setText("Last updated: " + 
            new SimpleDateFormat("hh:mm:ss a").format(new java.util.Date()));
    }
    
    /**
     * Update statistics display
     */
    private void updateStatistics() {
        int[] stats = listingService.getRestaurantStats(authService.getFssaiNumber());
        
        statsLabel.setText(String.format(
            "📊 Statistics: Total: %d  |  Available: %d  |  Accepted: %d  |  Expired: %d",
            stats[0], stats[1], stats[2], stats[3]
        ));
    }
    
    /**
     * Refresh listings manually
     */
    private void refreshListings() {
        refreshButton.setEnabled(false);
        refreshButton.setText("Refreshing...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Mark expired listings
                listingService.refreshListings();
                return null;
            }
            
            @Override
            protected void done() {
                loadListings();
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
            // Refresh in background
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    listingService.refreshListings();
                    return null;
                }
                
                @Override
                protected void done() {
                    loadListings();
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
     * Show Add Listing Dialog
     */
    private void showAddListingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                     "Add Food Listing", true);
        dialog.setMinimumSize(new Dimension(550, 600));
        dialog.setPreferredSize(new Dimension(600, 650));
        dialog.setLocationRelativeTo(this);
        
        // Main container with BorderLayout for better resizing
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIConstants.BG_PRIMARY);
        
        // Form panel with proper layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.BG_PRIMARY);
        formPanel.setBorder(UIConstants.PADDING_LARGE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        // Title
        JLabel titleLabel = ComponentFactory.createTitle("Add New Food Listing");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(titleLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 5, 10);
        
        // Food Name
        gbc.gridwidth = 2;
        JLabel foodNameLabel = ComponentFactory.createLabel("Food Name *");
        formPanel.add(foodNameLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 15, 10);
        JTextField foodNameField = ComponentFactory.createTextField("Enter food name");
        formPanel.add(foodNameField, gbc);
        
        // Quantity
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel quantityLabel = ComponentFactory.createLabel("Quantity *");
        formPanel.add(quantityLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 15, 10);
        JTextField quantityField = ComponentFactory.createTextField("e.g., 5 kg, 20 servings");
        formPanel.add(quantityField, gbc);
        
        // Description
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel descLabel = ComponentFactory.createLabel("Description");
        formPanel.add(descLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 15, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea descArea = ComponentFactory.createTextArea(4, 30);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(0, 100));
        descScroll.setMinimumSize(new Dimension(0, 80));
        formPanel.add(descScroll, gbc);
        
        // Expiry Hours
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel expiryLabel = ComponentFactory.createLabel("Valid For (hours) *");
        formPanel.add(expiryLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 5, 10);
        JTextField expiryField = ComponentFactory.createTextField("e.g., 4 or 6.5");
        formPanel.add(expiryField, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 20, 10);
        JLabel expiryHintLabel = ComponentFactory.createSecondaryLabel(
            "Food will be available for this many hours (max 24)"
        );
        formPanel.add(expiryHintLabel, gbc);
        
        container.add(formPanel, BorderLayout.CENTER);
        
        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(UIConstants.BG_SECONDARY);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_LIGHT));
        
        JButton cancelBtn = ComponentFactory.createOutlineButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        
        JButton submitBtn = ComponentFactory.createPrimaryButton("Create Listing");
        submitBtn.setPreferredSize(new Dimension(140, 40));
        submitBtn.addActionListener(e -> {
            handleAddListing(
                foodNameField.getText(),
                quantityField.getText(),
                descArea.getText(),
                expiryField.getText(),
                dialog
            );
        });
        buttonPanel.add(submitBtn);
        
        container.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(container);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    /**
     * Handle adding new listing
     */
    private void handleAddListing(String foodName, String quantity, 
                                  String description, String hoursStr, JDialog dialog) {
        // Validate expiry hours
        String hoursError = ListingService.validateExpiryHours(hoursStr);
        if (hoursError != null) {
            ComponentFactory.showError(dialog, hoursError);
            return;
        }
        
        // Convert to timestamp
        double hours = Double.parseDouble(hoursStr.trim());
        Timestamp expiryTime = ListingService.hoursToTimestamp(hours);
        
        // Create listing
        String error = listingService.createListing(
            authService.getCurrentUser(),
            foodName,
            quantity,
            description,
            expiryTime
        );
        
        if (error == null) {
            ComponentFactory.showSuccess(dialog, "Food listing created successfully!");
            dialog.dispose();
            refreshListings();
        } else {
            ComponentFactory.showError(dialog, error);
        }
    }
    
    /**
     * Show listing details dialog
     */
    private void showListingDetails(int row) {
        int listingId = (int) tableModel.getValueAt(row, 0);
        FoodListing listing = listingService.getListingById(listingId);
        
        if (listing == null) {
            ComponentFactory.showError(this, "Listing not found!");
            return;
        }
        
        String details = String.format(
            "Food: %s\n" +
            "Quantity: %s\n" +
            "Description: %s\n" +
            "Expiry: %s\n" +
            "Status: %s\n" +
            "Created: %s\n" +
            "%s",
            listing.getFoodName(),
            listing.getQuantity(),
            listing.getDescription(),
            dateFormat.format(listing.getExpiryTime()),
            UIConstants.getStatusDisplay(listing.getStatus()),
            dateFormat.format(listing.getCreatedAt()),
            listing.getAcceptedByNGO() != null ? 
                "\nAccepted by: " + listing.getAcceptedByNGO() + 
                "\nPhone: " + listing.getNgoPhone() : ""
        );
        
        JOptionPane.showMessageDialog(
            this,
            details,
            "Listing Details - #" + listingId,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Cancel a listing
     */
    private void cancelListing(int row) {
        int listingId = (int) tableModel.getValueAt(row, 0);
        String foodName = (String) tableModel.getValueAt(row, 1);
        
        boolean confirmed = ComponentFactory.showConfirm(
            this,
            "Are you sure you want to cancel:\n" + foodName + "?"
        );
        
        if (confirmed) {
            String error = listingService.cancelListing(
                authService.getCurrentUser(), 
                listingId
            );
            
            if (error == null) {
                ComponentFactory.showSuccess(this, "Listing cancelled successfully!");
                refreshListings();
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
     * Truncate text for display
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    // ==================== BUTTON RENDERER/EDITOR ====================
    
    /**
     * Button renderer for table cells
     */
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        
        private JButton viewBtn;
        private JButton cancelBtn;
        
        public ButtonRenderer() {
            setLayout(new GridLayout(1, 2, 5, 0));
            setBackground(UIConstants.BG_SECONDARY);
            setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            
            viewBtn = new JButton("View");
            viewBtn.setFont(UIConstants.FONT_SMALL);
            viewBtn.setBackground(UIConstants.INFO);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setOpaque(true);
            viewBtn.setBorderPainted(false);
            
            cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(UIConstants.FONT_SMALL);
            cancelBtn.setBackground(UIConstants.ERROR);
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setOpaque(true);
            cancelBtn.setBorderPainted(false);
            
            add(viewBtn);
            add(cancelBtn);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Enable/disable cancel button based on status
            String status = (String) table.getValueAt(row, 5);
            cancelBtn.setEnabled("available".equalsIgnoreCase(status));
            
            return this;
        }
    }
    
    /**
     * Button editor for table cells
     */
    class ButtonEditor extends DefaultCellEditor {
        
        private JPanel panel;
        private JButton viewBtn;
        private JButton cancelBtn;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
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
                showListingDetails(currentRow);
            });
            
            cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(UIConstants.FONT_SMALL);
            cancelBtn.setPreferredSize(new Dimension(75, 30));
            cancelBtn.setBackground(UIConstants.ERROR);
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setOpaque(true);
            cancelBtn.setBorderPainted(false);
            cancelBtn.addActionListener(e -> {
                fireEditingStopped();
                cancelListing(currentRow);
            });
            
            panel.add(viewBtn);
            panel.add(cancelBtn);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            
            currentRow = row;
            
            // Enable/disable cancel button based on status
            String status = (String) table.getValueAt(row, 5);
            cancelBtn.setEnabled("available".equalsIgnoreCase(status));
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}