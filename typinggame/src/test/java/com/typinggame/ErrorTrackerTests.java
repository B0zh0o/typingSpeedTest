package com.typinggame;

import org.junit.jupiter.api.Test;

import com.typinggame.logic.ErrorTracker;
import com.typinggame.model.ErrorType;
import com.typinggame.model.TypingError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import java.util.Map;

public class ErrorTrackerTests {

    @Test
    public void recordAddsError() {
        ErrorTracker tracker = new ErrorTracker();
        
        tracker.record(ErrorType.WRONG_CHARACTER, 0, 'a', 'x');
        
        assertEquals(1, tracker.getErrors().size());

        TypingError error = tracker.getErrors().get(0);

        assertEquals(ErrorType.WRONG_CHARACTER, error.getType());
        assertEquals('a', error.getExpCharacter());
        assertEquals('x', error.getCharacter());
    }

    @Test
    public void correctedErrorIsNotActive() {
        ErrorTracker tracker = new ErrorTracker();

        tracker.record(ErrorType.WRONG_CHARACTER, 0, 'a', 'x');

        assertEquals(1, tracker.getActiveErrorCount());

        tracker.markCorrectedInRange(0, 1);

        assertEquals(0, tracker.getActiveErrorCount());
        assertTrue(tracker.getErrors().get(0).isCorrected());
    }

    @Test 
    public void errorCountsAreGroupedByType() {
        ErrorTracker tracker = new ErrorTracker();

        tracker.record(ErrorType.WRONG_CHARACTER, 0, 'x', 'a');
        tracker.record(ErrorType.WRONG_CHARACTER, 1, 'y', 'b');
        tracker.record(ErrorType.MISSING_CHARACTER, 2, 'c', null);

        assertEquals(2L, tracker.getErrorCountsByType().get(ErrorType.WRONG_CHARACTER));
        assertEquals(1L, tracker.getErrorCountsByType().get(ErrorType.MISSING_CHARACTER));
    }

    @Test 
    public void mostMissedCharactersComeFirst() {
        ErrorTracker tracker = new ErrorTracker();

        tracker.record(ErrorType.WRONG_CHARACTER, 0, 'a', 'x');
        tracker.record(ErrorType.WRONG_CHARACTER, 1, 'a', 'y');
        tracker.record(ErrorType.WRONG_CHARACTER, 2, 'b', 'x');

        List<Map.Entry<Character, Long>> result = tracker.getTop5MissedCharacters();

        assertEquals('a', result.get(0).getKey());
        assertEquals(2L, result.get(0).getValue());
    }

}
