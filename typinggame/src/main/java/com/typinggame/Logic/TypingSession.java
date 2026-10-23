package com.typinggame.logic;

import java.util.HashSet;
import java.util.Set;

import com.typinggame.model.ErrorType;
import com.typinggame.model.TypingError;

import java.util.List;


/**
 * TypingSession manages typing state, character processing, errors, position,
 * accuracy and both wpms.
 */
public class TypingSession {
    
    public int id;
    private String targetText;
    private int currentPosition;
    private int currentWordEnd;
    private boolean spaceJustHandled;

    private int currentWordStart;
    private int lockedBoundary = 0;
    private boolean currentWordHasErrors = false;
    private boolean hasTypedInCurrentWord = false;
    private int lastActivePosition = 0;

    public int wpm;
    public int rawWpm;

    private Resynch resynch;
    private final ErrorTracker errorTracker;
    private final Set<Integer> activeCorrectPositions;
    public List<TypingError> errorsList;

    /**
     * Creates a typing session object.
     *
     * @param targetText the text that is going to be processed.
     */
    public TypingSession(String targetText) {
        this.targetText = targetText;
        this.currentPosition = 0;
        this.currentWordEnd = findWordSpace(currentPosition);
        this.spaceJustHandled = false;
        this.currentWordStart = 0;
        this.lockedBoundary = 0;
        this.lastActivePosition = 0;
        this.wpm = 0;
        this.rawWpm = 0;
        this.errorTracker = new ErrorTracker();
        this.activeCorrectPositions = new HashSet<>();
        this.resynch = null;
	    this.errorsList = List.copyOf(errorTracker.errors);
        
    }

    private int findWordSpace(int position) {
        int spacePosition = position;
        while ((spacePosition < targetText.length()) && !Character.isWhitespace(targetText.charAt(spacePosition))) {
            spacePosition++;
            }
        return spacePosition;
    }

    private void refreshCurrentWordBounds() {
        int start = currentPosition;
        while (start > lockedBoundary && !Character.isWhitespace(targetText.charAt(start - 1))) {
            start--;
        }
        currentWordStart = start;
        currentWordEnd = findWordSpace(currentPosition);
    }

    private void recordMissingCharacter(int position, TypingUpdate update) {
        errorTracker.record(ErrorType.MISSING_CHARACTER, position, targetText.charAt(position), null);
        currentWordHasErrors = true;
    }

    private void recordWrongCharacter(int position, char typCharacter, TypingUpdate update) {
        errorTracker.record(ErrorType.WRONG_CHARACTER, position, targetText.charAt(position), typCharacter);

        update.addIncorrect(position);

        currentWordHasErrors = true;
    }
    

    private void commitCorrect(int position, TypingUpdate update) {
        activeCorrectPositions.add(position);
        update.addCorrect(position);
    }


    private void recordMissingSpaces(TypingUpdate update) {
        while (currentPosition < targetText.length() && Character.isWhitespace(targetText.charAt(currentPosition))) {
            errorTracker.record(ErrorType.MISSING_SPACE, currentPosition, targetText.charAt(currentPosition), null);

            update.addIncorrect(currentPosition);

            currentWordHasErrors = true;
            currentPosition++;
        }

        currentWordStart = currentPosition;

        currentWordEnd = findWordSpace(currentPosition);
    }

    private boolean tryResynch(char typCharacter) {
        /*
         * No previous char to cause offset.
         */
        if (resynch == null) {
            return false;
        }

        /*
         * New key does not match what previous expected.
         * No resynch needed.
         */
        if (typCharacter != resynch.getExpectedCharacter()) {
            resynch = null;
            return false;
        }

        /*
         * Repeated letter protection.
         */
        if (currentPosition < targetText.length() && typCharacter == targetText.charAt(currentPosition)) {
            resynch = null;
            return false;
        }

        /*
         * Character matches what the previous was expected to be,
         * but not what the current position expects.
         * This key is used to restore alignment.
         * Previous error stays red,
         * WRONG_CHARACTER is recorded, current position does not move
         */
        resynch = null;
        return true;
    }

    private void processCharacter(char typCharacter, TypingUpdate update) {

        if (currentPosition >= targetText.length()) {
            return;
        }

        /*
         * Target expects a space, but user continued typing.
         */
        if (Character.isWhitespace(targetText.charAt(currentPosition))) {

            recordMissingSpaces(update);

            if (currentPosition >= targetText.length()) {
                return;
            }
        }

        char expectedCharacter = targetText.charAt(currentPosition);

        /*
         * CASE 1: exact match.
         */
        if (typCharacter == expectedCharacter) {

            commitCorrect(currentPosition, update);

            currentPosition++;

            resynch = null;

            return;
        }

        /*
         * CASE 2: wrong character.
         */
        int wrongPosition = currentPosition;
        recordWrongCharacter(wrongPosition, typCharacter, update);

        resynch = new Resynch(wrongPosition, expectedCharacter);
        currentWordHasErrors = true;
        currentPosition++;

    }

