package org.example.client.ui;

import org.example.client.api.ClientAPI;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class PreferencesPage extends JPanel {

    private final MainFrame frame;

    private final JTextField nameField = new JTextField(20);
    @SuppressWarnings("unchecked")
    private final JComboBox<String>[] boxes = new JComboBox[5];

    /* cache of last prefs so AssignmentPage can show them */
    private static final List<String> lastSubmitted = new CopyOnWriteArrayList<>();

    /* --------------------------------------------------- */
    PreferencesPage(MainFrame f) {
        this.frame = f;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        /* ---------- title ---------- */
        JLabel title = new JLabel("Your Volunteer Preferences");
        title.setFont(MainFrame.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        /* ---------- form ---------- */
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        /* name row */
        JPanel nameRow = makeRow("Your Name");
        nameRow.add(nameField);
        form.add(nameRow);
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        /* 5 preference rows */
        String[] labs = {"First", "Second", "Third", "Fourth", "Fifth"};
        for (int i = 0; i < 5; i++) {
            boxes[i] = new JComboBox<>(ClientAPI.getAllServices());
            boxes[i].setPreferredSize(new Dimension(300, 30));
            JPanel row = makeRow(labs[i] + " Choice");
            row.add(boxes[i]);
            form.add(row);
            form.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        add(new JScrollPane(form), BorderLayout.CENTER);

        /* ---------- submit button ---------- */
        JButton submit = styledButton("Submit Preferences", this::handleSubmit);
        JPanel south = new JPanel();
        south.setBackground(Color.WHITE);
        south.add(submit);
        add(south, BorderLayout.SOUTH);
    }

    /* --------------------------------------------------- */
    private JPanel makeRow(String label) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT));
        r.setBackground(Color.WHITE);

        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(120, 30));
        l.setFont(MainFrame.TEXT_FONT);
        r.add(l);

        return r;
    }

    private JButton styledButton(String txt, Runnable action) {
        JButton b = new JButton(txt);
        b.setBackground(MainFrame.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(200, 40));

        b.addActionListener(e -> action.run());
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(MainFrame.PRIMARY.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(MainFrame.PRIMARY);
            }
        });
        return b;
    }

    /* --------------------------------------------------- */
    private void handleSubmit() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name");
            return;
        }

        /* collect prefs */
        lastSubmitted.clear();
        Set<String> seen = new HashSet<>();
        for (JComboBox<String> box : boxes) {
            String sel = (String) box.getSelectedItem();
            if (sel != null && seen.add(sel))
                lastSubmitted.add(sel);
        }
        if (lastSubmitted.size() < 5) {
            JOptionPane.showMessageDialog(this, "Select 5 unique choices");
            return;
        }

        /* send to server */
        ClientAPI.sendPreferences(MainFrame.USER_ID, name, lastSubmitted);

        JOptionPane.showMessageDialog(this, "Preferences stored!");
        frame.showPage("assign");                 // go to assignments
    }

    /* called by AssignmentPage */
    static List<String> getLastPrefs() {
        return List.copyOf(lastSubmitted);
    }
}
