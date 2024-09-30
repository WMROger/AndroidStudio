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

        // Initialize UI elements
        addWorkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        wm4_Save_txt = findViewById(R.id.wm4_Save_txt);
        recyclerView = findViewById(R.id.recyclerViewWorkouts);

        // Set layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>(); // Initialize the workout list

        // Load saved workouts from server or SharedPreferences
        fetchWorkoutsFromServer();

        // Add Workout button listener
        addWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
            startActivity(intent);
        });

        // Back button listener to navigate to MainActivity
        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        // Save button listener to save workouts
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

    // Load saved workouts from SharedPreferences
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
            Type workoutListType = new TypeToken<List<Workout>>() {
            }.getType();
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

        // Wrap the workout list in a WorkoutRequest object
        WorkoutResponse workoutResponse = new WorkoutResponse(true,userEmail, workouts);

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
                    // Check if body is null
                    if (response.body() != null) {
                        SaveWorkoutResponse saveResponse = response.body();
                        // Check if the response indicates success
                        if (saveResponse.isSuccess()) {
                            Toast.makeText(WorkoutModule4.this, "Workouts saved to server successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Log and show why the success check failed
                            Log.e("SaveWorkout", "Server response indicates failure. Success field: " + saveResponse.isSuccess());
                            Toast.makeText(WorkoutModule4.this, "Failed to save workouts. Server did not return success.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Body is null
                        Log.e("SaveWorkout", "Response body is null, despite status code 200");
                        Toast.makeText(WorkoutModule4.this, "Failed to save workouts. Response body is null.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Server returned a failure HTTP status
                    int statusCode = response.code();
                    Log.e("SaveWorkout", "Failed to save workouts. Status code: " + statusCode);
                    Toast.makeText(WorkoutModule4.this, "Failed to save workouts to server. Status code: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaveWorkoutResponse> call, Throwable t) {
                // Network or other error
                Log.e("SaveWorkout", "Error saving workout to server", t);
                Toast.makeText(WorkoutModule4.this, "Error saving workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void fetchWorkoutsFromServer() {
        String userId = "6";  // Assuming userId 6 for now, you can fetch it dynamically.

        WorkoutApi workoutApi = RetrofitClient.getClient().create(WorkoutApi.class);
        Call<WorkoutResponse> call = workoutApi.fetchWorkouts(userId);

        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WorkoutResponse workoutResponse = response.body();
                    if (workoutResponse.isSuccess()) {
                        workoutList = workoutResponse.getWorkouts();  // Get the workout list from response
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

    // Update RecyclerView with the latest workout data
    private void updateRecyclerView() {
        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    onWorkoutViewClicked(workout);
                }

                @Override
                public void onWorkoutDeleted() {
                    onWorkoutDeleted();
                }
            });
            recyclerView.setAdapter(workoutAdapter);
        } else {
            workoutAdapter.notifyDataSetChanged(); // Refresh adapter data
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
