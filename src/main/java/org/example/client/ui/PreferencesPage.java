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
 * Designed with a clean, minimalist look that matches the AssignmentsPage style
 */
class PreferencesPage extends JPanel {
    // ========== DESIGN CONSTANTS ==========
    private static final int OUTER_PADDING = 30;    // Space around edges
    private static final int INNER_PADDING = 25;    // Space inside cards
    private static final int VERTICAL_SPACING = 20; // Space between sections
    private static final Color BACKGROUND = Color.WHITE; // Pure white background
    private static final Color CARD_BACKGROUND = new Color(245, 245, 245); // Light gray for cards
    private static final Color BUTTON_COLOR = new Color(43, 234, 240); // Teal action button
    private static final Color HEADER_BG = new Color(220, 235, 245); // Light blue header

    // ========== COMPONENTS ==========
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
        this.nameField = new JTextField(20);
        
        setupPageLayout();
        add(createHeader(), BorderLayout.NORTH);
        add(createFormContent(), BorderLayout.CENTER);
        add(createSubmitButton(), BorderLayout.SOUTH);
    }

    // ========== PAGE STRUCTURE ==========
    private void setupPageLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(
            OUTER_PADDING, OUTER_PADDING, 
            OUTER_PADDING, OUTER_PADDING));
    }

    // ========== HEADER SECTION ==========
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(
            VERTICAL_SPACING, INNER_PADDING, 
            VERTICAL_SPACING, INNER_PADDING));
        
        JLabel title = new JLabel("Your Volunteer Preferences");
        title.setFont(MainFrame.TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(title, BorderLayout.CENTER);
        return header;
    }

    // ========== MAIN FORM CONTENT ==========
    private JComponent createFormContent() {
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(BACKGROUND);
        formContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Name input field
        formContainer.add(createInputCard("Your Name", nameField));
        formContainer.add(Box.createRigidArea(new Dimension(0, VERTICAL_SPACING)));

        // Create five preference dropdowns
        String[] preferenceLabels = {"First", "Second", "Third", "Fourth", "Fifth"};
        for (int i = 0; i < 5; i++) {
            boxes[i] = createServiceDropdown();
            formContainer.add(createInputCard(preferenceLabels[i] + " Choice", boxes[i]));
            if (i < 4) formContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);
        return scrollPane;
    }

    private JComboBox<String> createServiceDropdown() {
        JComboBox<String> dropdown = new JComboBox<>(ClientAPI.getAllServices());
        dropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        dropdown.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return dropdown;
    }

    private JPanel createInputCard(String label, JComponent input) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createEmptyBorder(
            INNER_PADDING, INNER_PADDING,
            INNER_PADDING, INNER_PADDING));
        card.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(input);

        return card;
    }

    // ========== SUBMIT BUTTON ==========
    private JPanel createSubmitButton() {
        JButton submit = createRoundedButton(
            "Submit Preferences", 
            BUTTON_COLOR, 
            this::handleSubmit
        );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND);
        buttonPanel.add(submit);
        return buttonPanel;
    }

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

    // ========== FORM SUBMISSION ==========
    private void handleSubmit() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter your name");
            return;
        }

        lastSubmitted.clear();
        Set<String> seen = new HashSet<>();
        for (JComboBox<String> box : boxes) {
            String selection = (String) box.getSelectedItem();
            if (selection != null && seen.add(selection)) {
                lastSubmitted.add(selection);
            }
        }
        
        if (lastSubmitted.size() < 5) {
            showError("Please select 5 unique choices");
            return;
        }

        ClientAPI.sendPreferences(MainFrame.VolunteerIdentity.id(), name, lastSubmitted);
        JOptionPane.showMessageDialog(this, "Preferences saved successfully!");
        frame.showPage("assign"); // Navigate to assignments page
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Needed", JOptionPane.WARNING_MESSAGE);
    }

    // ========== DATA ACCESS ==========
    static List<String> getLastPrefs() {
        return List.copyOf(lastSubmitted);
    }
}