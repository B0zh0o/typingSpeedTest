package com.typinggame;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextFileManager {
    
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
