package com.example.heavymetals.network;

import com.example.heavymetals.Models.Adapters.WorkoutResponse;
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.Models.LoginResponse;
import com.example.heavymetals.Models.RegisterResponse;
import com.example.heavymetals.Models.ResetResponse;
import com.example.heavymetals.Models.VerifyCodeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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


    @FormUrlEncoded
    @POST("forgetpass/verify_code.php")  // The path to your verify_code.php
    Call<VerifyCodeResponse> verifyCode(
            @Field("reset_code") String resetCode  // Send the reset code as a form field
    );


    @FormUrlEncoded
    @POST("forgetpass/reset_password.php")
    Call<Void> resetPassword(
            @Field("token") String token,  // Use "token" here instead of "user_id"
            @Field("new_password") String newPassword,
            @Field("confirm_password") String confirmPassword,
            @Field("code") String code  // Include the code in the request
    );

    @POST("HeavyMetals/workout_save/update_exercises.php")
    @FormUrlEncoded
    Call<Void> updateExercises(
            @Field("session_token") String sessionToken,
            @Field("workout_id") int workoutId,
            @Field("exercises") String exercisesJson  // JSON array of exercises
    );

    // New method for saving workouts
    @POST("HeavyMetals/workout_save/save_workouts.php")  // Endpoint for saving workouts
    Call<SaveWorkoutResponse> saveWorkouts(
            @Body WorkoutResponse workoutResponse  // Sending the WorkoutRequest object
    );

    // New method for fetching workouts
    @POST("HeavyMetals/workout_save/get_workout.php")  // Endpoint for fetching workouts
    Call<FetchWorkoutsResponse> fetchWorkouts(
            @Body UserIdRequest userIdRequest  // Sending the UserIdRequest object
    );
    @GET("HeavyMetals/workout_save/get_exercise.php")
    Call<ExerciseResponse> getExercises(
            @Query("session_token") String sessionToken,
            @Query("workout_id") int workoutId
    );
    @FormUrlEncoded
    @POST("HeavyMetals/workout_save/delete_workout.php")  // Endpoint for deleting workouts
    Call<Void> deleteWorkout(
            @Field("workout_id") int workoutId,  // Workout ID to be deleted
            @Field("session_token") String sessionToken  // Session token for authentication
    );
}
