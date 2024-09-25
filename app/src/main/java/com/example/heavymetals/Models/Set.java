package com.example.heavymetals.Models;

public class Set {
    private int setNumber;
    private int reps;
    private boolean done;

    public Set(int setNumber, int reps) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.done = false;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public int getReps() {
        return reps;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
