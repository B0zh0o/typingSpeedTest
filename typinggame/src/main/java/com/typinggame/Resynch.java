package com.typinggame;

public class Resynch {

    private final int position;
    private final char expectedCharacter;

    public Resynch(
            int position,
            char expectedCharacter) {

        this.position = position;
        this.expectedCharacter = expectedCharacter;
    }

    public int getPosition() {
        return position;
    }

    public char getExpectedCharacter() {
        return expectedCharacter;
    }
}