package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.Models.Adapters.WorkoutAdapter;
import com.example.heavymetals.Models.Adapters.WorkoutResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.RetrofitClient;
import com.example.heavymetals.Models.Adapters.WorkoutApi;
import com.example.heavymetals.network.SaveWorkoutResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutModule4 extends AppCompatActivity {

    private Button addWorkout;
    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private TextView wm4_Back_txt, wm4_Save_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module4);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Check if user is logged in
        checkLoginStatus();

        // Initialize UI elements
        addWorkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        wm4_Save_txt = findViewById(R.id.wm4_Save_txt);
        recyclerView = findViewById(R.id.recyclerViewWorkouts);

        // Set layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>(); // Initialize the workout list

        // Load saved workouts from SharedPreferences before fetching from server
        loadSavedWorkouts();

        // Fetch workouts from server
        fetchWorkoutsFromServer();

        // Set up button listeners
        addWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
            startActivity(intent);
        });

        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        wm4_Save_txt.setOnClickListener(v -> {
            if (!workoutList.isEmpty()) {
                saveWorkoutsForUser(workoutList); // Trigger saving workouts
                Toast.makeText(WorkoutModule4.this, "Workout manually saving!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WorkoutModule4.this, "No workout to save.", Toast.LENGTH_SHORT).show();
            }
        });


        // Load workout data passed from another activity (if any)
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");
        if (workout != null) {
            addNewWorkout(workout);
        }
    }

    // Step 6: Check if the user is logged in
    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (loggedInUser == null || authToken == null) {
            // User is not logged in, redirect to login screen
            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WorkoutModule4.this, LoginActivity.class);
            startActivity(intent);
            finish();  // Prevent going back to the current activity
        } else {
            Log.d("WorkoutModule4", "Logged-in user: " + loggedInUser);
        }
    }


    private void loadSavedWorkouts() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        String userEmail = getLoggedInUserEmail();

        if (userEmail == null) {
            Log.e("LoadWorkouts", "No logged-in user found.");
            return;
        }

        String workoutJson = sharedPreferences.getString("workout_" + userEmail, null);
        Log.d("LoadWorkouts", "Loading workouts for user: " + userEmail + ". Data: " + workoutJson);

        if (workoutJson != null) {
            Gson gson = new Gson();
            Type workoutListType = new TypeToken<List<Workout>>() {}.getType();
            workoutList = gson.fromJson(workoutJson, workoutListType);

            if (workoutList != null && !workoutList.isEmpty()) {
                updateRecyclerView(); // Update UI with loaded data
                Log.d("LoadWorkouts", "Workouts loaded from SharedPreferences.");
            } else {
                Log.d("LoadWorkouts", "No workouts found in SharedPreferences.");
            }
        } else {
            Log.d("LoadWorkouts", "No saved workouts found.");
        }
    }



    private void saveWorkoutsForUser(List<Workout> workouts) {
        String userEmail = getLoggedInUserEmail();

        if (userEmail == null) {
            Log.e("SaveWorkout", "No logged-in user found. Cannot save workouts.");
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;
        }

        // Save to SharedPreferences
        saveWorkoutsToLocalStorage(userEmail, workouts);

        // Proceed with the API call to save to the server
        WorkoutResponse workoutResponse = new WorkoutResponse(true, userEmail, workouts);

        // Log the request payload for debugging
        Gson gson = new Gson();
        String requestPayload = gson.toJson(workoutResponse);
        Log.d("SaveWorkout", "Request payload: " + requestPayload);

        // Use Retrofit to create an API call
        WorkoutApi workoutApi = RetrofitClient.getClient().create(WorkoutApi.class);
        Call<SaveWorkoutResponse> call = workoutApi.saveWorkouts(workoutResponse);

        call.enqueue(new Callback<SaveWorkoutResponse>() {
            @Override
            public void onResponse(Call<SaveWorkoutResponse> call, Response<SaveWorkoutResponse> response) {
                if (response.isSuccessful()) {
                    // Ensure the body is not null
                    SaveWorkoutResponse saveResponse = response.body();
                    if (saveResponse != null) {
                        // Check if the server returned success
                        if (saveResponse.isSuccess()) {
                            Toast.makeText(WorkoutModule4.this, "Workouts saved to server successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("SaveWorkout", "Server response indicates failure. Message: " + saveResponse.getMessage());
                            Toast.makeText(WorkoutModule4.this, "Failed to save workouts. Server message: " + saveResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("SaveWorkout", "Response body is null.");
                        Toast.makeText(WorkoutModule4.this, "Failed to save workouts. Response body is null.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int statusCode = response.code();
                    Log.e("SaveWorkout", "Failed to save workouts. Status code: " + statusCode);
                    Toast.makeText(WorkoutModule4.this, "Failed to save workouts to server. Status code: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaveWorkoutResponse> call, Throwable t) {
                Log.e("SaveWorkout", "Error saving workout to server", t);
                Toast.makeText(WorkoutModule4.this, "Error saving workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to save workouts to SharedPreferences
    private void saveWorkoutsToLocalStorage(String userEmail, List<Workout> workouts) {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String workoutJson = gson.toJson(workouts);
        editor.putString("workout_" + userEmail, workoutJson);
        editor.apply();  // Save changes asynchronously

        Log.d("SaveWorkouts", "Workouts saved locally for user: " + userEmail);
    }


    private String getLoggedInUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("loggedInUser", null);
    }

    private void fetchWorkoutsFromServer() {
        String userId = getLoggedInUserId();  // Fetch user_id from SharedPreferences

        if (userId == null) {
            Toast.makeText(WorkoutModule4.this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with the API call using userId
        WorkoutApi workoutApi = RetrofitClient.getClient().create(WorkoutApi.class);
        Call<WorkoutResponse> call = workoutApi.fetchWorkouts(userId);

        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                // Log the entire response to see what you're getting
                Log.d("FetchWorkouts", "Response: " + new Gson().toJson(response.body()));

                if (response.isSuccessful() && response.body() != null) {
                    WorkoutResponse workoutResponse = response.body();
                    if (workoutResponse.isSuccess()) {
                        workoutList = workoutResponse.getWorkouts();
                        Log.d("FetchWorkouts", "Fetched Workouts: " + new Gson().toJson(workoutList));

                        // Save the fetched workouts to SharedPreferences for future use
                        saveWorkoutsForUser(workoutList);

                        updateRecyclerView();  // Update UI with workouts
                    } else {
                        Toast.makeText(WorkoutModule4.this, "Failed to fetch workouts.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WorkoutModule4.this, "Failed to fetch workouts. Status code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkoutResponse> call, Throwable t) {
                Toast.makeText(WorkoutModule4.this, "Error fetching workouts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Get the logged-in user's email from SharedPreferences
    private String getLoggedInUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("loggedInUser", null);
    }

    // Add a new workout to the list and update the UI
    private void addNewWorkout(Workout workout) {
        workoutList.add(workout);
        updateRecyclerView();
    }

    // Set up the adapter in WorkoutModule4.java
    private void updateRecyclerView() {
        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    // Handle viewing the workout details
                    onWorkoutViewClicked(workout);
                }

                @Override
                public void onWorkoutDeleted() {
                    // Handle saving the updated list after deletion
                    saveWorkoutsForUser(workoutList);

                    if (workoutList.isEmpty()) {
                        Toast.makeText(WorkoutModule4.this, "All workouts deleted. Changes saved.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(WorkoutModule4.this, "Workout deleted. Changes saved.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            recyclerView.setAdapter(workoutAdapter);
        } else {
            // Log the updated data
            Log.d("RecyclerViewUpdate", "Updating RecyclerView with workoutList: " + new Gson().toJson(workoutList));

            workoutAdapter.notifyDataSetChanged();  // Refresh adapter data
        }
    }

    // Handle workout deletion and save updated list
    private void onWorkoutDeleted() {
        saveWorkoutsForUser(workoutList);

        if (workoutList.isEmpty()) {
            Toast.makeText(this, "All workouts deleted. Changes saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Workout deleted. Changes saved.", Toast.LENGTH_SHORT).show();
        }
    }

    // Navigate to WorkoutDetailActivity when a workout is clicked
    private void onWorkoutViewClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<Exercise>) workout.getExercises());
        startActivity(intent);
    }

    // Navigate back to the main activity
    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
