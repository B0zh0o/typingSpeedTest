package com.typinggame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.typinggame.logic.TypingSession;

//*
// A model for the History objects
//*

public class History {
    public int id;
    public int sessionId;
    public int sessionWpm;
    public int sessionRawWpm;
    public int accuracy;

    public int wrongCharacterErrorCount;
    public int missingCharacterErrorCount;
    public int missingSpaceErrorCount;

    public List<MissedCharacter> top5MissedCharacters;

    public History(TypingSession session, int elapsedSeconds) {
        this.sessionId = session.id;
        this.sessionWpm = session.calculateWPM(elapsedSeconds);
        this.sessionRawWpm = session.calculateRawWPM(elapsedSeconds);
        this.accuracy = session.calculateAccuracy();

        Map<ErrorType, Long> errorCounts = session.getErrorTracker().getErrorCountsByType();   
        
        this.wrongCharacterErrorCount = errorCounts.getOrDefault(ErrorType.WRONG_CHARACTER, 0L).intValue();
        this.missingCharacterErrorCount = errorCounts.getOrDefault(ErrorType.MISSING_CHARACTER, 0L).intValue();
        this.missingSpaceErrorCount = errorCounts.getOrDefault(ErrorType.MISSING_SPACE, 0L).intValue();

        List<Map.Entry<Character, Long>> rawTop5 = session.getErrorTracker().getTop5MissedCharacters();
        this.top5MissedCharacters = new ArrayList<>();
        
        for (Map.Entry<Character, Long> entry : rawTop5) {
            this.top5MissedCharacters.add(new MissedCharacter(entry.getKey(), entry.getValue()));
        }    
    }
}
