package com.typinggame;

//import java.awt.event.StyleConstants;
//import java.awt.event.KeyEvent;


public class TypingSession {
    
    TypingTimer typingTimer = new TypingTimer();
    String targetText;
    Word[] wordsArray = convertIntoWordsArray(splitText(targetText));

    int currentLetterIndex = 0;
    int currentWordIndex = 0; 
    char lastUserInput;

    public TypingSession(String targetText) {
        this.targetText = targetText;
    }

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
