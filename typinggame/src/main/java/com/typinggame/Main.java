package com.typinggame;

import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TypingGameFrame frame = new TypingGameFrame();
            frame.setVisible(true);
        });
    }
}