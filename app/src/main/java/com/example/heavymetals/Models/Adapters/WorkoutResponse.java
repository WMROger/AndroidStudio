package com.example.heavymetals.Models.Adapters;

import java.util.List;

public class WorkoutResponse {
    private boolean success;  // Success flag from the server
    private String email;     // User email
    private List<Workout> workouts;  // List of workout objects

    // Constructor
    public WorkoutResponse(boolean success, String email, List<Workout> workouts) {
        this.success = success;
        this.email = email;
        this.workouts = workouts;
    }

    // Getter for success
    public boolean isSuccess() {
        return success;
    }

    // Getters and Setters for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and Setters for workouts
    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }
}
