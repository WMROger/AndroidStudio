package com.example.heavymetals.Models.Adapters;

import java.io.Serializable;

public class Exercise implements Serializable {
    private String name;
    private int sets;
    private int reps;
    private boolean isDone;

    // Constructor
    public Exercise(String name, int sets, int reps, boolean isDone) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.isDone = isDone;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
