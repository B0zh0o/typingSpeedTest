package com.typinggame;

import java.util.Stack;


public class TypingSession {
    
    private String targetText; 
    private int currentPosition;
    private int currentWordEnd;
    private boolean spaceJustHandled;

    private int currentWordStart;
    private int lockedBoundary = 0;
    private boolean currentWordHasErrors = false;
    private boolean hasTypedInCurrentWord = false;
    private int lastActivePosition = 0;

    public TypingSession(String targetText) {
        this.targetText = targetText;
        this.currentPosition = 0;
        this.currentWordEnd = findWordSpace(currentPosition); 
        this.spaceJustHandled = false;
        this.currentWordStart = 0;
        this.lockedBoundary = 0;
        this.lastActivePosition = 0;
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

    public boolean handleCharacter(char typedCharacter) {
        if (currentPosition >= targetText.length()) {
            return false;
        }

        hasTypedInCurrentWord = true;

        spaceJustHandled = false;

        char expectedCharacter = targetText.charAt(currentPosition);

        boolean correct = (typedCharacter == expectedCharacter);

        if (!correct) {
            currentWordHasErrors = true;
        }

        if (Character.isWhitespace(expectedCharacter)) {
            currentWordHasErrors = true;
        }

        currentPosition++;

        return correct;
    }

    public int handleBackspace() {

        if (currentPosition <= lockedBoundary) {
            refreshCurrentWordBounds();
            return currentPosition;
        }

       if (currentPosition <= currentWordStart) {
            currentPosition = lastActivePosition;
        } else {
            currentPosition--;
        }

        if (currentPosition < lockedBoundary) {
            currentPosition = lockedBoundary;
        }

        refreshCurrentWordBounds();
        spaceJustHandled = false;

        return currentPosition;
    }

    public int handleSpace() {
        if(spaceJustHandled) {
            return -1;
        }

        boolean reachedEndOfWord = (currentPosition == currentWordEnd);
        boolean wordIsCorrect = reachedEndOfWord && !currentWordHasErrors && hasTypedInCurrentWord;

        int oldWordEnd = currentWordEnd;

        lastActivePosition = currentPosition;

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
    
        if (wordIsCorrect) {
            lockedBoundary = nextPosition;
            lastActivePosition = nextPosition;
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
        return oldWordEnd;
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