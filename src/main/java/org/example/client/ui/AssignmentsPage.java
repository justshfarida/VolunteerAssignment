package org.example.client.ui;

import org.example.client.api.ClientAPI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** “My Assignments” view styled exactly like the old createAssignmentsPanel(). */
class AssignmentsPage extends JPanel {

    /* ------------------------------------------------------------------ */
    private final JLabel    assignmentLbl = new JLabel("Not assigned yet");
    private final JTextArea prefsArea     = new JTextArea();

    private final MainFrame frame;

    AssignmentsPage(MainFrame f) {
        this.frame = f;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        /* ---------- title ---------- */
        JLabel title = new JLabel("Your Assignment");
        title.setFont(MainFrame.TITLE_FONT);
        add(title, BorderLayout.NORTH);

        /* ---------- content column ---------- */
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        /* ── card 1 : assignment ───────────────── */
        JPanel assignCard = greyCard();
        assignCard.setMaximumSize(new Dimension(600, 100));

        JLabel assignTitle = new JLabel("Current Assignment:");
        assignTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        assignmentLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        JPanel txtCol = new JPanel();                        // vertical column
        txtCol.setLayout(new BoxLayout(txtCol, BoxLayout.Y_AXIS));
        txtCol.setBackground(assignCard.getBackground());
        txtCol.add(assignTitle);
        txtCol.add(Box.createRigidArea(new Dimension(0, 5)));
        txtCol.add(assignmentLbl);

        assignCard.add(txtCol, BorderLayout.CENTER);
        content.add(assignCard);
        content.add(Box.createRigidArea(new Dimension(0, 40)));

        /* ── card 2 : preferences ─────────────── */
        JPanel prefsCard = greyCard();
        prefsCard.setMaximumSize(new Dimension(600, 200));
        prefsCard.setLayout(new BorderLayout());

        JLabel prefsTitle = new JLabel("Your Preferences:");
        prefsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        prefsArea.setEditable(false);
        prefsArea.setBackground(prefsCard.getBackground());
        prefsArea.setFont(MainFrame.TEXT_FONT);
        prefsArea.setLineWrap(true);
        prefsArea.setWrapStyleWord(true);

        prefsCard.add(prefsTitle, BorderLayout.NORTH);
        prefsCard.add(new JScrollPane(prefsArea), BorderLayout.CENTER);
        content.add(prefsCard);

        add(content, BorderLayout.CENTER);

        /* ---------- blue footer bar with button ---------- */
        JButton runBtn = new JButton("Run Optimization");
        runBtn.setBackground(MainFrame.PRIMARY);
        runBtn.setForeground(Color.WHITE);
        runBtn.setFocusPainted(false);
        runBtn.setPreferredSize(new Dimension(180, 40));
        runBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { runBtn.setBackground(MainFrame.PRIMARY.darker()); }
            public void mouseExited (java.awt.event.MouseEvent e) { runBtn.setBackground(MainFrame.PRIMARY); }
        });
        runBtn.addActionListener(e -> ClientAPI.triggerOptimization());

        JPanel footer = new JPanel();
        footer.setBackground(MainFrame.PRIMARY);
        footer.add(runBtn);
        add(footer, BorderLayout.SOUTH);

        /* ---------- live updates ---------- */
        ClientAPI.setOnAssignmentReceived(json ->
                SwingUtilities.invokeLater(() -> assignmentLbl.setText(json)));

        ClientAPI.startPolling(MainFrame.USER_ID);
    }

    /* helper for a grey rounded card */
    private JPanel greyCard() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 245));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        return p;
    }

    /* called by MainFrame when user lands here */
    void refreshPrefsBox() {
        List<String> list = PreferencesPage.getLastPrefs();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(". ").append(list.get(i)).append('\n');
        }
        prefsArea.setText(sb.toString());
    }
}