    /**
     * Handled typed character that isn't space or backspace.
     *
     * @param typCharacter the character that was input through the keyboard
     * @return typingUpdate object
     */
    public TypingUpdate handleTypedCharacter(char typCharacter) {

        rawWpm++;

        if (typCharacter == ' ') {
            return handleSpace();
        }

        TypingUpdate update = new TypingUpdate();
        spaceJustHandled = false;
        hasTypedInCurrentWord = true;

        if (tryResynch(typCharacter)) {
            return update;
        }

        processCharacter(typCharacter, update);

        return update;
    }

    /**
     * Handles backspace.
     *
     * @return typing update object
     */
    public TypingUpdate handleBackspace() {
        TypingUpdate update = new TypingUpdate();

        resynch = null;

        if (currentPosition <= 0 || currentPosition <= lockedBoundary) {
            update.setIgnored(true);
            return update;
        }

        int oldPosition = currentPosition;

        if (currentPosition == currentWordStart && lastActivePosition < currentPosition && lastActivePosition >= lockedBoundary) {
            currentPosition = lastActivePosition;
        } else {
            currentPosition--;
        }

        for (int position = currentPosition; position < oldPosition; position++) {
            activeCorrectPositions.remove(position);
            update.addReset(position);
        }

        errorTracker.markCorrectedInRange(currentPosition, oldPosition);

        refreshCurrentWordBounds();

        currentWordHasErrors = errorTracker.getErrors().stream().anyMatch(error -> !error.isCorrected() && error.getPosition() >= currentWordStart
        && error.getPosition() < currentWordEnd);

        hasTypedInCurrentWord = activeCorrectPositions.stream().anyMatch(position -> position >= currentWordStart && position < currentWordEnd) || currentWordHasErrors;

        spaceJustHandled = false;
        return update;
    }

    /**
     * Handles space.
     *
     * @return update object
     */
    public TypingUpdate handleSpace() {
        TypingUpdate update = new TypingUpdate();
        if (spaceJustHandled) {
            update.setIgnored(true);
            return update;
        }

        resynch = null;

        int oldWordEnd = findWordSpace(currentPosition);
        currentWordEnd = oldWordEnd;

        boolean wordCompletedCorrectly = currentPosition == oldWordEnd && !currentWordHasErrors && hasTypedInCurrentWord;

        lastActivePosition = currentPosition;

        for (int position = currentPosition; position < oldWordEnd; position++) {
            recordMissingCharacter(position, update);
        }

        if (oldWordEnd < targetText.length()) {
            commitCorrect(oldWordEnd, update);
        }

        int nextPosition;
        /*
         * Move to the character after the current word's whitespace.
         */
        if (currentWordEnd < targetText.length()) {
            nextPosition = currentWordEnd + 1;
        } else {
            nextPosition = targetText.length();
        }

        /*
         * Skip additional whitespace if necessary.
         */
        while (nextPosition < targetText.length() && Character.isWhitespace(targetText.charAt(nextPosition))) {
            nextPosition++;
        }

        if (wordCompletedCorrectly) {
            lockedBoundary = nextPosition;
        }
    

        currentPosition = nextPosition;
        currentWordStart = currentPosition;
        currentWordHasErrors = false;
        hasTypedInCurrentWord = false;

        /*
         * Find the end of the new word.
         */
        currentWordEnd = findWordSpace(currentPosition);

        spaceJustHandled = true;

        /*
         * Return the old word ending so the panel knows
         * which characters were skipped.
         */
        return update;
    }


    /**
     * Calculates accuracy.
     *
     * @return accuracy
     */
    public int calculateAccuracy() {
        long correct = activeCorrectPositions.size();
        long activeErrors = errorTracker.getActiveErrorCount();

        long total = correct + activeErrors;

        if (total == 0) {
            return 100;
        }

        return (int) Math.round(correct * 100.0 / total);
    }
    
    /**
     * Calculates words per minute as correct / 5 / minutes.
     *
     * @param elapsedSeconds time passed since session start
     * @return words per minute
     */
    public int calculateWPM(int elapsedSeconds) {
        if (elapsedSeconds <= 0) {
            return 0;
        }

        double minutes = elapsedSeconds / 60.0;

        wpm = (int) ((activeCorrectPositions.size() / 5.0) / minutes);

        return wpm;
    }

    /**
     * Calculates the raw words per minute of the user.
     *
     * @param elapsedSeconds time passed since session starts
     * @return raw words per minute
     */
    public int calculateRawWPM(int elapsedSeconds) {
        if (elapsedSeconds <= 0) {
            return 0;
        }
        double minutes = elapsedSeconds / 60.0;

        return (int) ((rawWpm / 5.0) / minutes);
    }

    /**
     * Getter for currentPosition.
     *
     * @return current position
     */
    public int getCurrentPosition() {
        return currentPosition;
    }

    /**
     * Getter for word end.
     *
     * @return current word end
     */
    public int getCurrentWordEnd() {
        return currentWordEnd;
    }

    /**
     * Getter for target text.
     *
     * @return target text
     */
    public String getTargetText() {
        return targetText;
    }

    /**
     * Checks if position is more than target Text.
     *
     * @return isFinished true or false
     */
    public boolean isFinished() {
        return currentPosition >= targetText.length();
    }

    /**
     * Getter for error tracker.
     *
     * @return errorTracker
     */
    public ErrorTracker getErrorTracker() {
        return errorTracker;
    }
}