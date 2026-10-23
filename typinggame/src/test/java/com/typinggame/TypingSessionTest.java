package com.typinggame;

import org.junit.jupiter.api.Test;

import com.typinggame.Logic.TypingSession;
import com.typinggame.Logic.TypingUpdate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypingSessionTest {
    @Test
    void TestPerfectTypingMetrics() {
        TypingSession session = new TypingSession("water bread pie");

        String target = session.getTargetText();

        for (int i = 0; i < target.length(); i++) {
            session.handleTypedCharacter(target.charAt(i));
        }

        assertEquals(100, session.calculateAccuracy(), "Accuracy should be 100% for flawless typing.");
        
        int wpm = session.calculateWPM(60);
        assertEquals(3, wpm, "WPM calculation is incorrect for 60 seconds.");

        int rawWpm = session.calculateRawWPM(60);
        assertEquals(3, rawWpm, "Raw WPM should match WPM when there are no errors.");
    }

    @Test 
    void testAccuracyWithErrors() {
        TypingSession session = new TypingSession("abc");

        session.handleTypedCharacter('x');
        session.handleTypedCharacter('b');
        session.handleTypedCharacter('c');

        int accuracy = session.calculateAccuracy();
        assertTrue(accuracy < 100, "Accuracy should drop due to the typing error.");
    }

    @Test
    void testZeroElapsedTimeHandling() {
        TypingSession session = new TypingSession("hello");

        assertEquals(0, session.calculateWPM(0), "WPM should be 0 if elapsed time is 0.");
        assertEquals(0, session.calculateRawWPM(0), "Raw WPM should be 0 if elapsed time is 0.");
    }

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
