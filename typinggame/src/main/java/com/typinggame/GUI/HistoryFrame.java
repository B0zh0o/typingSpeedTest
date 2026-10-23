package com.typinggame.gui;

import com.typinggame.model.History;
import com.typinggame.model.MissedCharacter;
import com.typinggame.service.HistoryFileManager;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

/**
 * A class responsible for loading the frame and showing the needed history information
 * **/

public class HistoryFrame extends JDialog {

    public HistoryFrame(JFrame parent) {
        super(parent, "Session History", false);

        setSize(600, 700);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (parent instanceof TypingGameFrame) {
                    TypingGameFrame gameFrame = (TypingGameFrame) parent;
                    gameFrame.resumeGameAfterHistory();
                }
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Session History");
        title.setFont(new Font("Monospaced", Font.BOLD, 28));
        title.setForeground(new Color(255, 255, 255));
        title.setAlignmentX(CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(20));

        HistoryFileManager historyFileManager = new HistoryFileManager("typinggame/history.json");
        List<History> allHistories = historyFileManager.loadAllHistories();

        if (allHistories.isEmpty()) {
            JLabel emptyLabel = new JLabel("No history found.");
            emptyLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
            emptyLabel.setForeground(new Color(180, 180, 180));
            emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            mainPanel.add(emptyLabel);
        } else {
            for (int i = allHistories.size() - 1; i >= 0; i--) {
                History h = allHistories.get(i);

                JPanel sessionCard = new JPanel();
                sessionCard.setLayout(new BoxLayout(sessionCard, BoxLayout.Y_AXIS));
                sessionCard.setBackground(new Color(55, 55, 55));
                sessionCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                sessionCard.setAlignmentX(CENTER_ALIGNMENT);
                sessionCard.setMaximumSize(new Dimension(500, 130));

                JLabel sessionTitle = new JLabel("Session #" + h.sessionId);
                sessionTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
                sessionTitle.setForeground(new Color(220, 220, 220));
                sessionCard.add(sessionTitle);
                sessionCard.add(Box.createVerticalStrut(8));

                JLabel statsLabel = new JLabel(String.format("WPM: %d | Raw WPM: %d | Accuracy: %d%%", h.sessionWpm, h.sessionRawWpm, h.accuracy));
                statsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));
                statsLabel.setForeground(new Color(200, 200, 200));
                sessionCard.add(statsLabel);
                sessionCard.add(Box.createVerticalStrut(5));

                JLabel errorsLabel = new JLabel(String.format("Errors -> Wrong: %d, Missing Char: %d, Missing Space: %d", 
                h.wrongCharacterErrorCount, h.missingCharacterErrorCount, h.missingSpaceErrorCount));
                errorsLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
                errorsLabel.setForeground(new Color(170, 170, 170));
                sessionCard.add(errorsLabel);

                String top5Formatted = "None";
                if (h.top5MissedCharacters != null && !h.top5MissedCharacters.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < h.top5MissedCharacters.size(); j++) {
                        MissedCharacter mc = h.top5MissedCharacters.get(j);
                        char c = mc.character;
                        String charText = (c == ' ') ? "SPACE" : String.valueOf(c);
                        
                        sb.append(charText).append("->").append(mc.count);
                        
                        if (j < h.top5MissedCharacters.size() - 1) {
                            sb.append(" | ");
                        }
                    }
                    top5Formatted = sb.toString();
                }

                JLabel top5Label = new JLabel("Top Missed: " + top5Formatted);
                top5Label.setFont(new Font("Monospaced", Font.PLAIN, 12));
                top5Label.setForeground(new Color(230, 150, 150));
                sessionCard.add(top5Label);

                mainPanel.add(sessionCard);
                mainPanel.add(Box.createVerticalStrut(15));
            }
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
}
