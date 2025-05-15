package org.example.client.ui;

import javax.swing.*;
import java.awt.*;

class HomePage extends JPanel {

    HomePage(MainFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        JLabel title = new JLabel("Welcome, Volunteer!");
        title.setFont(MainFrame.TITLE_FONT);
        add(title,BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);

        addCard(list,"Set Your Preferences",
                "Choose your top 5 preferred volunteer activities","prefs",frame);
        addCard(list,"View Assignments",
                "See where you've been assigned to help","assign",frame);
        addCard(list,"Learn More",
                "Understand how our matching system works","about",frame);

        add(list,BorderLayout.CENTER);
    }

    private void addCard(JPanel parent,String t,String d,String page,MainFrame f){
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(20,20,20,20)));
        c.setMaximumSize(new Dimension(600,120));

        JLabel tl = new JLabel(t);
        tl.setFont(new Font("Segoe UI",Font.BOLD,18));

        JTextArea ta = new JTextArea(d); ta.setEditable(false);
        ta.setBackground(Color.WHITE); ta.setFont(MainFrame.TEXT_FONT);
        ta.setLineWrap(true); ta.setWrapStyleWord(true);

        JButton go = new JButton("Go");
        go.setBackground(MainFrame.NAV_BTN); go.setFocusPainted(false);
        go.addActionListener(e->f.showPage(page));

        c.add(tl,BorderLayout.NORTH);
        c.add(ta,BorderLayout.CENTER);
        c.add(go,BorderLayout.EAST);

        parent.add(c); parent.add(Box.createRigidArea(new Dimension(0,25)));
    }
}
