package org.example.client.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * The main welcome screen that volunteers see when they log in
 * Shows a friendly greeting and their available options
 */
class HomePage extends JPanel {
    // These numbers control the spacing and make everything look neat
    private static final int OUTER_PADDING = 40;  // Space around the edges of the whole screen
    private static final int INNER_PADDING = 20;  // Space inside panels (matches welcome area)
    private static final int VERTICAL_SPACING = 15; // Space between sections
    private static final Color BUTTON_COLOR = new Color(43, 234, 240); // Our nice teal button color

    /**
     * Sets up the whole home page with welcome message and option cards
     * @param frame The main window so we can switch views when buttons are clicked
     */
    HomePage(MainFrame frame) {
        // Basic setup for the whole panel
        setLayout(new BorderLayout()); // Simple top-to-bottom layout
        setBackground(Color.WHITE); // Clean white background
        setBorder(BorderFactory.createEmptyBorder(OUTER_PADDING, OUTER_PADDING, OUTER_PADDING, OUTER_PADDING)); // Add breathing room

        // Create and add the welcome banner at the top
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);

        // Create and add the cards showing volunteer options
        // We measure the welcome panel's width so cards match exactly
        JPanel cardsPanel = createCardsPanel(frame, welcomePanel.getPreferredSize().width);
        add(cardsPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the blue welcome banner at the top of the screen
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 235, 245)); // Light blue background
        // Give it rounded corners with our custom border
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(180, 210, 230), 2, 15), // Light blue border
            BorderFactory.createEmptyBorder( // Inner padding matches card padding
                VERTICAL_SPACING, INNER_PADDING, 
                VERTICAL_SPACING, INNER_PADDING)
        ));
        
        // The actual welcome text
        JLabel title = new JLabel("Welcome, Dear Volunteer!");
        title.setFont(MainFrame.TITLE_FONT); // Uses our standard big font
        title.setHorizontalAlignment(SwingConstants.CENTER); // Centered in the blue bar
        panel.add(title, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates the container for all the option cards
     * @param frame Needed for button actions
     * @param contentWidth The width to make all cards (matches welcome panel)
     */
    private JPanel createCardsPanel(MainFrame frame, int contentWidth) {
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS)); // Stack cards vertically
        cardsContainer.setBackground(Color.WHITE);
        cardsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // All the options we want to show volunteers
        String[] cardTitles = {
            "Set Your Preferences",
            "View Assignments", 
            "Learn More"
        };
        
        String[] cardDescriptions = {
            "Choose your top 5 preferred volunteer activities",
            "See where you've been assigned to help",
            "Understand how our matching system works"
        };

        String[] pageIds = {"prefs", "assign", "about"};

        // Create each card and add it to the container
        for (int i = 0; i < cardTitles.length; i++) {
            addCard(cardsContainer, cardTitles[i], cardDescriptions[i], pageIds[i], frame, contentWidth);
            // Add space between cards, but not after the last one
            if (i < cardTitles.length - 1) {
                cardsContainer.add(Box.createRigidArea(new Dimension(0, VERTICAL_SPACING)));
            }
        }

        // This wrapper centers everything nicely on the screen
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(cardsContainer);
        
        return centerWrapper;
    }

    /**
     * Creates an individual option card with title, description and button
     */
    private void addCard(JPanel parent, String title, String description, 
                        String page, MainFrame frame, int contentWidth) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Stack elements vertically
        card.setBackground(Color.WHITE);
        card.setAlignmentX(Component.LEFT_ALIGNMENT); // Everything lines up on the left
        
        // Match the exact same padding as the welcome panel
        card.setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_SPACING, INNER_PADDING, 
            VERTICAL_SPACING, INNER_PADDING));
        
        // Make card same width as welcome panel's content area
        card.setMaximumSize(new Dimension(contentWidth, Integer.MAX_VALUE));

        // Card title (bigger and bold)
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10))); // Small space after title

        // Card description (normal text that wraps nicely)
        JTextArea descText = new JTextArea(description);
        descText.setEditable(false); // Can't edit this text
        descText.setBackground(Color.WHITE); // Match card background
        descText.setFont(MainFrame.TEXT_FONT); // Standard readable font
        descText.setLineWrap(true); // Make text wrap to next line
        descText.setWrapStyleWord(true); // Don't break words in middle
        descText.setAlignmentX(Component.LEFT_ALIGNMENT);
        descText.setBorder(null); // Remove default border
        descText.setFocusable(false); // Don't let it get focus
        card.add(descText);
        card.add(Box.createRigidArea(new Dimension(0, 15))); // Space before button

        // The action button at the bottom of the card
        JButton actionBtn = createRoundedButton(
            page.equals("prefs") ? "My Preferences" :
            page.equals("assign") ? "My Assignments" : "About Services",
            BUTTON_COLOR,
            () -> frame.showPage(page) // What happens when clicked
        );
        actionBtn.setAlignmentX(Component.LEFT_ALIGNMENT); // Line up with text
        card.add(actionBtn);

        parent.add(card);
    }

    /**
     * Creates a nice rounded button with smooth edges
     * @param text What the button says
     * @param bgColor Background color (our teal)
     * @param action What to do when clicked
     */
    private JButton createRoundedButton(String text, Color bgColor, Runnable action) {
        // Custom button that draws itself with rounded corners
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // First draw the rounded background
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                // Then let the button draw its text
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Draw a subtle border around the button
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground().darker());
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
            
            @Override
            public boolean contains(int x, int y) {
                // Only respond to clicks inside the rounded shape
                return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20).contains(x, y);
            }
        };
        
        // Button styling
        button.setOpaque(false); // Let the rounded background show through
        button.setContentAreaFilled(false); // Don't use default rectangle background
        button.setBorderPainted(false); // We're drawing our own border
        button.setBackground(bgColor); // Our nice teal color
        button.setForeground(Color.WHITE); // White text
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Readable font
        button.setPreferredSize(new Dimension(180, 40)); // Comfortable button size
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // Text padding
        button.addActionListener(e -> action.run()); // What to do when clicked
        
        return button;
    }

    /**
     * Our custom border that draws rounded rectangles
     * Used for the welcome panel and could be used elsewhere
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color; // Border color
        private final int thickness; // How thick the border is
        private final int radius; // How round the corners are
        private final Insets insets; // How much space the border takes up
        
        RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
            this.insets = new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            // Draw the rounded rectangle border with smooth edges
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(
                x + thickness/2, y + thickness/2,
                width - thickness, height - thickness,
                radius, radius
            );
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }
    }
}