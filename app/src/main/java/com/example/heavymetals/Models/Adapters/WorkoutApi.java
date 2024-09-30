package com.example.heavymetals.Models.Adapters;

import com.example.heavymetals.network.SaveWorkoutResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WorkoutApi {

    // Fetch workouts for the user
    @GET("workout_save/get_workout.php")
    Call<WorkoutResponse> fetchWorkouts(@Query("user_id") String userId);

    // Save workouts for the user
    @POST("workout_save/save_workouts.php")
    Call<SaveWorkoutResponse> saveWorkouts(@Body WorkoutResponse workoutResponse);
}
