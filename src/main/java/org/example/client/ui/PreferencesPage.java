package org.example.client.ui;

import org.example.client.api.ClientAPI;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The volunteer preferences form - where users select their top 5 activity choices
 * Designed with a clean, minimalist look that focuses on content rather than borders
 */
class PreferencesPage extends JPanel {
    // ========== DESIGN CONSTANTS ==========
    // These control the visual spacing and colors
    private static final int OUTER_PADDING = 30;    // Space around window edges
    private static final int INNER_PADDING = 20;    // Space inside containers
    private static final int VERTICAL_SPACING = 12; // Space between sections
    private static final Color BACKGROUND = Color.WHITE; // Pure white background
    private static final Color BUTTON_COLOR = new Color(43, 234, 240); // Vibrant teal action button
    private static final Color HEADER_BG = new Color(220, 235, 245); // Light blue header

    // ========== FORM COMPONENTS ==========
    private final MainFrame frame;          // Reference to main window
    private final JTextField nameField;     // For volunteer's name input
    private final JComboBox<String>[] boxes = new JComboBox[5]; // The 5 preference dropdowns
    private static final List<String> lastSubmitted = new CopyOnWriteArrayList<>(); // Stores submitted prefs

    // ========== INITIALIZATION ==========
    /**
     * Sets up the entire preferences form
     * @param f The main application window (for navigation between pages)
     */
    PreferencesPage(MainFrame f) {
        this.frame = f;
        this.nameField = new JTextField(20); // Initialize name field
        
        // Build the page layout
        setupPageLayout();
        add(createHeader(), BorderLayout.NORTH);       // Add the title header
        add(createFormContent(), BorderLayout.CENTER); // Add the main form content
        add(createSubmitButton(), BorderLayout.SOUTH); // Add the submit button
    }

    // ========== PAGE STRUCTURE ==========
    /**
     * Configures the basic layout settings for this page
     */
    private void setupPageLayout() {
        setLayout(new BorderLayout()); // Simple top-to-bottom layout
        setBackground(BACKGROUND); // Clean white background
        // Add generous padding around edges (no visible border)
        setBorder(BorderFactory.createEmptyBorder(
            OUTER_PADDING, OUTER_PADDING, 
            OUTER_PADDING, OUTER_PADDING));
    }

    // ========== HEADER SECTION ==========
    /**
     * Creates the light blue header panel with page title
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG); // Light blue background
        // Add padding only - no visible border
        header.setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_SPACING, INNER_PADDING, 
            VERTICAL_SPACING, INNER_PADDING));
        
        // Create and style the title label
        JLabel title = new JLabel("Your Volunteer Preferences");
        title.setFont(MainFrame.TITLE_FONT); // Use app's standard title font
        title.setHorizontalAlignment(SwingConstants.CENTER); // Centered text
        header.add(title, BorderLayout.CENTER);
        
        return header;
    }

    // ========== MAIN FORM CONTENT ==========
    /**
     * Creates the scrollable area containing all form inputs
     * Note: The scroll pane border is removed for cleaner look
     */
    private JComponent createFormContent() {
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(BACKGROUND);
        // Add vertical padding only - no visible border
        formContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Name input field - first thing volunteers see
        formContainer.add(createInputCard("Your Name", nameField));
        formContainer.add(Box.createRigidArea(new Dimension(0, VERTICAL_SPACING)));

        // Create five preference dropdowns with labels
        String[] preferenceLabels = {"First", "Second", "Third", "Fourth", "Fifth"};
        for (int i = 0; i < 5; i++) {
            boxes[i] = createServiceDropdown();
            formContainer.add(createInputCard(preferenceLabels[i] + " Choice", boxes[i]));
            // Add smaller spacing between fields (except after last one)
            if (i < 4) formContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        // Create scroll pane but remove its border completely
        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border
        scrollPane.getViewport().setBackground(BACKGROUND); // Match background
        return scrollPane;
    }

    /**
     * Creates a dropdown menu with all available volunteer services
     */
    private JComboBox<String> createServiceDropdown() {
        JComboBox<String> dropdown = new JComboBox<>(ClientAPI.getAllServices());
        dropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Comfortable reading size
        dropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32)); // Standard height
        // Remove border completely for clean look
        dropdown.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return dropdown;
    }

    /**
     * Creates a clean container for each form input
     * @param label The descriptive text (shown in smaller 12pt font)
     * @param input The actual input component (shown in larger 14pt font)
     */
    private JPanel createInputCard(String label, JComponent input) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BACKGROUND);
        // No border - using spacing only for visual separation
        card.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        card.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        // Descriptive label (smaller, subtle text)
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4))); // Small space after label

        // The actual input field with larger text
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(input);

        return card;
    }

    // ========== SUBMIT BUTTON ==========
    /**
     * Creates the action button to submit preferences
     * This is the only element with visible styling (rounded teal background)
     */
    private JPanel createSubmitButton() {
        // Custom button with perfect text fitting
        JButton submit = new JButton("Submit Preferences") {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw rounded teal background
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                
                // Draw perfectly centered text
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g.drawString(getText(), x, y);
            }

            @Override
            public Dimension getPreferredSize() {
                // Size button to fit text comfortably
                FontMetrics fm = getFontMetrics(getFont());
                return new Dimension(fm.stringWidth(getText()) + 40, 38);
            }

            @Override
            public boolean contains(int x, int y) {
                // Only respond to clicks within the rounded shape
                return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12).contains(x, y);
            }
        };
        
        // Button styling
        submit.setOpaque(false);
        submit.setContentAreaFilled(false);
        submit.setBorderPainted(false);
        submit.setBackground(BUTTON_COLOR);
        submit.setForeground(Color.WHITE);
        submit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Container to center the button (with no border)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(submit);
        return buttonPanel;
    }

    // ========== FORM SUBMISSION ==========
    /**
     * Handles when volunteers submit their preferences
     * Validates the inputs and saves to server
     */
    private void handleSubmit() {
        // Check name was entered
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter your name");
            return;
        }

        // Collect and validate preferences
        lastSubmitted.clear();
        Set<String> seen = new HashSet<>(); // Track duplicates
        for (JComboBox<String> box : boxes) {
            String selection = (String) box.getSelectedItem();
            if (selection != null && seen.add(selection)) {
                lastSubmitted.add(selection);
            }
        }
        
        // Require exactly 5 unique choices
        if (lastSubmitted.size() < 5) {
            showError("Please select 5 unique choices");
            return;
        }

        // Save to server and show confirmation
        ClientAPI.sendPreferences(MainFrame.VolunteerIdentity.id(), name, lastSubmitted);
        JOptionPane.showMessageDialog(this, "Preferences saved successfully!");
        frame.showPage("assign"); // Show assignments page
    }

    /**
     * Shows an error message to the volunteer
     * @param message The helpful error text to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Needed", JOptionPane.WARNING_MESSAGE);
    }

    // ========== DATA ACCESS ==========
    /**
     * Provides the last submitted preferences to other pages
     * @return A safe copy of the preferences list
     */
    static List<String> getLastPrefs() {
        return List.copyOf(lastSubmitted);
    }
}