package com.typinggame;




public class TypingSession {
    
    private String targetText; 
    private int currentPosition;
    private int currentWordEnd;
    private boolean spaceJustHandled; 

    public TypingSession(String targetText) {
        this.targetText = targetText;
        this.currentPosition = 0;
        this.currentWordEnd = findWordSpace(currentPosition); 
        this.spaceJustHandled = false;
    }

    private int findWordSpace(int position) {
        int spacePosition = position;
        while((spacePosition < targetText.length()) &&  !Character.isWhitespace(targetText.charAt(spacePosition))) {
            spacePosition++;
            }
        return spacePosition;
    } 

        public boolean handleCharacter(char typedCharacter) {
        if (currentPosition >= targetText.length()) {
            return false;
        }

        spaceJustHandled = false; 

        char expectedCharacter = targetText.charAt(currentPosition);

        boolean correct = typedCharacter == expectedCharacter;

        currentPosition++;

        return correct;
    }

        public int handleBackspace() {

        if (currentPosition <= 0) {
            return -1;
        }

        currentPosition--;

        currentWordEnd = findWordSpace(currentPosition);

        spaceJustHandled = false;

        return currentPosition;
    }

    public int handleSpace() {


        if(spaceJustHandled) {
            return -1;
        }

        int oldWordEnd = currentWordEnd;

        /*
         * Move to the character after the current word's whitespace.
         */
        if (currentWordEnd < targetText.length()) {
            currentPosition = currentWordEnd + 1;
        } else {
            currentPosition = targetText.length();
        }

        /*
         * Skip additional whitespace if necessary.
         */
        while (currentPosition < targetText.length() && Character.isWhitespace(targetText.charAt(currentPosition))) {

            currentPosition++;
        }

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