package org.example.client.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The "About" page - explains how the volunteer matching system works
 * in a clean, borderless design matching the rest of the application
 */
class Services extends JPanel {
    // ========== DESIGN CONSTANTS ==========
    private static final int OUTER_PADDING = 30;    // Space around edges
    private static final int INNER_PADDING = 25;    // Space inside cards
    private static final int VERTICAL_SPACING = 20; // Space between sections
    private static final Color BACKGROUND = Color.WHITE; // Pure white background
    private static final Color CARD_BACKGROUND = new Color(245, 245, 245); // Light gray for cards
    private static final Color HEADER_BG = new Color(220, 235, 245); // Light blue header

    // ========== INITIALIZATION ==========
    /**
     * Creates the about page with system explanation
     * @param f The main application window (unused but kept for consistency)
     */
    Services(MainFrame f) {
        setupPageLayout();
        add(createHeader(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    // ========== PAGE SETUP ==========
    /**
     * Configures the basic layout with padding only (no borders)
     */
    private void setupPageLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        // Padding only - no border
        setBorder(BorderFactory.createEmptyBorder(
            OUTER_PADDING, OUTER_PADDING,
            OUTER_PADDING, OUTER_PADDING));
    }

    // ========== HEADER SECTION ==========
    /**
     * Creates the page title header with no borders
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        // Padding only - no border
        header.setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_SPACING, INNER_PADDING,
            VERTICAL_SPACING, INNER_PADDING));

        JLabel title = new JLabel("About Volunteer Matching");
        title.setFont(MainFrame.TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    // ========== MAIN CONTENT ==========
    /**
     * Creates the content area with no visible borders
     */
    private JComponent createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        // No border - using spacing only
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Add the explanation card
        content.add(createInfoCard());
        
        // Create scroll pane but remove all borders
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);
        return scrollPane;
    }

    /**
     * Creates the info card with no borders
     */
    private JPanel createInfoCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        // Padding only - no border
        card.setBorder(BorderFactory.createEmptyBorder(
            INNER_PADDING, INNER_PADDING,
            INNER_PADDING, INNER_PADDING));
        card.setMaximumSize(new Dimension(600, 300));

        JTextArea infoText = new JTextArea("""
            This system matches volunteers to services based on their preferences.

            Steps:
              1. Choose up to five preferred services
              2. Submit preferences
              3. Click "Run Optimization" (any volunteer can)
              4. All open windows update automatically

            All data is stored in RAM; restarting the server clears everything.
            """);
        
        // Configure text area with no borders
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setBackground(CARD_BACKGROUND);
        infoText.setFont(MainFrame.TEXT_FONT);
        infoText.setBorder(BorderFactory.createEmptyBorder());

        // Create scroll pane but remove all borders
        JScrollPane textScroll = new JScrollPane(infoText);
        textScroll.setBorder(BorderFactory.createEmptyBorder());
        textScroll.getViewport().setBackground(CARD_BACKGROUND);
        
        card.add(textScroll, BorderLayout.CENTER);
        return card;
    }
}