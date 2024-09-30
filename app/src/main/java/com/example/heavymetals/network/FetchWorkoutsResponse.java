package com.example.heavymetals.network;

import com.example.heavymetals.Models.Adapters.Workout;

import java.util.List;

public class FetchWorkoutsResponse {
    private boolean success;
    private List<Workout> workouts;

    public boolean isSuccess() {
        return success;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }
}
