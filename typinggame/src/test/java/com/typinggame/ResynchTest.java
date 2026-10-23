package com.typinggame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResynchTest {

    @Test
    public void resynchPreventsCascadingError() {
        TypingSession session = new TypingSession("abc");

        session.handleTypedCharacter('x');

        TypingUpdate update = session.handleTypedCharacter('a');
        /*
         * The assertion tests when given a string abc,
         * When a wrong character x is input for a, it should then consume the a keypress
         * that was pressed in the next position as to stop from one
         * character misalignment making the entire text filled with errors.
         * 
         * Based on this assertion both correctPositions and incorrectPositions should be empty
         */
        assertEquals(1, session.getCurrentPosition());
        assertTrue(update.getCorrectPositions().isEmpty());
        assertTrue(update.getIncorrectPositions().isEmpty());

        /*
         * Assures that the resynch does not cause any further errors.
         */
        session.handleTypedCharacter('b');
        assertEquals(2, session.getCurrentPosition());
    }

    @Test 
    public void repeatedLetterIsNotConsumedByResynch() {
        TypingSession session = new TypingSession("all");

        session.handleTypedCharacter('a');

        /*
         * Wrong character for first 'l'.
         */
        session.handleTypedCharacter('x');
        assertEquals(2, session.getCurrentPosition());

        /*
         * Current position expects 'l' too.
         * The assertion shows that the position of the second 'l' was put into the correctPositions list.
         */
        TypingUpdate update = session.handleTypedCharacter('l');
        assertEquals(3, session.getCurrentPosition());
        assertTrue(update.getCorrectPositions().contains(2));
    }
}
