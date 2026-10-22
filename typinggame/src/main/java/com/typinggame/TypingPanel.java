package com.typinggame;

import java.awt.Point;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Map;


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
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import com.typinggame.TypingGameFrame; 

public class TypingPanel extends JPanel {

    private JTextPane textPane;
    private JScrollPane scrollPane;
    public TypingSession session;
    private TypingGameFrame parentFrame;
    private int lastVisualLineY = -1;
    private boolean resultsShown = false;

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
        textPane = new JTextPane() {


            @Override 
            public boolean getScrollableTracksViewportWidth(){
                return true;
            }
        };
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
        scrollPane = new JScrollPane(textPane);

        scrollPane.setPreferredSize(new Dimension(850, 180));

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        add(scrollPane, BorderLayout.CENTER);



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

    private void scrollWhenLineChanges() {
        int position = session.getCurrentPosition();

        if (textPane.getDocument().getLength() == 0) {
            return;
        }

        if(position >= textPane.getDocument().getLength()) {
            position = textPane.getDocument().getLength() -1;
        }

        try {
            Rectangle2D bounds = textPane.modelToView2D(position);

            if (bounds == null) {
                return;
            }

            int currentLineY = (int) bounds.getY();


            if (lastVisualLineY == -1) {
                lastVisualLineY = currentLineY;

                return;
            }

            if (currentLineY != lastVisualLineY) {
                lastVisualLineY = currentLineY;
                int lineHeight = textPane.getFontMetrics(getFont()).getHeight();
                
                int targetScrollY = currentLineY - (2*lineHeight);

                if (targetScrollY < 0) {
                    targetScrollY = 0;
                }

                scrollPane.getViewport().setViewPosition(new Point(0, targetScrollY));
            }
        } catch (BadLocationException e) {
            System.out.println("Couldn't determine current text row");
        }
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
        scrollWhenLineChanges();

        if (session.isFinished() && !resultsShown) {
            resultsShown = true;

            showResults();
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

        resultsShown = false;

        textPane.setText(normalizedText);
        makeTargetTextFaded();
        setUnderline(session.getCurrentPosition(), true);
        lastVisualLineY = -1;


        textPane.setCaretPosition(0);

        SwingUtilities.invokeLater(()-> {
            scrollPane.getViewport().setViewPosition(new Point(0,0));
            lastVisualLineY = -1;
        });

        requestFocusInWindow();
    }

    public void resetSession() {
        String currentText = session.getTargetText();
        session = new TypingSession(currentText);

        resultsShown = false;

        textPane.setText(currentText);
        textPane.setCaretPosition(0);
        makeTargetTextFaded();
        setUnderline(session.getCurrentPosition(), true);

        lastVisualLineY = -1;
        
        SwingUtilities.invokeLater(()-> {
            scrollPane.getViewport().setViewPosition(new Point(0,0));
            lastVisualLineY = -1;
        });

        requestFocusInWindow();
        setEnabled(true);
    }

    public void showResults() {

        parentFrame.stopTimer();
        int elapsedSeconds = parentFrame.getElapsedSeconds();

        if (elapsedSeconds <= 0) {
            elapsedSeconds = 1;
        }
        int wpm = session.calculateWPM(elapsedSeconds);
        int rawWpm = session.calculateRawWPM(elapsedSeconds);
        int accuracy = session.calculateAccuracy();

        List<Map.Entry<Character, Long>> top5 = session.getErrorTracker().getTop5MissedCharacters();

        ResultsFrame resultsFrame = new ResultsFrame(parentFrame, wpm, rawWpm, accuracy, top5);
        resultsFrame.setVisible(true);
    }


}