package com.example.heavymetals.network;

public class UserIdRequest {
    private String user_id;

    public UserIdRequest(String user_id) {
        this.user_id = user_id;
    }

    // Getter and setter
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
