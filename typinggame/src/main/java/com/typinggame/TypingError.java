package com.typinggame;

public class TypingError {
    private final ErrorType type;
    private final int position;

    private final Character expCharacter;
    private final Character typCharacter;

    private boolean corrected;


    public TypingError(ErrorType type, int position, Character expCharacter, Character typCharacter) { 
        this.type = type;
        this.position = position;
        this.expCharacter = expCharacter;
        this.typCharacter = typCharacter;
        this.corrected = false;
    }

    public ErrorType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public Character getExpCharacter() {
        return expCharacter;
    }

    public Character getCharacter() {
        return typCharacter;
    }

    public boolean isCorrected() {
        return corrected;
    }

    public void setCorrected(boolean corrected) {
        this.corrected = corrected;
    }

    @Override
    public String toString() { 
    
        return "TypingError{" +
        "type=" + type +
        ", position=" + position +
        ", expectedCharacter=" + expCharacter +
        ", typedCharacter=" + typCharacter +
        ", corrected=" + corrected +
        '}';
    }
}
