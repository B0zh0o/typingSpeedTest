package com.typinggame;

public class MultipleErrorsTest {

    public static void main(String[] args) {

        String targetText =
            "cat apple hello world";

        String typedText =
            "cot aple helxloworld";

        TypingSession session =
            new TypingSession(targetText);

        for (char character : typedText.toCharArray()) {
            session.handleTypedCharacter(character);
        }

        System.out.println(
            session.getErrorTracker().getErrors()
        );
    }
}