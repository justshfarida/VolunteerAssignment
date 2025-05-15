package org.example.client.ui;

import javax.swing.*;
import java.awt.*;

class Services extends JPanel {

    Services(MainFrame f) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        JLabel title = new JLabel("About Volunteer Matching");
        title.setFont(MainFrame.TITLE_FONT);
        add(title,BorderLayout.NORTH);

        JTextArea ta=new JTextArea("""
                This system matches volunteers to services based on their preferences.

                Steps:
                  1. Choose up to five preferred services
                  2. Submit preferences
                  3. Click “Run Optimization” (any volunteer can)
                  4. All open windows update automatically

                All data is stored in RAM; restarting the server clears everything.
                """);
        ta.setEditable(false); ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setBackground(Color.WHITE); ta.setFont(MainFrame.TEXT_FONT);

        add(new JScrollPane(ta),BorderLayout.CENTER);
    }
}
