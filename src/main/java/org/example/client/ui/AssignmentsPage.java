package org.example.client.ui;

import org.example.client.api.ClientAPI;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * The "My Assignments" page - shows a volunteer's current assignment
 * and their submitted preferences in a clean, borderless design
 */
class AssignmentsPage extends JPanel {
    // ========== DESIGN CONSTANTS ==========
    private static final int OUTER_PADDING = 30;    // Space around edges
    private static final int INNER_PADDING = 25;    // Space inside cards
    private static final int VERTICAL_SPACING = 40; // Space between sections
    private static final Color BACKGROUND = Color.WHITE; // Pure white background
    private static final Color CARD_BACKGROUND = new Color(245, 245, 245); // Light gray for cards
    private static final Color BUTTON_COLOR = new Color(43, 234, 240); // Teal action button
    private static final Color HEADER_BG = new Color(220, 235, 245); // Light blue header

    // ========== COMPONENTS ==========
    private final JLabel assignmentLbl = new JLabel("Not assigned yet"); // Current assignment display
    private final JTextArea prefsArea = new JTextArea(); // Shows volunteer's preferences
    private final MainFrame frame; // Reference to main window

    // ========== INITIALIZATION ==========
    /**
     * Creates the assignments page with current assignment and preferences
     * @param f The main application window (for navigation)
     */
    AssignmentsPage(MainFrame f) {
        this.frame = f;
        setupPageLayout();
        add(createHeader(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        setupLiveUpdates();
    }

    // ========== PAGE SETUP ==========
    /**
     * Configures the basic layout of the page
     */
    private void setupPageLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        // Padding only - no visible border
        setBorder(BorderFactory.createEmptyBorder(
                OUTER_PADDING, OUTER_PADDING,
                OUTER_PADDING, OUTER_PADDING));
    }

    // ========== HEADER SECTION ==========
    /**
     * Creates the page title header with light blue background
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        // Padding only - no border
        header.setBorder(BorderFactory.createEmptyBorder(
                VERTICAL_SPACING/3, INNER_PADDING,
                VERTICAL_SPACING/3, INNER_PADDING));

        JLabel title = new JLabel("Your Assignment");
        title.setFont(MainFrame.TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    // ========== MAIN CONTENT ==========
    /**
     * Creates the scrollable content area with assignment and preferences
     */
    private JComponent createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        // No border - using spacing only
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Current assignment card
        content.add(createAssignmentCard());
        content.add(Box.createRigidArea(new Dimension(0, VERTICAL_SPACING)));

        // Preferences card
        content.add(createPreferencesCard());

        // Create scroll pane but remove all borders
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);
        return scrollPane;
    }

    /**
     * Creates the card showing current assignment
     */
    private JPanel createAssignmentCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        // No border - just padding
        card.setBorder(BorderFactory.createEmptyBorder(
                INNER_PADDING, INNER_PADDING,
                INNER_PADDING, INNER_PADDING));
        card.setMaximumSize(new Dimension(600, 120));

        JLabel title = new JLabel("Current Assignment:");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        assignmentLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JPanel textColumn = new JPanel();
        textColumn.setLayout(new BoxLayout(textColumn, BoxLayout.Y_AXIS));
        textColumn.setBackground(CARD_BACKGROUND);
        textColumn.add(title);
        textColumn.add(Box.createRigidArea(new Dimension(0, 8)));
        textColumn.add(assignmentLbl);

        card.add(textColumn, BorderLayout.CENTER);
        return card;
    }

    /**
     * Creates the card showing volunteer's preferences
     */
    private JPanel createPreferencesCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        // No border - just padding
        card.setBorder(BorderFactory.createEmptyBorder(
                INNER_PADDING, INNER_PADDING,
                INNER_PADDING, INNER_PADDING));
        card.setMaximumSize(new Dimension(600, 250));

        JLabel title = new JLabel("Your Preferences:");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        prefsArea.setEditable(false);
        prefsArea.setBackground(CARD_BACKGROUND);
        prefsArea.setFont(MainFrame.TEXT_FONT);
        prefsArea.setLineWrap(true);
        prefsArea.setWrapStyleWord(true);

        // Create scroll pane but remove borders
        JScrollPane prefsScroll = new JScrollPane(prefsArea);
        prefsScroll.setBorder(BorderFactory.createEmptyBorder());

        card.add(title, BorderLayout.NORTH);
        card.add(prefsScroll, BorderLayout.CENTER);
        return card;
    }

    // ========== FOOTER SECTION ==========
    /**
     * Creates the footer with the optimization button
     */
    private JPanel createFooter() {
        JButton optimizeBtn = createRoundedButton(
                "Run Optimization",
                BUTTON_COLOR,
                ClientAPI::triggerOptimization
        );

        JPanel footer = new JPanel();
        footer.setBackground(BACKGROUND);
        footer.add(optimizeBtn);
        return footer;
    }

    /**
     * Creates a rounded button with proper styling
     */
    private JButton createRoundedButton(String text, Color bgColor, Runnable action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();

                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(getText(), x, y);
            }

            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                return new Dimension(fm.stringWidth(getText()) + 40, 40);
            }

            @Override
            public boolean contains(int x, int y) {
                return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12).contains(x, y);
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        button.addActionListener(e -> action.run());
        return button;
    }

    // ========== LIVE UPDATES ==========
    /**
     * Sets up live updates for assignment changes from server
     */
    private void setupLiveUpdates() {
        // Update assignment when new one comes from server
        ClientAPI.setOnAssignmentReceived(service ->
                SwingUtilities.invokeLater(() -> {
                    if (service == null || service.isBlank()) return;
                    assignmentLbl.setText(service);
                }));

        // Start polling for updates
        // 2) instead of polling, open a persistent WebSocket
        ClientAPI.connectWebSocket(MainFrame.VolunteerIdentity.id());
    }

    // ========== DATA REFRESH ==========
    /**
     * Updates the preferences display when this page is shown
     * Called by MainFrame when user navigates to this page
     */
    void refreshPrefsBox() {
        List<String> preferences = PreferencesPage.getLastPrefs();
        StringBuilder formattedPrefs = new StringBuilder();

        // Format preferences as numbered list
        for (int i = 0; i < preferences.size(); i++) {
            formattedPrefs.append(i + 1)
                    .append(". ")
                    .append(preferences.get(i))
                    .append('\n');
        }

        prefsArea.setText(formattedPrefs.toString());
    }
}