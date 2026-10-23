package com.typinggame.Logic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * TypingUpdate stores the positions that become correct, incorrect,
 * reset after any type of typing action.
 */
public class TypingUpdate {

    private final List<Integer> correctPositions = new ArrayList<>();
    private final List<Integer> incorrectPositions = new ArrayList<>();
    private final List<Integer> resetPositions = new ArrayList<>();

    private boolean ignored;

    /**
     * Adds position to correct List.
     *
     * @param position in text
     */
    public void addCorrect(int position) {
        correctPositions.add(position);
    }

    /**
     * Adds position to incorrect List.
     *
     * @param position in text
     */
    public void addIncorrect(int position) {
        incorrectPositions.add(position);
    }

    /**
     * Adds position to reset List.
     *
     * @param position in text
     */
    public void addReset(int position) {
        resetPositions.add(position);
    }

    /**
     * Getter for correctPositions list.
     *
     * @return copy of correctPositions list
     */
    public List<Integer> getCorrectPositions() {
        return List.copyOf(correctPositions);
    }
    
    /**
     * Getter for resetPositions list.
     *
     * @return copy of resetPositions list
     */
    public List<Integer> getResetPositions() {
        return List.copyOf(resetPositions);
    }

    /**
     * Getter for incorrectPositions list.
     
     * @return copy of incorrectPositions list
     */
    public List<Integer> getIncorrectPositions() {
        return List.copyOf(incorrectPositions);
    }

    /**
     * Returns whether the update should be ignored.
     *
     * @return ignored
     */
    public boolean isIgnored() {
        return ignored;
    }

    /**
     * Setter for ignored.
     *
     * @param ignored whether the update is ignored
     */
    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
