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
import com.example.heavymetals.Models.Adapters.WorkoutApi;
import com.example.heavymetals.Models.Adapters.WorkoutResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.RetrofitClient;
import com.example.heavymetals.network.SaveWorkoutResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

    // Method to add a new workout to the list and update the RecyclerView
    private void addNewWorkout(Workout workout) {
        // Add the workout to the workout list
        workoutList.add(workout);

        // Update the RecyclerView with the new workout added
        if (workoutAdapter == null) {
            // If adapter is not initialized, set it up
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    // Handle viewing the workout details
                    onWorkoutViewClicked(workout);
                }

                @Override
                public void onWorkoutDeleted() {
                    // Handle deletion of a workout
                    onWorkoutDeleted();
                }
            });
            recyclerView.setAdapter(workoutAdapter); // Set the adapter to RecyclerView
        } else {
            workoutAdapter.notifyDataSetChanged(); // Notify adapter about data change
        }
    }

    // Check if the user is logged in
    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);

        if (loggedInUser == null) {
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

        Log.d("SaveWorkout", "Workouts saved locally for user: " + userEmail);
    }

    private void saveWorkoutsToLocalStorage(String userEmail, List<Workout> workouts) {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String workoutJson = gson.toJson(workouts);
        editor.putString("workout_" + userEmail, workoutJson);
        editor.apply();  // Save changes asynchronously

        Log.d("SaveWorkouts", "Workouts saved locally for user: " + userEmail);
    }

    private String getLoggedInUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("loggedInUser", null);
    }

    private void updateRecyclerView() {
        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    onWorkoutViewClicked(workout);
                }

                @Override
                public void onWorkoutDeleted() {
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
            Log.d("RecyclerViewUpdate", "Updating RecyclerView with workoutList: " + new Gson().toJson(workoutList));
            workoutAdapter.notifyDataSetChanged();  // Refresh adapter data
        }
    }

    private void onWorkoutViewClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<Exercise>) workout.getExercises());
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // New Code for Server Integration Starts Below

    private void saveWorkoutsToServer(List<Workout> workouts) {
        String userEmail = getLoggedInUserEmail();

        if (userEmail == null) {
            Log.e("SaveWorkout", "No logged-in user found. Cannot save workouts to server.");
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;
        }

        // Create a WorkoutResponse object to send to the server
        WorkoutResponse workoutResponse = new WorkoutResponse(true, userEmail, workouts);

        // Log the request payload
        Gson gson = new Gson();
        String requestPayload = gson.toJson(workoutResponse);
        Log.d("SaveWorkout", "Request payload: " + requestPayload);

        // Use Retrofit to send the data to the server
        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        WorkoutApi workoutApi = retrofit.create(WorkoutApi.class);

        // Call the API to save workouts
        Call<SaveWorkoutResponse> call = workoutApi.saveWorkouts(workoutResponse);
        call.enqueue(new Callback<SaveWorkoutResponse>() {
            @Override
            public void onResponse(Call<SaveWorkoutResponse> call, Response<SaveWorkoutResponse> response) {
                if (response.isSuccessful()) {
                    SaveWorkoutResponse saveResponse = response.body();
                    if (saveResponse != null && saveResponse.isSuccess()) {
                        Toast.makeText(WorkoutModule4.this, "Workouts saved to server successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("SaveWorkout", "Server response indicates failure: " + (saveResponse != null ? saveResponse.getMessage() : "Unknown error"));
                        Toast.makeText(WorkoutModule4.this, "Failed to save workouts.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("SaveWorkout", "Server error: Status code: " + response.code());
                    Toast.makeText(WorkoutModule4.this, "Failed to save workouts. Status code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaveWorkoutResponse> call, Throwable t) {
                Log.e("SaveWorkout", "Error saving workout to server", t);
                Toast.makeText(WorkoutModule4.this, "Error saving workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Remember to call `saveWorkoutsToServer(workoutList);` inside the wm4_Save_txt click listener
}
