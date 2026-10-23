package com.typinggame;


import org.junit.jupiter.api.Test;

import com.typinggame.Logic.TypingSession;
import com.typinggame.Logic.TypingUpdate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BackSpaceTests {

    @Test
    public void backspaceMovesPositionBack() {
        TypingSession session = new TypingSession("abc");

        session.handleTypedCharacter('a');

        assertEquals(1, session.getCurrentPosition());

        TypingUpdate update = session.handleBackspace();

        /*
         * The assertions show that the current position moved back to 0,
         * as the previous assertion holds and reset contains 0.
         */
        assertEquals(0, session.getCurrentPosition());
        assertTrue(update.getResetPositions().contains(0));
    }

    @Test 
    public void backspaceAtStartIsIgnored() {
        TypingSession session = new TypingSession("abc");

        TypingUpdate update = session.handleBackspace();

        /*
         * The following assertions show that backspace has been ignored
         * and that the current position is 0 even after a backspace call.
         */
        assertTrue(update.isIgnored());
        assertEquals(0, session.getCurrentPosition());
    }

    @Test 
    public void backSpaceCorrectsError() {
        TypingSession session = new TypingSession("abc");

        /*
         * The assertion shows that x has been logged as a error.
         */
        session.handleTypedCharacter('x');
        assertEquals(1, session.getErrorTracker().getActiveErrorCount());

        session.handleBackspace();

        /*
         * The assertion under shows that the backspace has reduced the
         * number of errors has decreased by 1.
         */
        assertEquals(0, session.getErrorTracker().getActiveErrorCount());
    }

    @Test 
    public void cannotBackspaceIntoCompletedWord() {
        TypingSession session = new TypingSession("Hello World");

        session.handleTypedCharacter('H');
        session.handleTypedCharacter('e');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter('l');
        session.handleTypedCharacter('o');
        session.handleTypedCharacter(' ');

        int position = session.getCurrentPosition();

        TypingUpdate update = session.handleBackspace();

        assertTrue(update.isIgnored());
        assertEquals(position, session.getCurrentPosition());
    }
}
