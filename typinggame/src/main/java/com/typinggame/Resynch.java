package com.typinggame;

public class Resynch {

    private final int position;
    private final char expectedCharacter;
    /**
     * Realigns the expected text and typed text.
     * 
     * @param position position of character
     * @param expectedCharacter the expected character in the text
     */
    public Resynch(int position, char expectedCharacter) {

        this.position = position;
        this.expectedCharacter = expectedCharacter;
    }
    /**
     * 
     * @return the stored position
     */
    public int getPosition() {
        return position;
    }
    /**
     * 
     * @return the expected character
     */
    public char getExpectedCharacter() {
        return expectedCharacter;
    }
}