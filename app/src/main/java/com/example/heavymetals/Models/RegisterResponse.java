package com.example.heavymetals.Models;

public class RegisterResponse {
    private int success;
    private String message;
    private String user_id;  // Add this field to match your API response

    // Getters and setters for the fields
    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
}
