package com.example.heavymetals.Models;

import com.google.gson.annotations.SerializedName;

public class VerifyCodeResponse {

    @SerializedName("success")
    private int success;

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private String userId;  // Assuming user_id is a String; change it to int if needed

    // Getters
    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }
}
