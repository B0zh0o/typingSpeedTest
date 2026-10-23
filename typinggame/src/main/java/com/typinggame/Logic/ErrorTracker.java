package com.typinggame.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.typinggame.model.ErrorType;
import com.typinggame.model.TypingError;

/**
 * Class to track the errors made in the text.
 * 
 * ErrorTracker
 */
public class ErrorTracker {

    public final List<TypingError> errors = new ArrayList<>();

    /**
     * Adds a new error to a error list.
     * 
     * @param type type of error
     * @param position position of error in text
     * @param expCharacter the expected character
     * @param typCharacter the actual character
     */
    public void record(ErrorType type, int position,
         Character expCharacter, Character typCharacter) {
        errors.add(new TypingError(type, position, expCharacter, typCharacter));
    }

    /**
     * Marks the errors in the range as corrected.
     * 
     * @param startInclusive inclusive start
     * @param endExclusive exclusive end
     */
    public void markCorrectedInRange(int startInclusive, int endExclusive) {
        errors.stream().filter(error -> !error.isCorrected())
            .filter(error -> error.getPosition() >= startInclusive)
            .filter(error -> error.getPosition() < endExclusive)
            .forEach(error -> error.setCorrected(true));
    }

    /**
     * Gets the error counts.
     * 
     * @return the count of errors
     */
    public long getActiveErrorCount() {
        return errors.stream().filter(error -> !error.isCorrected()).count();
    }

    /**
     * Gets the error counts by the type of error.
     * 
     * @return a map of the error type and their count
     */
    public Map<ErrorType, Long> getErrorCountsByType() {
        return errors.stream()
            .collect(Collectors.groupingBy(
                TypingError::getType, 
                Collectors.counting()));
    }


    /**
     * Uses streams to get the most missed characters and their error counts.
     * 
     * @return the most missed characters
     */
    public List<Map.Entry<Character, Long>> getMostMissedCharacters() {
        return errors.stream()
            .filter(error -> error.getType() == ErrorType.MISSING_CHARACTER)
            .filter(error -> error.getExpCharacter() != null)
            .collect(Collectors.groupingBy(
                TypingError::getExpCharacter,
                Collectors.counting()))
            .entrySet().stream()
            .sorted(
                Map.Entry.<Character, Long>
                comparingByValue()
                .reversed())
            .toList(); 
    }

    public List<TypingError> getErrors() {
        return List.copyOf(errors);
    }


     
    /**
     * Returns the five most missed characters in the session.
     * 
     * @return the five most missed characters and their error counts as a list
     */
    public List<Map.Entry<Character, Long>> getTop5MissedCharacters() {
        return errors.stream()
        .filter(error -> error.getExpCharacter() != null)
        .collect(Collectors.groupingBy(
            TypingError::getExpCharacter,
             Collectors.counting()))
        .entrySet()
        .stream()
        .sorted(Map.Entry.<Character, Long>
            comparingByValue()
            .reversed())
            .limit(5)
            .toList();
    }
}
