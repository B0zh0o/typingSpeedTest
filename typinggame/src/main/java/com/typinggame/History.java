

package com.typinggame;

import java.util.Map;

import com.typinggame.TypingSession;

public class History {
    public int id;
    public int sessionId;
    public int sessionWpm;

    public int wrongCharacterErrorCount;
    public int missingCharacterErrorCount;
    public int extraCharacterErrorCount;
    public int missingSpaceErrorCount;

    public History(TypingSession session) {
        this.id++;
        this.sessionWpm = session.wpm;

        Map<ErrorType, Long> errorCounts = session.getErrorTracker().getErrorCountsByType();   
        
        this.wrongCharacterErrorCount = errorCounts.getOrDefault(ErrorType.WRONG_CHARACTER, 0L).intValue();
        this.missingCharacterErrorCount = errorCounts.getOrDefault(ErrorType.MISSING_CHARACTER, 0L).intValue();
        this.missingSpaceErrorCount = errorCounts.getOrDefault(ErrorType.MISSING_SPACE, 0L).intValue();
    }
}
