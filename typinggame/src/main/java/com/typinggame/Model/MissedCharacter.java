package com.typinggame.model;

//* A model needed for History */

public class MissedCharacter {
    public char character;
    public long count;

    public MissedCharacter(char character, long count) {
        this.character = character;
        this.count = count;
    }
}
