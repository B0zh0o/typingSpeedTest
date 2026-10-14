package com.typinggame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.io.IOException;

public class TypingGameFrame extends JFrame {

    private TypingPanel typingPanel;
    private TextFileManager textFileManager;

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
        typingPanel = new TypingPanel();
        textFileManager = new TextFileManager();

        /*
         * Create the Load Text button.
         */
        JButton loadTextButton = new JButton("Load Text");

        /*
         * This code runs whenever the button is clicked.
         */
        loadTextButton.addActionListener(e -> {

            try {

                String loadedText =
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
                typingPanel.setTargetText(loadedText);

            } catch (IOException exception) {

                JOptionPane.showMessageDialog(
                    this,
                    "The selected file could not be read.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        /*
         * Small panel containing window controls.
         */
        JPanel controlPanel = new JPanel();

        controlPanel.add(loadTextButton);

        /*
         * Controls at the top.
         */
        add(controlPanel, BorderLayout.NORTH);

        /*
         * Typing area takes the rest of the window.
         */
        add(typingPanel, BorderLayout.CENTER);
    }

    /*
     * Used by Main after the frame becomes visible.
     */
    public void requestTypingFocus() {
        typingPanel.requestFocusInWindow();
    }
}