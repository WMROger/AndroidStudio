package com.example.heavymetals.Models.Adapters;

import com.example.heavymetals.Models.Exercise;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Workout implements Serializable {
    private int workoutId;  // ID of the workout
    private String title;   // Title of the workout
    private List<Exercise> exercises;  // List of Exercise objects (now using Exercise.java)

    // Constructor
    public Workout(int workoutId, String title, List<Exercise> exercises) {
        this.workoutId = workoutId;
        this.title = title;
        this.exercises = exercises;
    }

    // Getters and setters
    public int getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(int workoutId) {
        this.workoutId = workoutId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    // Method to get the count of exercises
    public int getExerciseCount() {
        return exercises != null ? exercises.size() : 0;
    }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workout workout = (Workout) o;
        return workoutId == workout.workoutId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(workoutId);
    }
}
