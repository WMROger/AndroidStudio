package com.example.heavymetals.Models.Adapters;

import com.example.heavymetals.network.SaveWorkoutResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WorkoutApi {
    // Define the GET request with user_id as a query parameter
    @GET("/HeavyMetals/workout_save/get_workout.php")
    Call<WorkoutResponse> getWorkouts(@Query("session_token") String userId);


    // Save workouts for the user
    @POST("/HeavyMetals/workout_save/add_workout.php")
    Call<SaveWorkoutResponse> saveWorkouts(@Body WorkoutResponse workoutResponse);

    // Delete workout API
    @FormUrlEncoded
    @POST("/HeavyMetals/workout_save/delete_workout.php")
    Call<Void> deleteWorkout(
            @Field("workout_id") int workoutId,
            @Field("session_token") String sessionToken
    );



}
