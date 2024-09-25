package com.example.heavymetals.Models;

import java.io.Serializable;
import java.util.List;

public class Workout implements Serializable {
    private String title;
    private int exerciseCount;
    private List<String> exercises;  // List of exercises

    // Constructor
    public Workout(String title, int exerciseCount, List<String> exercises) {
        this.title = title;
        this.exerciseCount = exerciseCount;
        this.exercises = exercises;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public List<String> getExercises() {
        return exercises;
    }
}
