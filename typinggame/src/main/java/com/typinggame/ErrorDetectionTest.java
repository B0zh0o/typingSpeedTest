package com.typinggame;

public class ErrorDetectionTest {

    public static void main(String[] args) {

        testWrongCharacter();
        testExtraCharacter();
        testMissingCharacter();
        testMissingSpace();
        testEarlySpace();
    }

    private static void testWrongCharacter() {

        System.out.println("\n=== WRONG CHARACTER TEST ===");
        System.out.println("Target: cat");
        System.out.println("Typed:  cot");

        TypingSession session =
            new TypingSession("cat");

        type(session, "cot");

        printErrors(session);
    }

    private static void testExtraCharacter() {

        System.out.println("\n=== EXTRA CHARACTER TEST ===");
        System.out.println("Target: cat");
        System.out.println("Typed:  caxt");

        TypingSession session =
            new TypingSession("cat");

        type(session, "caxt");

        printErrors(session);
    }

    private static void testMissingCharacter() {

        System.out.println("\n=== MISSING CHARACTER TEST ===");
        System.out.println("Target: apple");
        System.out.println("Typed:  aple");

        TypingSession session =
            new TypingSession("apple");

        type(session, "aple");

        printErrors(session);
    }

    private static void testMissingSpace() {

        System.out.println("\n=== MISSING SPACE TEST ===");
        System.out.println("Target: hello world");
        System.out.println("Typed:  helloworld");

        TypingSession session =
            new TypingSession("hello world");

        type(session, "helloworld");

        printErrors(session);
    }

    private static void testEarlySpace() {

        System.out.println("\n=== EARLY SPACE TEST ===");
        System.out.println("Target: hello world");
        System.out.println("Typed:  he[SPACE]");

        TypingSession session =
            new TypingSession("hello world");

        session.handleTypedCharacter('h');
        session.handleTypedCharacter('e');
        session.handleTypedCharacter(' ');

        printErrors(session);
    }

    private static void type(
            TypingSession session,
            String text) {

        for (char character : text.toCharArray()) {
            session.handleTypedCharacter(character);
        }
    }

    private static void printErrors(
            TypingSession session) {

        if (session.getErrorTracker()
                .getErrors()
                .isEmpty()) {

            System.out.println("No errors recorded.");
            return;
        }

        for (TypingError error :
                session.getErrorTracker().getErrors()) {

            System.out.println(
                "Type:      " + error.getType()
            );

            System.out.println(
                "Position:  " + error.getPosition()
            );

            System.out.println(
                "Expected:  "
                + printable(
                    error.getExpCharacter())
            );

            System.out.println(
                "Typed:     "
                + printable(
                    error.getCharacter())
            );

            System.out.println(
                "Corrected: "
                + error.isCorrected()
            );

            System.out.println();
        }
    }

    private static String printable(
            Character character) {

        if (character == null) {
            return "null";
        }

        if (character == ' ') {
            return "[SPACE]";
        }

        return character.toString();
    }
}