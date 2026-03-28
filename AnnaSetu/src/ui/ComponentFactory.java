package ui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Component Factory - Creates styled, reusable UI components
 * Ensures consistent look and feel across the application
 */
public class ComponentFactory {
    
    // ==================== BUTTONS ====================
    
    /**
     * Create primary button (green)
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, UIConstants.PRIMARY, UIConstants.TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.PRIMARY_DARK);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.PRIMARY);
            }
        });
        
        return button;
    }
    
    /**
     * Create secondary button (orange)
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, UIConstants.SECONDARY, UIConstants.TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.SECONDARY_DARK);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.SECONDARY);
            }
        });
        
        return button;
    }
    
    /**
     * Create outline button (primary color text with border)
     */
    public static JButton createOutlineButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.FONT_BUTTON);
        button.setForeground(UIConstants.PRIMARY);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.PRIMARY, 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UIConstants.HEIGHT_BUTTON));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.PRIMARY_LIGHT);
                button.setForeground(UIConstants.PRIMARY_DARK);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setForeground(UIConstants.PRIMARY);
            }
        });
        
        return button;
    }
    
    /**
     * Create danger button (red)
     */
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, UIConstants.ERROR, UIConstants.TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.darker(UIConstants.ERROR, 0.8f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.ERROR);
            }
        });
        
        return button;
    }
    
    /**
     * Create success button (green)
     */
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, UIConstants.SUCCESS, UIConstants.TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.darker(UIConstants.SUCCESS, 0.8f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.SUCCESS);
            }
        });
        
        return button;
    }
    
    /**
     * Create info button (blue)
     */
    public static JButton createInfoButton(String text) {
        JButton button = new JButton(text);
        styleButton(button, UIConstants.INFO, UIConstants.TEXT_LIGHT);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UIConstants.darker(UIConstants.INFO, 0.8f));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.INFO);
            }
        });
        
        return button;
    }
    
    /**
     * Style a button with given colors
     */
    private static void styleButton(JButton button, Color bg, Color fg) {
        button.setFont(UIConstants.FONT_BUTTON);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, UIConstants.HEIGHT_BUTTON));
    }
    
    // ==================== TEXT FIELDS ====================
    
    /**
     * Create styled text field
     */
    public static JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(UIConstants.FONT_NORMAL);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        textField.setPreferredSize(new Dimension(UIConstants.WIDTH_INPUT, UIConstants.HEIGHT_INPUT));
        
        // Placeholder
        if (placeholder != null && !placeholder.isEmpty()) {
            addPlaceholder(textField, placeholder);
        }
        
        // Focus effect
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.FOCUS_BORDER, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return textField;
    }
    
    /**
     * Create password field
     */
    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(UIConstants.FONT_NORMAL);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setPreferredSize(new Dimension(UIConstants.WIDTH_INPUT, UIConstants.HEIGHT_INPUT));
        
        // Focus effect
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.FOCUS_BORDER, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return passwordField;
    }
    
    /**
     * Create text area
     */
    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        textArea.setFont(UIConstants.FONT_NORMAL);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textArea;
    }
    
    /**
     * Add placeholder text to a text field
     */
    private static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(UIConstants.TEXT_DISABLED);
        textField.setText(placeholder);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(UIConstants.TEXT_DISABLED);
                }
            }
        });
    }
    
    // ==================== LABELS ====================
    
    /**
     * Create heading label
     */
    public static JLabel createHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_HEADING);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Create title label
     */
    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_TITLE);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Create subheading label
     */
    public static JLabel createSubheading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_SUBHEADING);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Create body label
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_NORMAL);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Create secondary label (gray text)
     */
    public static JLabel createSecondaryLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_NORMAL);
        label.setForeground(UIConstants.TEXT_SECONDARY);
        return label;
    }
    
    /**
     * Create status label with colored background
     */
    public static JLabel createStatusLabel(String status) {
        JLabel label = new JLabel(UIConstants.getStatusDisplay(status));
        label.setFont(UIConstants.FONT_SMALL_BOLD);
        label.setForeground(UIConstants.TEXT_LIGHT);
        label.setBackground(UIConstants.getStatusColor(status));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return label;
    }
    
    // ==================== PANELS ====================
    
    /**
     * Create card panel (with border and padding)
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(
                UIConstants.SPACING_NORMAL,
                UIConstants.SPACING_NORMAL,
                UIConstants.SPACING_NORMAL,
                UIConstants.SPACING_NORMAL
            )
        ));
        return panel;
    }
    
    /**
     * Create section panel (no border, just padding)
     */
    public static JPanel createSectionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.BG_PRIMARY);
        panel.setBorder(UIConstants.PADDING_NORMAL);
        return panel;
    }
    
    // ==================== DIALOGS ====================
    
    /**
     * Show error dialog
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show success dialog
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Show confirmation dialog
     */
    public static boolean showConfirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Show input dialog
     */
    public static String showInput(Component parent, String message, String initialValue) {
        return (String) JOptionPane.showInputDialog(
            parent,
            message,
            "Input",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            initialValue
        );
    }
}