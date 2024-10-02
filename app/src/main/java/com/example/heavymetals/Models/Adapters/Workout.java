package com.example.heavymetals.Models.Adapters;

import java.io.Serializable;
import java.util.List;

public class Workout implements Serializable {
    private int id;  // New field to hold the workout ID
    private String title;
    private List<AdaptersExercise> exercises;

    // Constructor accepting an int, String, and List<AdaptersExercise>
    public Workout(int id, String title, List<AdaptersExercise> exercises) {
        this.id = id;
        this.title = title;
        this.exercises = exercises;
    }

    // Existing constructor for backwards compatibility
    public Workout(String title, List<AdaptersExercise> exercises) {
        this.title = title;
        this.exercises = exercises;
    }

    // Getter and setter for id
    public int getWorkoutId() {
        return id;
    }

    public void setWorkoutId(int id) {
        this.id = id;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for exercises
    public List<AdaptersExercise> getExercises() {
        return exercises;
    }

    // Setter for exercises
    public void setExercises(List<AdaptersExercise> exercises) {
        this.exercises = exercises;
    }
}
