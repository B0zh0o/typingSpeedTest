package com.typinggame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Random;

public class TypingGameFrame extends JFrame {

    private TypingPanel typingPanel;
    private TextFileManager textFileManager;
    private String loadedText = "";
    private Timer typingTimer;
    private int elapsedSeconds = 0;
    private JLabel timerLabel;

    public TypingGameFrame() {

        /*
         * Basic window settings.
         */
        setTitle("Typing Game");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        /*
         * BorderLayout lets us put controls at the top
         * and the typing panel in the center.
         */
        setLayout(new BorderLayout());

        /*
         * Create the main parts of the program.
         */
        typingPanel = new TypingPanel(this);
        textFileManager = new TextFileManager();

        timerLabel = new JLabel("Time: 0s");

        typingTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            timerLabel.setText("Time: " + elapsedSeconds + "s");
        });

        /*
         * Create the Load Text button.
         */
        JButton loadTextButton = new JButton("Load Text");

        /*
         * This code runs whenever the button is clicked.
         */
        loadTextButton.addActionListener(e -> {
            try {

                loadedText =
                    textFileManager.chooseTextFile(this);

                /*
                 * null means the user cancelled the file chooser.
                 */
                if (loadedText == null) {
                    return;
                }

                /*
                 * Don't start a typing test with an empty file.
                 */
                if (loadedText.isBlank()) {

                    JOptionPane.showMessageDialog(
                        this,
                        "The selected text file is empty.",
                        "Empty File",
                        JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                /*
                 * Give the newly loaded text to the typing panel.
                 */
                startNewSession(loadedText);

            } catch (IOException exception) {

                JOptionPane.showMessageDialog(
                    this,
                    "The selected file could not be read.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton generateButton = new JButton("Generate");

        generateButton.addActionListener(e -> {
            RandomWordGenerator generator = new RandomWordGenerator("typinggame/wordsPool");
            loadedText = generator.generateRandomText(400);
            startNewSession(loadedText);
        });

        JButton resetButton = new JButton("Reset");

        resetButton.addActionListener(e -> {
                if (typingPanel.session.getTargetText() == null || typingPanel.session.getTargetText().isBlank()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "No text loaded to reset.",
                        "Empty Text",
                        JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }
                typingPanel.resetSession();;
        });

        /*
         * Small panel containing window controls.
         */
        JPanel controlPanel = new JPanel();

        controlPanel.add(loadTextButton);
        controlPanel.add(generateButton);
        controlPanel.add(timerLabel);

        /*
         * Controls at the top.
         */
        add(controlPanel, BorderLayout.NORTH);

        /*
         * Typing area takes the rest of the window.
         */
        add(typingPanel, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel();

        bottomPanel.add(resetButton);

        add(bottomPanel, BorderLayout.SOUTH);

    }

    /*
     * Used by Main after the frame becomes visible.
     */
    public void requestTypingFocus() {
        typingPanel.requestFocusInWindow();
    }

    private void startNewSession(String text) {
        typingPanel.setTargetText(text);

        if(typingTimer != null) {
            if (typingTimer.isRunning()) {
                typingTimer.stop();
            }
            elapsedSeconds = 0;
            timerLabel.setText("Time: 0s");
        }
    }

    public void startTimerIfNeeded() {
        if (typingTimer != null && !typingTimer.isRunning()) {
            typingTimer.start();
        }
    }
    public void resetTimer() {
        if (typingTimer != null) {
            typingTimer.stop();
        }
        elapsedSeconds = 0;
        timerLabel.setText("Time: 0s");
    }
}