package com.typinggame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.io.IOException;

/**
 * Represents the main frame for the program.
 * Manages typing panel, timer, controls, history, 
 * and starting and resetting sessions.
 */
public class TypingGameFrame extends JFrame {
    
    private TypingPanel typingPanel;
    private TextFileManager textFileManager;
    private String loadedText;
    private RandomWordGenerator initialGenerator;
    private HistoryFileManager historyFileManager;
    private Timer typingTimer;
    private int secondsLeft = 60;
    private JLabel timerLabel;
    private JLabel wpmLabel;

    /**
     * Creates and initializes main frame.
     */
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
        historyFileManager = new HistoryFileManager();

        initialGenerator = new RandomWordGenerator("typinggame/wordsPool");
        loadedText = initialGenerator.generateRandomText(400);
        startNewSession(loadedText);

        timerLabel = new JLabel("60s");
        wpmLabel = new JLabel("WPM: 0");

        typingTimer = new Timer(1000, e -> {
            if (secondsLeft > 0) {
                secondsLeft--;
                timerLabel.setText(secondsLeft + "s");

                int elapsed = 60 - secondsLeft;
                if (elapsed > 0) {
                    int currentWpm = typingPanel.session.calculateWPM(elapsed);
                    wpmLabel.setText("WPM: " + currentWpm);
                }

                if (secondsLeft <= 0) {
                    sessionCompleted();
                }
            }
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
            if (typingPanel.session.getTargetText() == null
                || typingPanel.session.getTargetText().isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    "No text loaded to reset.",
                    "Empty Text",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
                }
            resetTimer();
        });

        JButton historyButton = new JButton("History");

        historyButton.addActionListener(e -> {
            HistoryFrame historyFrame = new HistoryFrame(this);
            historyFrame.setVisible(true);
        });

        /*
         * Small panel containing window controls.
         */
        JPanel controlPanel = new JPanel();

        controlPanel.add(loadTextButton);
        controlPanel.add(generateButton);
        controlPanel.add(timerLabel);
        controlPanel.add(wpmLabel);

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
        bottomPanel.add(historyButton);

        add(bottomPanel, BorderLayout.SOUTH);

    }

    /**
     * Asks swing to give keyboard focus.
     */
    public void requestTypingFocus() {
        typingPanel.requestFocusInWindow();
    }

    /**
     * Starts a new session and enables the target text.
     *
     * @param text is the text from the program
     */
    private void startNewSession(String text) {
        typingPanel.setTargetText(text);
        typingPanel.setEnabled(true);

        if (typingTimer != null) {
            if (typingTimer.isRunning()) {
                typingTimer.stop();
            }
            secondsLeft = 60;
            timerLabel.setText("60s");
            wpmLabel.setText("WPM: 0");
        }
    }

    /**
     * Continues game after history.
     */
    public void resumeGameAfterHistory() {
        if (secondsLeft > 0) {
            typingPanel.setEnabled(true);
            typingPanel.requestFocusInWindow();
        }
    }

    /**
     * Gets the elapsed seconds counting down from 60.
     *
     * @return elapsed time
     */
    public int getElapsedSeconds() {
        return 60 - secondsLeft;
    }

    /**
     * Stops the timer if typingTimer isnt null.
     */
    public void stopTimer() {
        if (typingTimer != null) {
            typingTimer.stop();
        }
    }

    /**
     * Starts timer.
     */
    public void startTimerIfNeeded() {
        if (typingTimer != null && !typingTimer.isRunning() && secondsLeft > 0) {
            typingTimer.start();
        }
    }

    /**
     * Resets timer.
     */
    public void resetTimer() {
        if (typingTimer != null) {
            typingTimer.stop();
        }

        secondsLeft = 60;
        timerLabel.setText("60s");
        wpmLabel.setText("WPM: 0");
        
        if (typingPanel.session.getTargetText() == null
            || typingPanel.session.getTargetText().isBlank()) {
            RandomWordGenerator generator = new RandomWordGenerator("typinggame/wordsPool");
            loadedText = generator.generateRandomText(400);
            startNewSession(loadedText);
        } else {
            typingPanel.resetSession();
        }
    }

    /**
     * Prevents the user from continuing session after it is completed.
     */
    public void sessionCompleted() {
        stopTimer();
        int elapsedSeconds = getElapsedSeconds();

        if (elapsedSeconds <= 0) {
            elapsedSeconds = 1;
        }

        History newHistory = new History(typingPanel.session, elapsedSeconds);
        historyFileManager.saveHistory(newHistory);

        typingPanel.setEnabled(false);
        typingPanel.showResults();
    }

}