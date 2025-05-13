package org.example.client.ui;

import org.example.client.api.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ClientUI extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 120, 215); // Blue buttons
    private static final Color SECONDARY_COLOR = new Color(255, 182, 193); // Pink side panel
    private static final Color NAV_BUTTON_COLOR = new Color(255, 105, 180); // Darker pink nav buttons
    private static final Color HOME_BUTTON_COLOR = new Color(255, 105, 180); // Pink for home page buttons
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JComboBox<String>[] preferenceSelectors;
    private JLabel assignmentLabel;
    private List<String> lastPreferences = Collections.synchronizedList(new ArrayList<>());

    public ClientUI() {
        configureWindow();
        setupNavigation();
        setupMainContent();
        ClientAPI.setOnAssignmentReceived(this::updateAssignmentDisplay);
        setVisible(true);
    }

    private void configureWindow() {
        setTitle("Volunteer Matching System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
    }

    private void setupNavigation() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SECONDARY_COLOR);
        navPanel.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));
        navPanel.setPreferredSize(new Dimension(200, 0));

        String[] navItems = {"Home", "My Preferences", "My Assignments", "About"};
        for (String item : navItems) {
            JButton btn = createNavButton(item);
            navPanel.add(btn);
            navPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        add(navPanel, BorderLayout.WEST);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(170, 45));
        btn.setBackground(NAV_BUTTON_COLOR);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            if (text.equals("My Assignments")) {
                refreshAssignmentPanel();
            }
            cardLayout.show(mainContentPanel, text);
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(NAV_BUTTON_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(NAV_BUTTON_COLOR);
            }
        });

        return btn;
    }

    private void setupMainContent() {
        mainContentPanel = new JPanel();
        cardLayout = new CardLayout();
        mainContentPanel.setLayout(cardLayout);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainContentPanel.add(createHomePanel(), "Home");
        mainContentPanel.add(createPreferencesPanel(), "My Preferences");
        mainContentPanel.add(createAssignmentsPanel(), "My Assignments");
        mainContentPanel.add(createAboutPanel(), "About");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Welcome, Volunteer!");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        String[][] cards = {
            {"Set Your Preferences", "Choose your top 5 preferred volunteer activities", "My Preferences"},
            {"View Assignments", "See where you've been assigned to help", "My Assignments"},
            {"Learn More", "Understand how our matching system works", "About"}
        };

        for (String[] card : cards) {
            JPanel cardPanel = createInfoCard(card[0], card[1], card[2]);
            content.add(cardPanel);
            content.add(Box.createRigidArea(new Dimension(0, 25)));
        }

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoCard(String title, String description, String action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(600, 120));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(Color.WHITE);
        descArea.setFont(SUBTITLE_FONT);
        descArea.setForeground(Color.BLACK);

        JButton actionBtn = new JButton("Go to " + action);
        actionBtn.setBackground(HOME_BUTTON_COLOR);
        actionBtn.setForeground(Color.BLACK);
        actionBtn.setBorderPainted(false);
        actionBtn.setFocusPainted(false);
        actionBtn.addActionListener(e -> cardLayout.show(mainContentPanel, action));

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.EAST);

        return card;
    }

    private JPanel createPreferencesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Your Volunteer Preferences");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        preferenceSelectors = new JComboBox[5];
        String[] labels = {"First Choice", "Second Choice", "Third Choice", 
                          "Fourth Choice", "Fifth Choice"};

        for (int i = 0; i < 5; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(Color.WHITE);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel label = new JLabel(labels[i]);
            label.setPreferredSize(new Dimension(120, 30));
            label.setFont(SUBTITLE_FONT);
            label.setForeground(Color.BLACK);

            preferenceSelectors[i] = new JComboBox<>(ClientAPI.getAllServices());
            preferenceSelectors[i].setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (index == 0) setText("Select...");
                    return this;
                }
            });
            preferenceSelectors[i].setPreferredSize(new Dimension(300, 30));
            preferenceSelectors[i].setFont(SUBTITLE_FONT);
            preferenceSelectors[i].setForeground(Color.BLACK);

            final int currentIndex = i;
            preferenceSelectors[i].addActionListener(e -> checkForDuplicates(currentIndex));

            row.add(label);
            row.add(preferenceSelectors[i]);
            formPanel.add(row);
            formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton submitBtn = createActionButton("Submit Preferences", e -> submitPreferences());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void checkForDuplicates(int currentIndex) {
        String selected = (String)preferenceSelectors[currentIndex].getSelectedItem();
        if (selected == null || "Select...".equals(selected)) return;

        for (int i = 0; i < 5; i++) {
            if (i != currentIndex && selected.equals(preferenceSelectors[i].getSelectedItem())) {
                JOptionPane.showMessageDialog(this,
                    "You've already selected '" + selected + "' as another preference.",
                    "Duplicate Selection", JOptionPane.WARNING_MESSAGE);
                preferenceSelectors[currentIndex].setSelectedIndex(0);
                return;
            }
        }
    }

    private void submitPreferences() {
        lastPreferences.clear();
        Set<String> uniqueSelections = new HashSet<>();

        for (JComboBox<String> box : preferenceSelectors) {
            String selected = (String)box.getSelectedItem();
            if (selected != null && !"Select...".equals(selected)) {
                if (!uniqueSelections.add(selected)) {
                    JOptionPane.showMessageDialog(this,
                        "Please remove duplicate selections before submitting.",
                        "Duplicate Preferences", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lastPreferences.add(selected);
            }
        }

        if (lastPreferences.size() < 3) {
            JOptionPane.showMessageDialog(this,
                "Please select at least 3 different preferences.",
                "Incomplete Preferences", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClientAPI.submitPreferences(lastPreferences);
        JOptionPane.showMessageDialog(this,
            "Your preferences have been submitted successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createAssignmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Your Assignment");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assignment Card
        JPanel assignmentCard = new JPanel(new BorderLayout());
        assignmentCard.setBackground(new Color(245, 245, 245));
        assignmentCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        assignmentCard.setMaximumSize(new Dimension(600, 100));

        JLabel assignmentTitle = new JLabel("Current Assignment:");
        assignmentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        assignmentTitle.setForeground(Color.BLACK);

        assignmentLabel = new JLabel("Not assigned yet");
        assignmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        assignmentLabel.setForeground(Color.BLACK);
        assignmentLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel assignmentText = new JPanel();
        assignmentText.setLayout(new BoxLayout(assignmentText, BoxLayout.Y_AXIS));
        assignmentText.setBackground(new Color(245, 245, 245));
        assignmentText.add(assignmentTitle);
        assignmentText.add(assignmentLabel);

        assignmentCard.add(assignmentText, BorderLayout.CENTER);
        content.add(assignmentCard);
        content.add(Box.createRigidArea(new Dimension(0, 40)));

        // Preferences Card
        JPanel prefsCard = new JPanel(new BorderLayout());
        prefsCard.setBackground(new Color(245, 245, 245));
        prefsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        prefsCard.setMaximumSize(new Dimension(600, 200));

        JLabel prefsTitle = new JLabel("Your Preferences:");
        prefsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        prefsTitle.setForeground(Color.BLACK);

        JTextArea prefsArea = new JTextArea(5, 30);
        prefsArea.setEditable(false);
        prefsArea.setLineWrap(true);
        prefsArea.setWrapStyleWord(true);
        prefsArea.setBackground(new Color(245, 245, 245));
        prefsArea.setFont(SUBTITLE_FONT);
        prefsArea.setForeground(Color.BLACK);
        prefsArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel prefsText = new JPanel(new BorderLayout());
        prefsText.setBackground(new Color(245, 245, 245));
        prefsText.add(prefsTitle, BorderLayout.NORTH);
        prefsText.add(new JScrollPane(prefsArea), BorderLayout.CENTER);

        prefsCard.add(prefsText, BorderLayout.CENTER);
        content.add(prefsCard);

        // Store reference for updates
        panel.putClientProperty("prefsArea", prefsArea);

        JButton optimizeBtn = createActionButton("Run Optimization", e -> {
            ClientAPI.triggerOptimization();
            JOptionPane.showMessageDialog(this,
                "Optimization process started. Your assignment will update shortly.",
                "Optimization", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(optimizeBtn);
        panel.add(content, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshAssignmentPanel() {
        SwingUtilities.invokeLater(() -> {
            Component comp = mainContentPanel.getComponent(2);
            if (comp instanceof JPanel) {
                JTextArea prefsArea = (JTextArea) ((JPanel) comp).getClientProperty("prefsArea");
                if (prefsArea != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < lastPreferences.size(); i++) {
                        sb.append(i+1).append(". ").append(lastPreferences.get(i));
                        if (i < lastPreferences.size()-1) sb.append("\n");
                    }
                    prefsArea.setText(sb.toString());
                }
            }
        });
    }

    private void updateAssignmentDisplay(String assignment) {
        SwingUtilities.invokeLater(() -> {
            assignmentLabel.setText(assignment != null ? assignment : "No assignment received");
            assignmentLabel.setForeground(assignment != null ? PRIMARY_COLOR : Color.BLACK);
        });
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("About Volunteer Matching");
        title.setFont(TITLE_FONT);
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(title, BorderLayout.NORTH);

        JTextArea aboutText = new JTextArea(
            "This system helps match volunteers with community service opportunities based on their preferences.\n\n" +
            "How It Works:\n" +
            "1. Select your top 5 preferred volunteer activities\n" +
            "2. Submit your preferences\n" +
            "3. The system optimizes assignments for all volunteers\n" +
            "4. View your personalized assignment\n\n" +
            "Key Features:\n" +
            "• Weighted preference matching\n" +
            "• Real-time assignment updates\n" +
            "• Simple, intuitive interface\n\n" +
            "Note: All data is temporary and resets when the server restarts."
        );
        aboutText.setEditable(false);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setFont(SUBTITLE_FONT);
        aboutText.setForeground(Color.BLACK);
        aboutText.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(aboutText);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createActionButton(String text, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 40));
        btn.addActionListener(action);
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(PRIMARY_COLOR);
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new ClientUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}