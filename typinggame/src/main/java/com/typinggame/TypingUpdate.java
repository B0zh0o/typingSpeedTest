package com.typinggame;

import java.util.ArrayList;
import java.util.List;

public class TypingUpdate {

    private final List <Integer> correctPositions = new ArrayList<>();
    private final List <Integer> incorrectPositions = new ArrayList<>();
    private final List <Integer> resetPositions = new ArrayList<>();

    private boolean ignored;

    public void addCorrect(int position) {
        correctPositions.add(position);
    }

    public void addIncorrect(int position) {
        incorrectPositions.add(position);
    }

    public void addReset(int position) {
        resetPositions.add(position);
    }

    public List<Integer> getCorrectPositions() {
        return List.copyOf(correctPositions);
    }
    
    public List<Integer> getResetPositions() {
        return List.copyOf(resetPositions);
    }

    
    public List<Integer> getIncorrectPositions() {
        return List.copyOf(incorrectPositions);
    }

    public boolean isIgnored() {
        return ignored;
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
