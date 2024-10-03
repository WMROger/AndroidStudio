package com.example.heavymetals.Models;

import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseResponse {
    private boolean success;
    private String message;
    private List<AdaptersExercise> exercises;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<AdaptersExercise> getExercises() {
        return exercises;
    }
}
