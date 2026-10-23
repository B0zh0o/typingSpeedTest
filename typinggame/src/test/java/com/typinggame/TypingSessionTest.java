package com.typinggame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypingSessionTest {

    @Test
    public void correctCharacterMovesPosition() {
        TypingSession session = new TypingSession("abc");

        TypingUpdate update = session.handleTypedCharacter('a');

        /*
         * This assertion shows that handling a character 'a' in "abc" moves the position in the text by 1.
         * It also shows that there exists a position 0 in correctPositions,
         * as a was the correct key press.
         */
        assertEquals(1, session.getCurrentPosition());
        assertTrue(update.getCorrectPositions().contains(0));
    }

    @Test 
    public void wrongCharacterCausesError() {
        TypingSession session = new TypingSession("abc");

        TypingUpdate update = session.handleTypedCharacter('x');

        /*
         * This assertion shows that a incorrect character 'x' in "abc" causes its position
         * to be sent to incorrectPositions list.
         */
        assertEquals(1, session.getCurrentPosition());
        assertTrue(update.getIncorrectPositions().contains(0));
    }

    @Test 
    public void sessionEndsAfterFinalCharacter() {
        TypingSession session = new TypingSession("ab");

        /*
         * Session shouldn't be finished as 'b' isn't processed.
         */
        session.handleTypedCharacter('a');
        assertFalse(session.isFinished());

        /*
         * Session should be finished as last character 'b' is processed.
         */
        session.handleTypedCharacter('b');
        assertTrue(session.isFinished());

    }
}