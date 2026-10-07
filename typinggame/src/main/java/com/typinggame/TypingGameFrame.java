package com.typinggame;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;

public class TypingGameFrame extends JFrame{
    
    private TypingPanel typingPanel; 

    public TypingGameFrame() {
        setTitle("Typing Game");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        getContentPane().setBackground(new Color(40, 40, 40));

        setLayout(new BorderLayout());

        typingPanel = new TypingPanel();

        add(typingPanel, BorderLayout.CENTER);

        typingPanel.requestFocusInWindow();
    }

    public void requestTypingFocus() {
        typingPanel.requestFocusInWindow();
    }
}
