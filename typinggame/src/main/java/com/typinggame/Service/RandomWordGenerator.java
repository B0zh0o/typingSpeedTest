package com.typinggame.service;

import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

//* A class responsible for reading the big file with words and picking on random from it */

public class RandomWordGenerator {
    private List<String> randomlyGeneratedWords;
    private Random random;

    public RandomWordGenerator(String filePath) {
        this.random = new Random();
        loadWords(filePath);
    }

    private void loadWords(String filePath) {
        try {
            String content = Files.readString(Paths.get(filePath));
            String[] splitWords = content.split("\\R+");

            randomlyGeneratedWords = Arrays.stream(splitWords).map(String::strip).filter(w -> !w.isEmpty()).collect(Collectors.toList());

            if(randomlyGeneratedWords.isEmpty()) {
                throw new IOException("File is empty!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            randomlyGeneratedWords = List.of("There", "was", "an", "error");
        }
    }

    public String generateRandomText(int wordCount) {
        if (randomlyGeneratedWords == null || randomlyGeneratedWords.isEmpty()) {
            return "";
        }     
        
        return random.ints(wordCount, 0, randomlyGeneratedWords.size()).mapToObj(randomlyGeneratedWords::get).collect(Collectors.joining(" "));
    }
}
