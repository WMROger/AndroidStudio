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
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
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

        // Initialize UI elements
        initializeUI();

        // Check if the user is logged in
        checkLoginStatus();

        // Load saved workouts from SharedPreferences before fetching from server
        loadSavedWorkouts();

        // Load workout data passed from another activity (if any)
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");
        if (workout != null) {
            addNewWorkout(workout);
        }
    }

    private void initializeUI() {
        addWorkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        wm4_Save_txt = findViewById(R.id.wm4_Save_txt);
        recyclerView = findViewById(R.id.recyclerViewWorkouts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();

        // Add new workout button listener
        addWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
            startActivity(intent);
        });

        // Navigate back to the main activity
        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        // Save workouts button listener
        wm4_Save_txt.setOnClickListener(v -> {
            if (!workoutList.isEmpty()) {
                saveWorkoutsForUser(workoutList);
                Toast.makeText(WorkoutModule4.this, "Workout manually saving!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WorkoutModule4.this, "No workout to save.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addNewWorkout(Workout workout) {
        workoutList.add(workout);

        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    onWorkoutViewClicked(workout);
                }

                @Override
                public void onWorkoutDeleted(Workout workout) {
                    // This method should be triggered when a workout is deleted
                    onWorkoutDeleted(workout);  // Call the deletion method in WorkoutModule4
                }
            });

            recyclerView.setAdapter(workoutAdapter);
        } else {
            workoutAdapter.notifyDataSetChanged();
        }
    }









    /**
     * Checks if the user is logged in by validating session.
     */
    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);

        if (loggedInUser == null) {
            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WorkoutModule4.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Loads saved workouts from SharedPreferences.
     */
    private void loadSavedWorkouts() {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        String userEmail = getLoggedInUserEmail();

        if (userEmail == null) {
            Log.e("LoadWorkouts", "No logged-in user found.");
            return;
        }

        String workoutJson = sharedPreferences.getString("workout_" + userEmail, null);
        if (workoutJson != null) {
            Gson gson = new Gson();
            Type workoutListType = new TypeToken<List<Workout>>() {}.getType();
            workoutList = gson.fromJson(workoutJson, workoutListType);
            if (workoutList != null && !workoutList.isEmpty()) {
                updateRecyclerView();
            }
        }
    }

    /**
     * Saves the updated workout list to SharedPreferences.
     */
    private void saveWorkoutsForUser(List<Workout> workouts) {
        String userEmail = getLoggedInUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;
        }
        saveWorkoutsToLocalStorage(userEmail, workouts);
    }

    /**
     * Saves the workouts to SharedPreferences.
     */
    private void saveWorkoutsToLocalStorage(String userEmail, List<Workout> workouts) {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String workoutJson = gson.toJson(workouts);
        editor.putString("workout_" + userEmail, workoutJson);
        editor.apply();
    }

    /**
     * Retrieves the logged-in user's email.
     */
    private String getLoggedInUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("loggedInUser", null);
    }

    /**
     * Updates the RecyclerView with the workout list.
     */
    private void updateRecyclerView() {
        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    onWorkoutViewClicked(workout);
                }

                @Override
                public void onWorkoutDeleted(Workout workout) {
                    // This will call the actual deletion method
                    onWorkoutDeleted(workout);
                }

            });
            recyclerView.setAdapter(workoutAdapter);
        } else {
            workoutAdapter.notifyDataSetChanged();
        }
    }


    public void onWorkoutDeleted(Workout workout) {
        if (workout == null) {
            Log.e("WorkoutModule4", "Attempted to delete a null workout.");
            Toast.makeText(this, "Workout not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the server to delete the workout first
        deleteWorkoutFromServer(workout);
    }

    private void deleteWorkoutFromServer(Workout workout) {
        String sessionToken = getSessionToken();
        if (sessionToken == null) {
            Toast.makeText(this, "Please log in to delete workout.", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        WorkoutApi workoutApi = retrofit.create(WorkoutApi.class);

        // Make the call to delete the workout on the server
        Call<Void> call = workoutApi.deleteWorkout(workout.getWorkoutId(), sessionToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Workout deleted successfully on the server, now delete locally
                    Log.d("WorkoutModule4", "Workout deleted from server: " + workout.getTitle());

                    // Remove workout from local list
                    workoutList.remove(workout);

                    // Notify the adapter about the data change
                    if (workoutAdapter != null) {
                        workoutAdapter.notifyDataSetChanged();
                    }

                    // Save the updated list locally
                    saveWorkoutsForUser(workoutList);

                    Toast.makeText(WorkoutModule4.this, "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Deletion on the server failed, show a toast
                    Toast.makeText(WorkoutModule4.this, "Failed to delete workout on the server", Toast.LENGTH_SHORT).show();
                    Log.e("WorkoutModule4", "Server deletion failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle failure
                Toast.makeText(WorkoutModule4.this, "Error deleting workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WorkoutModule4", "Error deleting workout from server", t);
            }
        });

    }




    /**
     * Handles viewing workout details.
     */
    private void onWorkoutViewClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<AdaptersExercise>) workout.getExercises());
        startActivity(intent);
    }

    /**
     * Navigates back to the main activity.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // ------------ Server Integration Methods ------------

    /**
     * Saves the workouts to the server.
     */
    private void saveWorkoutsToServer(List<Workout> workouts) {
        String userEmail = getLoggedInUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;
        }

        WorkoutResponse workoutResponse = new WorkoutResponse(true, userEmail, workouts);

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        WorkoutApi workoutApi = retrofit.create(WorkoutApi.class);

        Call<SaveWorkoutResponse> call = workoutApi.saveWorkouts(workoutResponse);
        call.enqueue(new Callback<SaveWorkoutResponse>() {
            @Override
            public void onResponse(Call<SaveWorkoutResponse> call, Response<SaveWorkoutResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(WorkoutModule4.this, "Workouts saved to server successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WorkoutModule4.this, "Failed to save workouts.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SaveWorkoutResponse> call, Throwable t) {
                Toast.makeText(WorkoutModule4.this, "Error saving workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches workouts from the server.
     */
    private void fetchWorkoutsFromServer() {
        String sessionToken = getSessionToken();
        if (sessionToken == null) {
            Toast.makeText(this, "Please log in to fetch workouts.", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        WorkoutApi workoutApi = retrofit.create(WorkoutApi.class);

        Call<WorkoutResponse> call = workoutApi.getWorkouts(sessionToken);
        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    workoutList = response.body().getWorkouts();
                    updateRecyclerView();
                } else {
                    Toast.makeText(WorkoutModule4.this, "Failed to load workouts.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WorkoutResponse> call, Throwable t) {
                Toast.makeText(WorkoutModule4.this, "Error loading workouts: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Retrieves the session token for API calls.
     */
    private String getSessionToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", null);
    }
}
