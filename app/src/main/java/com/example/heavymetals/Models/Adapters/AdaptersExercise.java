package com.example.heavymetals.Models.Adapters;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AdaptersExercise implements Serializable {
    @SerializedName("exercise_list_id") // This should match your JSON key
    private int id;  // Add id field to represent exercise_list_id
    @SerializedName("exercise_name")
    private String name;
    private int sets;
    private int reps;
    private boolean isDone;
    private String imageUrl;  // Add imageUrl to handle the image fetching

    // Constructor
    public AdaptersExercise(int id, String name, int sets, int reps, boolean isDone, String imageUrl) {
        this.id = id;  // Initialize id
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.isDone = isDone;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getId() {  // Add getter for id
        return id;
    }

    public void setId(int id) {  // Add setter for id
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
