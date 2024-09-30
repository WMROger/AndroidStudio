package com.example.heavymetals.network;

import com.example.heavymetals.Models.Adapters.WorkoutResponse;
import com.example.heavymetals.Models.LoginResponse;
import com.example.heavymetals.Models.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    // Existing login method
    @FormUrlEncoded
    @POST("login.php")  // Endpoint for login
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // Existing registration method
    @FormUrlEncoded
    @POST("register.php")  // Endpoint for registration
    Call<RegisterResponse> registerUser(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("password") String password
    );

    // New method for saving workouts
    @POST("workout_save/save_workouts.php")  // Endpoint for saving workouts
    Call<SaveWorkoutResponse> saveWorkouts(
            @Body WorkoutResponse workoutResponse  // Sending the WorkoutRequest object
    );

    // New method for fetching workouts
    @POST("workout_save/get_workouts.php")  // Endpoint for fetching workouts
    Call<FetchWorkoutsResponse> fetchWorkouts(
            @Body UserIdRequest userIdRequest  // Sending the UserIdRequest object
    );
}
