package com.example.heavymetals.Models;

import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("exercises")
    private List<AdaptersExercise> exercises;

    public boolean isSuccess() {
        return success;
    }

    public List<AdaptersExercise> getExercises() {
        return exercises;
    }
}
