package org.example.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The main application window for Volunteer Matching System
 * Handles navigation between different views/pages
 */
public class MainFrame extends JFrame {
    
    // ========== CONSTANTS ==========
    public static final Color PRIMARY = new Color(0, 120, 215);
    public static final Color PRIMARY_COLOR = PRIMARY;   // Main blue color
    public static final Color SECONDARY_COLOR = new Color(255, 182, 193); // Light pink background
    public static final Color NAV_BUTTON_COLOR = new Color(255, 105, 180); // Bright pink buttons
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24); // For main headings
    public static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16); // Regular text
    public static final Font NAV_BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15); // Navigation text

    // Unique ID generator for volunteer sessions
    public final class VolunteerIdentity {
        private static final String ID = "vol" + System.nanoTime();
        public static String id() { return ID; }
        private VolunteerIdentity() {}
    }

    // ========== COMPONENTS ==========
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(cardLayout);
    private final AssignmentsPage assignmentPage = new AssignmentsPage(this);
    private final PreferencesPage preferencesPage = new PreferencesPage(this);

    // ========== MAIN FRAME SETUP ==========
    public MainFrame() {
        configureWindow();
        setupNavigationAndContent();
        setVisible(true);
    }

    private void configureWindow() {
        setTitle("Volunteer Matching System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
    }

    private void setupNavigationAndContent() {
        add(createNavigationPanel(), BorderLayout.WEST);
        setupScreenPages();
        add(screenContainer, BorderLayout.CENTER);
        cardLayout.show(screenContainer, "home"); // Start on home page
    }

    // ========== NAVIGATION PANEL ==========
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SECONDARY_COLOR);
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        // Add navigation buttons
        addNavButton(navPanel, "Home", "home");
        addNavButton(navPanel, "My Preferences", "prefs");
        addNavButton(navPanel, "My Assignments", "assign");
        addNavButton(navPanel, "About Services", "about");

        return navPanel;
    }

    // ========== BUTTON IMPLEMENTATION ==========
    private void addNavButton(JPanel navPanel, String text, String page) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw rounded background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();

                // Custom text painting to ensure perfect fit
                paintButtonText(g);
            }

            private void paintButtonText(Graphics g) {
                FontMetrics fm = g.getFontMetrics();
                String text = getText();
                int textWidth = fm.stringWidth(text);
                int availableWidth = getWidth() - 30; // 15px padding each side

                g.setColor(getForeground());
                
                if (textWidth <= availableWidth) {
                    // Text fits normally
                    int x = 15; // Left padding
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g.drawString(text, x, y);
                } else {
                    // Text too long - shorten with ellipsis
                    String clipped = text;
                    while (fm.stringWidth(clipped + "...") > availableWidth && clipped.length() > 0) {
                        clipped = clipped.substring(0, clipped.length() - 1);
                    }
                    int x = 15;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g.drawString(clipped + "...", x, y);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                int width = fm.stringWidth(getText()) + 30; // Text width + padding
                return new Dimension(Math.min(width, 200), 45); // Max width 200px
            }

            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25).contains(x, y);
            }
        };

        // Button styling
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(NAV_BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(NAV_BUTTON_FONT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setMaximumSize(new Dimension(200, 45));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(NAV_BUTTON_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(NAV_BUTTON_COLOR);
            }
        });

        button.addActionListener(e -> showPage(page));
        navPanel.add(button);
        navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    // ========== SCREEN MANAGEMENT ==========
    private void setupScreenPages() {
        screenContainer.add(new HomePage(this), "home");
        screenContainer.add(preferencesPage, "prefs");
        screenContainer.add(assignmentPage, "assign");
        screenContainer.add(new Services(this), "about");
    }

    public void showPage(String key) {
        cardLayout.show(screenContainer, key);
        if ("assign".equals(key)) {
            assignmentPage.refreshPrefsBox();
        }
    }
}