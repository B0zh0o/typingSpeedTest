package com.typinggame.Service;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Manages the loading of text files.
 * 
 */
public class TextFileManager {
    
    /**
     * Filters so the user can only select text files, then returns them as a string.
     * 
     * @param parent parent frame
     * @return text file as a string
     * @throws IOException parent frame handles IO exceptions
     */
    public String chooseTextFile(Component parent) throws IOException {

        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*text)", "txt");

        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(parent);

        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        Path selectedFile = fileChooser.getSelectedFile().toPath();

        return Files.readString(selectedFile);
    }
}
