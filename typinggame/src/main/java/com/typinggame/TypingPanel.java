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

public class TypingPanel extends JPanel {

    private JTextPane textPane;

    private String targetText;
    private int currentPosition;

    public TypingPanel() {

        targetText = "This is a demo for the visual";
        currentPosition = 0;

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
        textPane.setText(targetText);

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
                 * Ignore typing once we have reached the
                 * end of the target text.
                 */
                if (currentPosition >= targetText.length()) {
                    return;
                }

                /*
                 * Get the character that the user is supposed
                 * to type at the current position.
                 */
                char expectedCharacter =
                    targetText.charAt(currentPosition);

                /*
                 * Compare what they typed with what was expected.
                 */
                if (typedCharacter == expectedCharacter) {

                    markCorrect(currentPosition);

                } else {

                    markIncorrect(currentPosition);
                }

                /*
                 * Move to the next character.
                 */
                currentPosition++;
            }

            @Override
            public void keyPressed(KeyEvent e) {

                /*
                 * Simple Backspace support.
                 */
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

                    if (currentPosition > 0) {

                        currentPosition--;

                        markTargetCharacter(currentPosition);
                    }
                }
            }
        });
    }

    /*
     * Makes the entire target text faded.
     */
    private void makeTargetTextFaded() {

        StyledDocument document = textPane.getStyledDocument();

        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setForeground(style, new Color(100, 100, 100));

        document.setCharacterAttributes(0, targetText.length(), style, true);
    }

    /*
     * Changes one character to a bright colour
     * when the user types it correctly.
     */
    private void markCorrect(int position) {

        StyledDocument document = textPane.getStyledDocument();

        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setForeground(style, new Color(230, 230, 230));

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
     * Currently used when Backspace is pressed.
     */
    private void markTargetCharacter(int position) {

        StyledDocument document = textPane.getStyledDocument();

        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setForeground(style, new Color(100, 100, 100));

        document.setCharacterAttributes(position, 1, style, true);
    }
}