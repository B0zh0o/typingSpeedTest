package com.typinggame.Model;

/**
 * TypingError class creates a type of error error.
 */
public class TypingError {
    private final ErrorType type;
    private final int position;
    private final Character expCharacter;
    private final Character typCharacter;
    private boolean corrected;

    /**
     * Creates a type of error object.
     * 
     * @param type type of error
     * @param position position of error
     * @param expCharacter expected character
     * @param typCharacter actual character typed
     */
    public TypingError(ErrorType type, int position, Character expCharacter, Character typCharacter) { 
        this.type = type;
        this.position = position;
        this.expCharacter = expCharacter;
        this.typCharacter = typCharacter;
        this.corrected = false;
    }

    /**
     * Getter for type field.
     * 
     * @return type of error
     */
    public ErrorType getType() {
        return type;
    }
    
    /**
     * Getter for position field.
     * 
     * @return position of error
     */
    public int getPosition() {
        return position;
    }

    /**
     * Getter for expected character field.
     * 
     * @return expected character
     */
    public Character getExpCharacter() {
        return expCharacter;
    }

    /**
     * Getter for typed character field.
     * 
     * @return actual typed character
     */
    public Character getCharacter() {
        return typCharacter;
    }

    /**
     * Getter for corrected field.
     * 
     * @return if the error is corrected or not
     */
    public boolean isCorrected() {
        return corrected;
    }

    /**
     * Setter for corrected.
     * 
     * @param corrected sets corrected to true or false
     */
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
