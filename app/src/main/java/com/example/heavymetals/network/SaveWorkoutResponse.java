package com.example.heavymetals.network;

public class SaveWorkoutResponse {
    private boolean success;
    private String message;

    // Constructor with parameters
    public SaveWorkoutResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Default constructor
    public SaveWorkoutResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
