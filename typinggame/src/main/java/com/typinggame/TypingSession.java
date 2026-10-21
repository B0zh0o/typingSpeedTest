package com.typinggame;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    private static final int LOOKAHEAD_LIMIT = 4;
    private static final int PENDING_LIMIT = 3;
    private final ErrorTracker errorTracker;
    private final Set<Integer> activeCorrectPositions;
    private final List<Character> pendingInput;
    private int pendingTargetPosition;

    public List<TypingError> errorsList;

    public TypingSession(String targetText) {
        this.targetText = targetText;
        this.currentPosition = 0;
        this.currentWordEnd = findWordSpace(currentPosition); 
        this.spaceJustHandled = false;
        this.currentWordStart = 0;
        this.lockedBoundary = 0;
        this.lastActivePosition = 0;
        this.wpm = 0;
        this.errorTracker = new ErrorTracker();
        this.activeCorrectPositions = new HashSet<>();
        this.pendingInput = new ArrayList<>();
        this.pendingTargetPosition = -1;
        this.errorsList = List.copyOf(errorTracker.errors);
    }

    private int findWordSpace(int position) {
        int spacePosition = position;
        while((spacePosition < targetText.length()) &&  !Character.isWhitespace(targetText.charAt(spacePosition))) {
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

    private void recordExtraCharacter(int position, char typCharacter) {
        Character expected = null; 
        if (position < targetText.length()) {
            expected = targetText.charAt(position);
        }

        errorTracker.record(ErrorType.EXTRA_CHARACTER, position, expected, typCharacter);

        currentWordHasErrors = true;
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

    private int findLookaheadMatch(char typCharacter) {
        int end = Math.min(targetText.length(), currentPosition + LOOKAHEAD_LIMIT + 1);

        for(int position = currentPosition + 1; position < end; position++) {
            char targetCharacter = targetText.charAt(position);

            if (Character.isWhitespace(targetCharacter)) {
                break;
            }

            if (targetCharacter == typCharacter) {
                return position;
            }
        }

        return -1;
    }

    private void resolvePendingFallback(TypingUpdate update) {
        int targetPosition = pendingTargetPosition;
        char firstCharacter = pendingInput.get(0);

        List<Character> remaining = new ArrayList<>(pendingInput.subList(1, pendingInput.size()));

        pendingInput.clear();
        pendingTargetPosition = -1;

        recordWrongCharacter(targetPosition, firstCharacter, update);
        currentPosition = targetPosition + 1;
        currentWordHasErrors = true;

        for(char character : remaining) {
            if (pendingInput.isEmpty()) {
                processCharacter(character, update);
            } else {
                resolvePendingWith(character, update);
            }
        }
    }

    private void resolvePendingWith(char typCharacter, TypingUpdate update) {
        int targetPosition = pendingTargetPosition;

        char expectedCharacter = targetText.charAt(targetPosition);

        /*
        * Extra character: 
        * target = cat
        * user typed = caxt
         */
        if (typCharacter == expectedCharacter) {
            for(char extra : pendingInput) {
                recordExtraCharacter(targetPosition, extra);
            }

            pendingInput.clear();
            pendingTargetPosition = -1;
            update.addReset(targetPosition);
            
            commitCorrect(targetPosition, update);
            currentPosition = targetPosition + 1;
            currentWordHasErrors = true;
            
            return;
        }

        /*
        * Wrong
        * target = cat
        * typed = cot
         */
        if(pendingInput.size() == 1 && targetPosition + 1 < targetText.length() 
            && !Character.isWhitespace(targetText.charAt(targetPosition + 1)) 
            && typCharacter == targetText.charAt(targetPosition + 1)) {
                char wrongCharacter = pendingInput.get(0);
                recordWrongCharacter(targetPosition, wrongCharacter, update);

                pendingInput.clear();
                pendingTargetPosition = -1;
                commitCorrect(targetPosition + 1, update);
                currentPosition = targetPosition + 2;
                currentWordHasErrors = true;
                return;
            }
        
        /*
        * Unsure
         */
        pendingInput.add(typCharacter);
        if (pendingInput.size() >= PENDING_LIMIT) {
            resolvePendingFallback(update);
        }
    }

    private void processCharacter(char typCharacter,TypingUpdate update) {

        if (currentPosition >= targetText.length()) {
            recordExtraCharacter(currentPosition, typCharacter);
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

        char expectedCharacter =
            targetText.charAt(currentPosition);

        /*
        * CASE 1: exact match.
        */
        if (typCharacter == expectedCharacter) {

            commitCorrect(currentPosition, update);

            currentPosition++;

            return;
        }

        /*
        * CASE 2: maybe they skipped target characters.
        */
        int matchPosition = findLookaheadMatch(typCharacter);

        if (matchPosition != -1) {

            for (int position = currentPosition; position < matchPosition; position++) {

                recordMissingCharacter(position, update);
            }

            commitCorrect(matchPosition, update);

            currentPosition = matchPosition + 1;

            currentWordHasErrors = true;

            return;
        }

        /*
        * CASE 3:
        * We don't know yet if this character is
        * WRONG or EXTRA.
        */

        update.addIncorrect(currentPosition);
        pendingTargetPosition = currentPosition;

        pendingInput.add(typCharacter);
    }
    public TypingUpdate handleTypedCharacter(char typCharacter) {



        if (typCharacter == ' ') {
            return handleSpace();
        }

        TypingUpdate update = new TypingUpdate();
        spaceJustHandled = false;
        hasTypedInCurrentWord = true;

        if (!pendingInput.isEmpty()) {
            resolvePendingWith(typCharacter, update);
        } else {
            processCharacter(typCharacter, update);
        }

        return update;
    }


    public TypingUpdate handleBackspace() {
        TypingUpdate update = new TypingUpdate();

        /*
         * CASE 1:
         * There is an unresolved character in the pending buffer
         *
         * target: cat
         * typed:  cax
         *
         * x has not yet been classified as WRONG_CHARACTER or EXTRA_CHARACTER.
         * Backspace only removes x from buffer.
         */
        if (!pendingInput.isEmpty()) {

            pendingInput.remove(pendingInput.size() - 1);

            if (pendingInput.isEmpty()) {
                pendingTargetPosition = -1;
            }

            update.setIgnored(true);

            return update;
        }

        /*
         * CASE 2:
         * We are already at the beginning of the text
         * or at a boundary we are not allowed to cross.
         */
        if (currentPosition <= 0 || currentPosition <= lockedBoundary) {

            update.setIgnored(true);
            return update;
        }

        int oldPosition = currentPosition;

        /*
         * CASE 3:
         * We previously skipped part of a word by pressing Space
         *
         * Example:
         *
         * target: Hello World
         * typed:  He[SPACE]
         *
         * lastActivePosition = 2
         * currentPosition = 6
         *
         * Pressing Backspace should jump back to position 2
         * instead of only moving from 6 to 5.
         */
        if (currentPosition == currentWordStart && lastActivePosition < currentPosition
             && lastActivePosition >= lockedBoundary) {

            currentPosition = lastActivePosition;

        } else {

            /*
             * Normal Backspace:
             */
            currentPosition--;
        }

        /*
         * Remove all current corrects that we moved backwards from
         *
         * Also tell TypingPanel that these characters
         * should return to their normal faded appearance
         */
        for (int position = currentPosition; position < oldPosition; position++) {

            activeCorrectPositions.remove(position);

            update.addReset(position);
        }

        /*
         * Do NOT delete old errors.
         *
         * Instead mark them as corrected so that they stop effecting live accuracy, and exist in the final error report
         */
        errorTracker.markCorrectedInRange(currentPosition, oldPosition);

        /*
         * recalculate the start/end of the current word.
         */
        refreshCurrentWordBounds();

        /*
         * Recalculate whether the current word still contains any uncorrected errors.
         */
        currentWordHasErrors =
            errorTracker.getErrors().stream().anyMatch(error ->
                !error.isCorrected()
                && error.getPosition() >= currentWordStart
                && error.getPosition() < currentWordEnd);

        /*
         * Work out whether anything is still actively typed
         * inside the current word.
         */
        hasTypedInCurrentWord =
            activeCorrectPositions.stream().anyMatch(position ->
                position >= currentWordStart
                && position < currentWordEnd) || currentWordHasErrors;

        /*
         * The user has interacted again, so a previous Space
         * should no longer block another Space press.
         */
        spaceJustHandled = false;

        return update;  
    }


    private void flushBuffer(TypingUpdate update) {
        if(pendingInput.isEmpty()) {
            return;
        }

        int position = pendingTargetPosition;

        char first = pendingInput.get(0);

        recordWrongCharacter(position, first, update);

        for (int i = 1; i < pendingInput.size(); i++) {
            recordExtraCharacter(position, pendingInput.get(i));
        }

        currentPosition = position + 1;
        pendingInput.clear();
        pendingTargetPosition = -1;
    }

    public TypingUpdate  handleSpace() {
        TypingUpdate update = new TypingUpdate();
        
        if (spaceJustHandled) {
            update.setIgnored(true);
            return update;
        }


        flushBuffer(update);

        int oldWordEnd = findWordSpace(currentPosition);
        currentWordEnd = oldWordEnd;

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



    public int calculateAccuracy() {
        long correct = activeCorrectPositions.size();
        long activeErrors = errorTracker.getActiveErrorCount();

        long total = correct + activeErrors;

        if(total == 0) {
            return 100;
        }

        return (int) Math.round(correct * 100.0 / total);
    }
    
    public int calculateWPM(int elapsedSeconds) {
        if (elapsedSeconds <= 0) {
            return 0;
        }

        double minutes = elapsedSeconds / 60.0;

        wpm = (int)((activeCorrectPositions.size() / 5.0) / minutes);

        return wpm;
    } 


    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getCurrentWordEnd() {
        return currentWordEnd;
    }

    public String getTargetText() {
        return targetText;
    }

    public boolean isFinished() {
        return currentPosition >= targetText.length();
    }

    public ErrorTracker getErrorTracker() {
        return errorTracker;
    }
}


/*
    public void timingSessionStarted() {
        typingTimer.startTimer();
        
        while (!(typingTimer.timeLeft <= 0)) {
            if ((int) lastUserInput >= 65 && (int) lastUserInput <= 122) { //LETTERS
                wordsArray[currentWordIndex].lastTypedIndex += 1;
                currentLetterIndex += 1;
           } else if ((int) lastUserInput == 32) { //SPACE
                currentWordIndex += 1;
                currentLetterIndex = 0;
           } else if ((int) lastUserInput == 8) { //BACK SPACE
                currentWordIndex -= 1;
                currentLetterIndex = wordsArray[currentWordIndex].lastTypedIndex;
           }
        }
    }

    //METHODS-----------------------------------------------
    public String[] splitText(String unsplitString) {
        String[] splitString = unsplitString.split("//s");
        return splitString;
    }

    public Word[] convertIntoWordsArray(String[] splitText) {
        Word[] wordsArray = new Word[splitText.length];
        for (int i = 0; i < splitText.length; i++) {
            wordsArray[i] = new Word(splitText[i]);
        }
        return wordsArray;
    }
}  
*/