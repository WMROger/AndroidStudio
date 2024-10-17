package com.example.heavymetals.Models.Adapters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdaptersExercise implements Serializable {
    @SerializedName("exercise_name")
    private String name;
    private int sets;
    private int reps;

    // Constructor
    public AdaptersExercise(String name, int sets, int reps, boolean isDone) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
    }

    // Alternative constructor without isDone for backwards compatibility
    public AdaptersExercise(String name, int sets, int reps) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
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


}
