package com.typinggame;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import java.util.List;
import java.util.Map;

/**
 * Class to create a frame containing the results of a session.
 *
 */
public class ResultsFrame extends JDialog {

    /**
     * Constructor to initialize an object of a resultFrame.
     * 
     * @param parent parent frame
     * @param wpm words per minute
     * @param rawWpm raw words per minute (errors included too)
     * @param accuracy accuracy of expected text vs typed text
     * @param top5 top 5 errors
     */
    public ResultsFrame(JFrame parent, int wpm, int rawWpm, int accuracy, List<Map.Entry<Character, Long>> top5) {

        super(parent, "Session Results", true);

        setSize(500, 650);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.setBackground(new Color(40, 40, 40));

        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Session Results");
        title.setFont(new Font("Monospaced", Font.BOLD, 30));
        title.setForeground(new Color(255, 255, 255));

        title.setAlignmentX(CENTER_ALIGNMENT);

        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(30));

        JLabel wpmLabel = new JLabel("WPM: " + wpm);
        styleStatisticLabel(wpmLabel);
        mainPanel.add(wpmLabel);

        mainPanel.add(Box.createVerticalStrut(15));

        JLabel rawwpmLabel = new JLabel("Raw WPM: " + rawWpm);
        styleStatisticLabel(rawwpmLabel);
        mainPanel.add(rawwpmLabel);

        mainPanel.add(Box.createVerticalStrut(15));

        JLabel accuracyLabel = new JLabel("Accuracy: " + accuracy + "%");
        styleStatisticLabel(accuracyLabel);
        mainPanel.add(accuracyLabel);

        mainPanel.add(Box.createVerticalStrut(35));

        JLabel missedTitle = new JLabel("Most Missed Characters");

        missedTitle.setFont(new Font("Monospaced", Font.BOLD, 22));
        missedTitle.setForeground(new Color(255, 255, 255));
        missedTitle.setAlignmentX(CENTER_ALIGNMENT);

        mainPanel.add(missedTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        /**
         * Enters the top 5 errors in a map.
         */
        for (int i = 0; i < top5.size(); i++) {
            Map.Entry<Character, Long> entry = top5.get(i);

            char c = entry.getKey();

            String characterText;

            if (c == ' ') {
                characterText = "SPACE";
            } else {
                characterText = String.valueOf(c);
            }

            JLabel resultLabel = new JLabel((i + 1)
             + ". " 
             + characterText 
             + " -- " 
             + entry.getValue() 
             + " errors");

            resultLabel.setFont(new Font("Monospaced", Font.PLAIN, 19));

            resultLabel.setForeground(new Color(190, 190, 190));

            resultLabel.setAlignmentX(CENTER_ALIGNMENT);

            mainPanel.add(resultLabel);
            mainPanel.add(Box.createVerticalStrut(10));
        }
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a style for labels.
     * 
     * @param label parameter for displayed text
     */
    private void styleStatisticLabel(JLabel label) {
        label.setFont(new Font("Monospaced", Font.BOLD, 24));

        label.setForeground(new Color(255, 255, 255));
        label.setAlignmentX(CENTER_ALIGNMENT);
    }
    
}
