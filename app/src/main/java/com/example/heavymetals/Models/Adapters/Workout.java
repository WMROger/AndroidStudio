package com.example.heavymetals.Models.Adapters;

import java.io.Serializable;
import java.util.List;

public class Workout implements Serializable {
    private String title;  // Name of the workout (previously called "name")
    private List<Exercise> exercises;  // List of Exercise objects

    // Constructor
    public Workout(String title, List<Exercise> exercises) {
        this.title = title;
        this.exercises = exercises;
    }

    // Getter for title (previously "name")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for exercise list
    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    // This method calculates the number of exercises (exerciseCount)
    public int getExerciseCount() {
        return exercises != null ? exercises.size() : 0;
    }
}
