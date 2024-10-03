package com.example.heavymetals.Models.Adapters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Workout implements Serializable {
    @SerializedName("workout_id")  // Ensure this matches the server's workout ID field
    private int id;

    @SerializedName("workout_name")  // Maps to workout_name field from the server
    private String title;

    private List<AdaptersExercise> exercises;

    // Constructor
    public Workout(int id, String title, List<AdaptersExercise> exercises) {
        this.id = id;
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

    // Getter and setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and setter for exercises
    public List<AdaptersExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<AdaptersExercise> exercises) {
        this.exercises = exercises;
    }
}
