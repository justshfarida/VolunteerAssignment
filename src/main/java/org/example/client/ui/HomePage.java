package org.example.client.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * The welcoming dashboard volunteers see when they open the app.
 * Think of this like a friendly reception desk - it greets users 
 * and shows them where they can go next with clear, inviting options.
 */
class HomePage extends JPanel {
    // These numbers are like the 'spice measurements' for our layout - 
    // they control how much space goes where to make everything look just right
    private static final int OUTER_PADDING = 40;    // Space between window edges and content
    private static final int INNER_PADDING = 20;    // Padding inside cards and welcome area
    private static final int VERTICAL_SPACING = 15; // Breathing room between sections
    private static final Color BUTTON_COLOR = new Color(43, 234, 240); // Our signature teal color

    /**
     * Builds the entire homepage like assembling a welcoming lobby.
     * @param frame The app's main window - our 'building' that contains this 'room'
     */
    HomePage(MainFrame frame) {
        // Set up the foundation of this 'room'
        setLayout(new BorderLayout()); // Everything arranged neatly top-to-bottom
        setBackground(Color.WHITE); // Clean white walls
        // Add 'breathing space' around the edges so content doesn't feel cramped
        setBorder(BorderFactory.createEmptyBorder(OUTER_PADDING, OUTER_PADDING, OUTER_PADDING, OUTER_PADDING));

        // Hang up our welcome sign at the top
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);

        // Set up the 'information kiosk' with navigation options
        // We measure the welcome panel's width so everything lines up perfectly
        JPanel cardsPanel = createCardsPanel(frame, welcomePanel.getPreferredSize().width);
        add(cardsPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the friendly welcome banner at the top.
     * Imagine this like a reception desk with a "We're glad you're here!" sign.
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(220, 235, 245)); // Soft, calming blue
        
        // Give it nice rounded corners with a subtle border, like a high-end kiosk
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(new Color(180, 210, 230), 2, 15), // Light blue frame
            BorderFactory.createEmptyBorder( // Inner cushioning
                VERTICAL_SPACING, INNER_PADDING, 
                VERTICAL_SPACING, INNER_PADDING)
        ));
        
        // The actual welcome message
        JLabel title = new JLabel("Welcome, Dear Volunteer!");
        title.setFont(MainFrame.TITLE_FONT); // Big, friendly letters
        title.setHorizontalAlignment(SwingConstants.CENTER); // Perfectly centered
        panel.add(title, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Builds the panel holding all the navigation cards.
     * These are like different doors users can choose from.
     */
    private JPanel createCardsPanel(MainFrame frame, int contentWidth) {
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS)); // Stack cards vertically
        cardsContainer.setBackground(Color.WHITE); // Keep it clean
        cardsContainer.setAlignmentX(Component.CENTER_ALIGNMENT); // Center everything

        // The different 'doors' we're offering
        String[] cardTitles = {
            "Set Your Preferences",    // Door #1: Choose activities
            "View Assignments",       // Door #2: See your schedule
            "Learn More"              // Door #3: Understand how it works
        };
        
        String[] cardDescriptions = {
            "Choose your top 5 preferred volunteer activities",
            "See where you've been assigned to help",
            "Understand how our matching system works"
        };

        String[] pageIds = {"prefs", "assign", "about"}; // Where each door leads

        // Build and arrange each door
        for (int i = 0; i < cardTitles.length; i++) {
            addCard(cardsContainer, cardTitles[i], cardDescriptions[i], pageIds[i], frame, contentWidth);
            // Add space between doors, but not after the last one
            if (i < cardTitles.length - 1) {
                cardsContainer.add(Box.createRigidArea(new Dimension(0, VERTICAL_SPACING)));
            }
        }

        // Center everything neatly on the page
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(cardsContainer);
        
        return centerWrapper;
    }

    /**
     * Builds one individual navigation card (like crafting a door with a sign).
     */
    private void addCard(JPanel parent, String title, String description, 
                        String page, MainFrame frame, int contentWidth) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Arrange contents top-to-bottom
        card.setBackground(Color.WHITE); // Clean white card
        card.setAlignmentX(Component.LEFT_ALIGNMENT); // Keep text aligned
        
        // Add padding so content isn't squished against edges
        card.setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_SPACING, INNER_PADDING, 
            VERTICAL_SPACING, INNER_PADDING));
        
        // Make all cards the same width for consistency
        card.setMaximumSize(new Dimension(contentWidth, Integer.MAX_VALUE));

        // --- Card Title ---
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Bold heading
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10))); // Small gap after title

        // --- Card Description ---
        JTextArea descText = new JTextArea(description);
        descText.setEditable(false); // Just for reading
        descText.setBackground(Color.WHITE); // Match card color
        descText.setFont(MainFrame.TEXT_FONT); // Easy-to-read text
        descText.setLineWrap(true); // Text flows to next line
        descText.setWrapStyleWord(true); // Don't break words awkwardly
        descText.setAlignmentX(Component.LEFT_ALIGNMENT);
        descText.setBorder(null); // No border around text
        descText.setFocusable(false); // Can't select text
        card.add(descText);
        card.add(Box.createRigidArea(new Dimension(0, 15))); // Space before button

        // --- Action Button ---
        // Button text changes based on where it leads
        String buttonText = switch (page) {
            case "prefs" -> "My Preferences";
            case "assign" -> "My Assignments";
            default -> "About Services";
        };
        
        // Create the actual button that responds to touches
        JButton actionBtn = createInteractiveButton(
            buttonText,
            BUTTON_COLOR,
            () -> frame.showPage(page) // What happens when clicked
        );
        actionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(actionBtn);

        parent.add(card); // Add this completed card to the panel
    }

    /**
     * Creates a button that feels alive and responsive to the user.
     * It will subtly change color when interacted with - like a physical button would.
     */
    private JButton createInteractiveButton(String text, Color bgColor, Runnable action) {
        // Custom button that paints its own attractive shape
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button gets darker when you interact with it
                Color fillColor = bgColor;
                if (isPressed) {
                    fillColor = bgColor.darker().darker(); // Extra dark when pressed
                } else if (isHovered) {
                    fillColor = bgColor.darker(); // Slightly dark when hovered
                }
                
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                
                // Paint the text label
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Outline that also responds to interaction
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color borderColor = isPressed ? bgColor.darker().darker().darker() : 
                                  isHovered ? bgColor.darker().darker() : 
                                  bgColor.darker();
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
            
            @Override
            public boolean contains(int x, int y) {
                // Only the rounded area should be clickable
                return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20).contains(x, y);
            }
        };
        
        // Basic button styling
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40)); // Comfortable size for fingers
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // Text padding
        
        // What happens when clicked
        button.addActionListener(e -> action.run());
        
        // Make the button respond to mouse movements like a physical button would
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.repaint(); // Show the change immediately
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.repaint();
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker().darker());
                button.repaint();
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
                button.repaint();
            }
        });
        
        return button;
    }

    /**
     * A special border that gives our welcome panel its rounded corners.
     * Like a fancy picture frame for our content.
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color; // The color of the frame
        private final int thickness; // How thick the frame is
        private final int radius; // How round the corners are
        private final Insets insets; // How much space the frame takes up
        
        RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
            this.insets = new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            // Draw the rounded rectangle with smooth edges
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            // Draw slightly inside the boundaries so the stroke doesn't get cut off
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