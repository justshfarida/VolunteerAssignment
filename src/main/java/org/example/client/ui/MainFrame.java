package org.example.client.ui;


import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    /* ---------- global constants ---------- */
    public static final String USER_ID = "vol1";          // one id everywhere

    static final Color PRIMARY   = new Color(0, 120, 215);
    static final Color SECONDARY = new Color(255, 182, 193);
    static final Color NAV_BTN   = new Color(255, 105, 180);
    static final Font  TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    static final Font  TEXT_FONT  = new Font("Segoe UI", Font.PLAIN, 16);

    /* ---------- card layout ---------- */
    private final CardLayout card = new CardLayout();
    private final JPanel     cards = new JPanel(card);

    /* keep references so we can refresh */
    private final AssignmentsPage assignmentPage = new AssignmentsPage(this);
    private final PreferencesPage preferencesPage = new PreferencesPage(this);

    public MainFrame() {
        setTitle("Volunteer Matching System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildNav(), BorderLayout.WEST);

        cards.add(new HomePage(this),         "home");
        cards.add(preferencesPage,            "prefs");
        cards.add(assignmentPage,             "assign");
        cards.add(new Services(this),         "about");

        add(cards, BorderLayout.CENTER);
        card.show(cards, "home");

        setVisible(true);
    }

    /* public so children can navigate */
    public void showPage(String key) {
        card.show(cards, key);
        if ("assign".equals(key)) assignmentPage.refreshPrefsBox();
    }

    /* ---------------- navigation pane ---------------- */
    private JPanel buildNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(SECONDARY);
        nav.setPreferredSize(new Dimension(200,0));
        nav.setBorder(BorderFactory.createEmptyBorder(30,15,30,15));

        addNav(nav, "Home",           "home");
        addNav(nav, "My Preferences", "prefs");
        addNav(nav, "My Assignments", "assign");
        addNav(nav, "About",          "about");
        return nav;
    }

    private void addNav(JPanel nav,String text,String page) {
        JButton b = new JButton(text);
        b.setMaximumSize(new Dimension(170,45));
        b.setBackground(NAV_BTN);
        b.setFont(TEXT_FONT);
        b.setFocusPainted(false);
        b.addActionListener(e -> showPage(page));
        b.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){ b.setBackground(NAV_BTN.darker()); }
            public void mouseExited (java.awt.event.MouseEvent e){ b.setBackground(NAV_BTN); }
        });
        nav.add(b);
        nav.add(Box.createRigidArea(new Dimension(0,15)));
    }
}
