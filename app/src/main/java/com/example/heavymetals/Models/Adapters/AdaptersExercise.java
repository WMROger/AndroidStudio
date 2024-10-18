package com.example.heavymetals.Models.Adapters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdaptersExercise implements Serializable {
    @SerializedName("exercise_name")
    private String name;
    private int sets;
    private int reps;
    private boolean isDone;  // Tracks if the exercise is completed

    // Constructor
    public AdaptersExercise(String name, int sets, int reps, boolean isDone) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.isDone = isDone;
    }

    // Alternative constructor without isDone for backwards compatibility
    public AdaptersExercise(String name, int sets, int reps) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.isDone = false;  // Default value for isDone
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
