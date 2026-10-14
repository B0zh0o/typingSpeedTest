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
    private TypingSession session;

    public TypingPanel() {

        String targetText = "Hello World! This is a demo for Thursday's Practical Session Hello World Hello World";
        session = new TypingSession(targetText);

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

                if(typedCharacter == '\b') {
                    return;
                }
                if(Character.isWhitespace(typedCharacter)) {
                    int oldPosition = session.getCurrentPosition();

                    setUnderline(oldPosition, false);
                    
                    handleSpaceVisually();

                    setUnderline(session.getCurrentPosition(), true);
                    return;
                }

                /*
                 * Ignore typing once we have reached the
                 * end of the target text.
                 */
                if (session.isFinished()) {
                    return;
                }

                int oldPosition = session.getCurrentPosition();

                setUnderline(oldPosition, false);

                boolean correct = session.handleCharacter(typedCharacter);

                if (correct) {
                    markCorrect(oldPosition);
                } else {
                    markIncorrect(oldPosition);
                }

                setUnderline(session.getCurrentPosition(), true);
            }

            @Override
            public void keyPressed(KeyEvent e) {

                /*
                 * Simple Backspace support. DOESNT WORK
                 */
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    int oldPosition = session.getCurrentPosition();

                    setUnderline(oldPosition, false);
                    
                    int position = session.handleBackspace();

                    if (position != -1) {
                        markTargetCharacter(position);

                        setUnderline(session.getCurrentPosition(), true);
                    }
                }
            }
        });
    }

    private void handleSpaceVisually() {

        int start = session.getCurrentPosition();

        int oldWordEnd = session.handleSpace();

        if (oldWordEnd == -1) {
            return;
        }

        for (int i = start; i < oldWordEnd; i++) {
            markIncorrect(i);
        }

        if (oldWordEnd < session.getTargetText().length()) {
            markCorrect(oldWordEnd);
        }
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
        session = new TypingSession(targetText);
        textPane.setText(targetText);
        makeTargetTextFaded();
        setUnderline(session.getCurrentPosition(), true);

        requestFocusInWindow();
    }

}