package com.example.heavymetals.Models.Adapters;

import java.io.Serializable;
import java.util.List;

public class Workout implements Serializable {
    private String title;  // Name of the workout (previously called "name")
    private List<AdaptersExercise> adaptersExercises;  // List of Exercise objects

    // Constructor
    public Workout(String title, List<AdaptersExercise> adaptersExercises) {
        this.title = title;
        this.adaptersExercises = adaptersExercises;
    }

    // Getter for title (previously "name")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for exercise list
    public List<AdaptersExercise> getExercises() {
        return adaptersExercises;
    }

    public void setExercises(List<AdaptersExercise> adaptersExercises) {
        this.adaptersExercises = adaptersExercises;
    }

    // This method calculates the number of exercises (exerciseCount)
    public int getExerciseCount() {
        return adaptersExercises != null ? adaptersExercises.size() : 0;
    }
}
