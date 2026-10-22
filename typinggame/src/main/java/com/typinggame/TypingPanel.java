package com.typinggame;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class TypingPanel extends JPanel {

    private JTextPane textPane;
    public TypingSession session;
    private TypingGameFrame parentFrame;

    public TypingPanel(TypingGameFrame parentFrame) {

        String targetText = "";
        session = new TypingSession(targetText);
        this.parentFrame = parentFrame;

        /*
         * BorderLayout makes the text pane fill the available
         * space inside this panel.
         */
        setLayout(new BorderLayout());
        

        /*
         * Dark background for the panel.
         */
        setBackground(new Color(40, 40, 40));

        /*
         * Adds empty space around the typing area.
         *
         * top, left, bottom, right
         */
        setBorder(BorderFactory.createEmptyBorder(150, 100, 150, 100));

        /*
         * JTextPane lets us style individual characters.
         */
        textPane = new JTextPane();

        /*
         * Put the entire target text into the text pane.
         */
        textPane.setText(session.getTargetText());

        textPane.setEditable(false);

        textPane.setFocusable(false);

        /*
         * Use a monospaced font so every character has approximately
         * the same width.
         */
        textPane.setFont(
            new Font("Monospaced", Font.PLAIN, 28));

        /*
         * Same background as the panel.
         */
        textPane.setBackground(new Color(40, 40, 40));

        /*
         * Add the text pane to the middle of the panel.
         */
        add(textPane, BorderLayout.CENTER);

        /*
         * Initially make the entire target text faded.
         */
        makeTargetTextFaded();

        /*
         * Set original underline
         */
        setUnderline(session.getCurrentPosition(), true);
        /*
         * The panel needs to be allowed to receive keyboard focus.
         */
        setFocusable(true);

        /*
         * Listen for keyboard input.
         */
        addKeyListener(new KeyAdapter() {

        @Override
        public void keyTyped(KeyEvent e) {

            char typedCharacter = e.getKeyChar();

            /*
            * Backspace is handled in keyPressed().
             */
            if (typedCharacter == '\b') {
                return;
            }

            /*
             * Ignore tabs/newlines.
             * A normal Space is still allowed.
             */
            if (Character.isWhitespace(typedCharacter) && typedCharacter != ' ') {
                return;
            }

            /*
             * Don't allow more typing after the test
             * is completely finished.
             */
            if (session.isFinished()) {
                return;
            }

            if (parentFrame != null) {
                parentFrame.startTimerIfNeeded();
            }

            int oldPosition =
                session.getCurrentPosition();

            setUnderline(oldPosition, false);

            TypingUpdate update = session.handleTypedCharacter(typedCharacter);

            applyTypingUpdate(update);
        }

        @Override
        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode()== KeyEvent.VK_BACK_SPACE) {

                int oldPosition = session.getCurrentPosition();

                setUnderline(oldPosition, false);

                TypingUpdate update = session.handleBackspace();

                applyTypingUpdate(update);
            }
        }
    });


    }


    private void applyTypingUpdate(TypingUpdate update) {

        /*
         * Backspace has undone these positions
         */
        for (int position : update.getResetPositions()) {
                markTargetCharacter(position);
        }

        /*
         * These positions became errors
         */
        for (int position : update.getIncorrectPositions()) {
            markIncorrect(position);
        }

        /*
         * These positions were correct
         */
        for (int position : update.getCorrectPositions()) {
            markCorrect(position);
        }

        /*
         * Put the underline underneath the new
         * current target position.
         */
        setUnderline(session.getCurrentPosition(), true);
    }


    /*
     * Makes the entire target text faded.
     */
    private void makeTargetTextFaded() {
        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, new Color(100, 100, 100));
        StyleConstants.setBold(style, false);
        document.setCharacterAttributes(0, session.getTargetText().length(), style, true);
    }

    /*
     * Changes one character to a bright colour
     * when the user types it correctly.
     */
    private void markCorrect(int position) {

        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, new Color(230, 230, 230));
        StyleConstants.setBold(style, false);
        document.setCharacterAttributes(position, 1, style, true);
    }

    /*
     * Changes one character to red when the
     * user types it incorrectly.
     */
    private void markIncorrect(int position) {
        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, new Color(220, 90, 90));
        StyleConstants.setBold(style, true);
        document.setCharacterAttributes(position, 1, style, true);
    }

    /*
     * Changes a character back to the faded target colour.
     *
     * Currently used when Backspace is pressed. I cant get it to work 
     */
    private void markTargetCharacter(int position) {

        StyledDocument document = textPane.getStyledDocument();

        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setForeground(style, new Color(100, 100, 100));
        StyleConstants.setBold(style, false);

        document.setCharacterAttributes(position, 1, style, true);
    }

    private void setUnderline(int position, boolean underline) {
        if (position < 0 || position >= session.getTargetText().length()) {
            return; 
        }

        StyledDocument document = textPane.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setUnderline(style, underline);
        document.setCharacterAttributes(position, 1, style, false);
    }

    public void setTargetText(String targetText) {
        String normalizedText = targetText.replace("\r\n", "\n").replace("\r", "\n").replaceAll(" +\n", "\n");
        session = new TypingSession(normalizedText);
        textPane.setText(normalizedText);
        makeTargetTextFaded();
        setUnderline(session.getCurrentPosition(), true);

        requestFocusInWindow();
    }

    public void resetSession() {
        String currentText = session.getTargetText();
        session = new TypingSession(currentText);
        textPane.setText(currentText);
        makeTargetTextFaded();
        setUnderline(session.getCurrentPosition(), true);
        requestFocusInWindow();
        parentFrame.resetTimer();
    }

}