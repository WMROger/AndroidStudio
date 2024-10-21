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

    // Login method
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // Registration method
    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> registerUser(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("password") String password
    );

    // Verify reset code
    @FormUrlEncoded
    @POST("forgetpass/verify_code.php")
    Call<VerifyCodeResponse> verifyCode(
            @Field("reset_code") String resetCode
    );

    // Reset password
    @FormUrlEncoded
    @POST("forgetpass/reset_password.php")
    Call<ResetResponse> resetPassword(
            @Field("user_id") String userId,
            @Field("new_password") String newPassword,
            @Field("confirm_password") String confirmPassword
    );

    // Update exercises method
    @POST("HeavyMetals/workout_save/update_exercise.php")
    @FormUrlEncoded
    Call<ExerciseResponse> updateExercises(
            @Field("session_token") String sessionToken,
            @Field("workout_id") int workoutId,
            @Field("exercises") String exercisesJson  // JSON array of exercises
    );

    // Save workouts method
    @POST("HeavyMetals/workout_save/save_workouts.php")
    Call<SaveWorkoutResponse> saveWorkouts(
            @Body WorkoutResponse workoutResponse
    );

    // Fetch workouts method
    @POST("HeavyMetals/workout_save/get_workout.php")
    Call<FetchWorkoutsResponse> fetchWorkouts(
            @Body UserIdRequest userIdRequest
    );

    // Get exercises method
    // Updated method to return List<WorkoutDetailExercise> instead of List<AdaptersExercise>
    @GET("HeavyMetals/workout_save/get_exercise.php")
    Call<ExerciseResponse> getExercises(
            @Query("session_token") String sessionToken,
            @Query("workout_id") int workoutId
    );



    // Delete workout method
    @FormUrlEncoded
    @POST("HeavyMetals/workout_save/delete_workout.php")
    Call<Void> deleteWorkout(
            @Field("workout_id") int workoutId,
            @Field("session_token") String sessionToken
    );
}
