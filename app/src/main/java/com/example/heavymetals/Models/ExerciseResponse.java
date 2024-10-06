package com.example.heavymetals.Models;

import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseResponse {
    private boolean success;
    private String message;
    private List<AdaptersExercise> exercises;

    @SerializedName("exercise_count")  // Map this field from JSON response
    private int exerciseCount;  // Add the new field for exercise count

    // Getter for success
    public boolean isSuccess() {
        return success;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Getter for exercises list
    public List<AdaptersExercise> getExercises() {
        return exercises;
    }

    // Getter for exercise count
    public int getExerciseCount() {
        return exerciseCount;
    }
}
