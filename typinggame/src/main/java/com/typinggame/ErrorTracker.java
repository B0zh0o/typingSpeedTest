package com.typinggame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ErrorTracker {

    public final List<TypingError> errors = new ArrayList<>();

    public void record(ErrorType type, int position, Character expCharacter, Character typCharacter) {
        errors.add(new TypingError(type, position, expCharacter, typCharacter));
    }

    public void markCorrectedInRange(int startInclusive, int endExclusive) {
        errors.stream().filter(error -> !error.isCorrected())
        .filter(error -> error.getPosition() >= startInclusive)
        .filter(error -> error.getPosition() < endExclusive)
        .forEach(error -> error.setCorrected(true));
    }

    public long getActiveErrorCount() {
        return errors.stream().filter(error -> !error.isCorrected()).count();
    }

    public Map<ErrorType, Long> getErrorCountsByType() {
        return errors.stream().collect(Collectors.groupingBy(TypingError::getType, Collectors.counting()));
    }

    public List<Map.Entry<Character, Long>> getMostMissedCharacters() {
        return errors.stream().filter(error -> error.getType() == ErrorType.MISSING_CHARACTER)
        .filter(error -> error.getExpCharacter() != null)
        .collect(Collectors.groupingBy(TypingError::getExpCharacter, Collectors.counting()))
        .entrySet().stream()
        .sorted(Map.Entry.<Character, Long>comparingByValue().reversed()).toList(); 
    }

    public List<TypingError> getErrors() {
        return List.copyOf(errors);
    }
}
