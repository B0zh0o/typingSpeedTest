package com.typinggame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
}
