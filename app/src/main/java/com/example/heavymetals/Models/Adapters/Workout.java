package com.example.heavymetals.Models.Adapters;

import java.io.Serializable;
import java.util.List;

public class Workout implements Serializable {
    private String title;
    private int exerciseCount;
    private List<Exercise> exercises;  // List of Exercise objects

    // Constructor
    public Workout(String title, int exerciseCount, List<Exercise> exercises) {
        this.title = title;
        this.exerciseCount = exerciseCount;
        this.exercises = exercises;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
