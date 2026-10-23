package com.typinggame;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class RandomWordGeneratorTest {
    private static final String TEST_WORDS_FILE = "test_words.txt";

    @BeforeEach 
    void SetUp() throws IOException {
        try (FileWriter writer = new FileWriter(TEST_WORDS_FILE)) {
            writer.write("apple/nbanana/ncherry/norange/nblueberry");
        }
    }

    @AfterEach 
    void tearDown() {
        File file = new File(TEST_WORDS_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testGenerateRandomText() {
        RandomWordGenerator generator = new RandomWordGenerator(TEST_WORDS_FILE);

        String generatedText = generator.generateRandomText(5);

        assertNotNull(generatedText, "Generated text should not be null.");
        assertFalse(generatedText.isBlank(), "Generated text should not be blank.");

        assertTrue(generatedText.contains("apple") || generatedText.contains("banana") || 
                    generatedText.contains("cherry") || generatedText.contains("orange") ||
                    generatedText.contains("blueberry"), "Generated text should contain words from the pool.");
    }
}
