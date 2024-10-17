package com.example.heavymetals.Home_LandingPage.Workouts;

import static android.app.PendingIntent.getActivity;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.Adapters.ExercisesAdapter;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.Models.Adapters.WorkoutAdapter;
import com.example.heavymetals.Models.Adapters.WorkoutApi;
import com.example.heavymetals.Models.Adapters.WorkoutResponse;
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;
import com.example.heavymetals.network.SaveWorkoutResponse;
import com.example.heavymetals.network.ScheduleDailyNotification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public class WorkoutModule4 extends AppCompatActivity {

    private Button addWorkout, wm4_Save_btn;
    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private TextView wm4_Back_txt;
    private static final String CHANNEL_ID = "workout_notifications";  // Define CHANNEL_ID as a constant
    private static final int NOTIFICATION_ID = 100;  // Unique ID for notifications
    private Handler handler = new Handler(Looper.getMainLooper()); // Use Handler to schedule the task
    private Runnable refreshRunnable;  // Define the runnable task
    private boolean fromTracker; // Flag to indicate if the user is coming from the tracker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module4);

        // Get the intent to check where the user is coming from
        fromTracker = getIntent().getBooleanExtra("fromTracker", false);

        // Initialize UI elements
        initializeUI();

        // Initialize the workoutList to avoid NullPointerException
        workoutList = new ArrayList<>();

        // Load saved workouts from SharedPreferences before fetching from server
        fetchWorkoutsFromServer();

        // Define the periodic refresh task
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchWorkoutsFromServer();  // Fetch new workouts from the server
                handler.postDelayed(this, 5000);  // Re-run this task every 5 seconds
            }
        };

        // Start the periodic refresh
        handler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the callback to stop the periodic updates when activity is destroyed
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    private void initializeUI() {
        addWorkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        wm4_Save_btn = findViewById(R.id.wm4_save_btn);
        recyclerView = findViewById(R.id.recyclerViewWorkouts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>(); // Ensure workoutList is initialized



        // Add new workout button listener
        addWorkout.setOnClickListener(v -> {
            if (fromTracker) {
                if (!workoutList.isEmpty()) {
                    addToTracker(workoutList);  // Call method to add to the tracker's progress
                    Toast.makeText(WorkoutModule4.this, "Workout added to tracker!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WorkoutModule4.this, "No workout to add.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
                startActivity(intent);
            }
        });

        // Navigate back to the main activity
        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        // Save workouts button listener
        wm4_Save_btn.setOnClickListener(v -> {
            if (!workoutList.isEmpty()) {
                saveWorkoutsForUser(workoutList);
                Toast.makeText(WorkoutModule4.this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WorkoutModule4.this, "No workout to save.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToTracker(List<Workout> workoutList) {
        String userEmail = getLoggedInUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "No logged-in user found. Cannot add to tracker.", Toast.LENGTH_LONG).show();
            return;
        }

        // Retrieve existing tracker data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("TrackerData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Get previously tracked workouts, or create a new empty list if none exists
        String trackerJson = sharedPreferences.getString("tracker_" + userEmail, "");
        List<Workout> trackerWorkouts;
        if (!trackerJson.isEmpty()) {
            Type workoutListType = new TypeToken<ArrayList<Workout>>() {}.getType();
            trackerWorkouts = gson.fromJson(trackerJson, workoutListType);
        } else {
            trackerWorkouts = new ArrayList<>();
        }

        // Add new workouts to the tracker list
        trackerWorkouts.addAll(workoutList);

        // Save the updated tracker list back to SharedPreferences
        String updatedTrackerJson = gson.toJson(trackerWorkouts);
        editor.putString("tracker_" + userEmail, updatedTrackerJson);
        editor.apply();

        Toast.makeText(this, "Workout added to your tracker progress!", Toast.LENGTH_SHORT).show();
    }


    private void loadTrackedWorkouts() {
        String userEmail = getLoggedInUserEmail();
        SharedPreferences sharedPreferences = this.getSharedPreferences("TrackerData", Context.MODE_PRIVATE);
        String trackerJson = sharedPreferences.getString("tracker_" + userEmail, "");

        if (!trackerJson.isEmpty()) {
            Gson gson = new Gson();
            Type workoutListType = new TypeToken<ArrayList<Workout>>() {}.getType();
            List<Workout> trackedWorkouts = gson.fromJson(trackerJson, workoutListType);

            // Update your RecyclerView or UI with the tracked workouts
            workoutAdapter.updateWorkouts(trackedWorkouts);
        } else {
            Toast.makeText(this, "No workouts tracked yet!", Toast.LENGTH_SHORT).show();
        }
    }



    private void fetchWorkoutsFromServer() {
        String sessionToken = getSessionToken();
        if (sessionToken == null) {
            Toast.makeText(this, "Please log in to fetch workouts.", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        WorkoutApi workoutApi = retrofit.create(WorkoutApi.class);

        // Fetch workouts from the server
        Call<WorkoutResponse> call = workoutApi.getWorkouts(sessionToken);
        call.enqueue(new Callback<WorkoutResponse>() {
            @Override
            public void onResponse(Call<WorkoutResponse> call, Response<WorkoutResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    workoutList = response.body().getWorkouts();
                    updateRecyclerView();  // Update RecyclerView with new data
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
                    if (workout == null) {
                        Log.e("WorkoutModule4", "Attempted to delete a null workout.");
                        return;
                    }

                    Log.d("WorkoutModule4", "Deleting workout: " + workout.getTitle());

                    // Call the server to delete the workout
                    deleteWorkoutFromServer(workout);
                }
            }, fromTracker, this, getLoggedInUserEmail());  // Pass 'this' as context and 'getLoggedInUserEmail()'

            recyclerView.setAdapter(workoutAdapter);
        } else {
            workoutAdapter.notifyDataSetChanged();
        }
    }


    private void updateRecyclerView() {
        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, new WorkoutAdapter.OnWorkoutClickListener() {
                @Override
                public void onViewWorkoutClick(Workout workout) {
                    onWorkoutViewClicked(workout);  // Respond to viewing workout
                }

                @Override
                public void onWorkoutDeleted(Workout workout) {
                    if (workout == null) {
                        Log.e("WorkoutModule4", "Attempted to delete a null workout.");
                        return;
                    }

                    Log.d("WorkoutModule4", "Deleting workout: " + workout.getTitle());

                    // Call the server to delete the workout
                    deleteWorkoutFromServer(workout);
                }
            }, fromTracker, this, getLoggedInUserEmail());  // Pass 'this' as context and 'getLoggedInUserEmail()'

            recyclerView.setAdapter(workoutAdapter);
        } else {
            workoutAdapter.notifyDataSetChanged();
        }
    }


    private void onWorkoutViewClicked(Workout workout) {
        Log.d("WorkoutModule4", "Workout clicked with ID: " + workout.getWorkoutId());

        if (workout.getWorkoutId() == 0) {
            Log.e("WorkoutModule4", "Invalid workout ID, cannot view details.");
            Toast.makeText(this, "Unable to open workout details. Invalid workout ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null);

        if (sessionToken != null && workout != null) {
            Intent intent = new Intent(this, WorkoutDetailActivity.class);
            intent.putExtra("workout_id", workout.getWorkoutId());
            intent.putExtra("session_token", sessionToken);
            startActivity(intent);
        } else {
            Log.e("WorkoutModule4", "Workout ID is invalid or session token is null.");
            Toast.makeText(this, "Unable to open workout details. Please try again.", Toast.LENGTH_SHORT).show();
        }
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
                    Log.d("WorkoutModule4", "Workout deleted from server: " + workout.getTitle());

                    runOnUiThread(() -> {
                        workoutList.remove(workout);
                        workoutAdapter.notifyDataSetChanged();
                        saveWorkoutsForUser(workoutList);

                        Toast.makeText(WorkoutModule4.this, "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    Log.e("WorkoutModule4", "Server failed to delete workout. Response code: " + response.code());
                    runOnUiThread(() -> Toast.makeText(WorkoutModule4.this, "Failed to delete workout on the server", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("WorkoutModule4", "Error deleting workout", t);
                runOnUiThread(() -> Toast.makeText(WorkoutModule4.this, "Error deleting workout: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void saveWorkoutsForUser(List<Workout> workouts) {
        String userEmail = getLoggedInUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;
        }

        // Filter out workouts with invalid IDs
        List<Workout> validWorkouts = new ArrayList<>();
        for (Workout workout : workouts) {
            if (workout.getWorkoutId() > 0) {
                validWorkouts.add(workout);
            } else {
                Log.e("WorkoutModule4", "Skipping invalid workout with ID: " + workout.getWorkoutId());
            }
        }

        saveWorkoutsToLocalStorage(userEmail, validWorkouts);
    }

    private void saveWorkoutsToLocalStorage(String userEmail, List<Workout> workouts) {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String workoutJson = gson.toJson(workouts);
        editor.putString("workout_" + userEmail, workoutJson);
        editor.apply();
    }

    private String getLoggedInUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("loggedInUser", null);
    }

    private String getSessionToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", null);
    }

    // ------------ Server Integration Methods ------------

    private void fetchExercises(int workoutId, String sessionToken) {
        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        ApiService exerciseApi = retrofit.create(ApiService.class);

        // Make the call to fetch exercises for the workoutId
        Call<ExerciseResponse> call = exerciseApi.getExercises(sessionToken, workoutId);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<AdaptersExercise> exercises = response.body().getExercises();
                    displayExercises(exercises);
                } else {
                    Log.e("WorkoutModule4", "Failed to fetch exercises for workout ID: " + workoutId);
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                Log.e("WorkoutModule4", "Error fetching exercises: " + t.getMessage());
            }
        });
    }

    private void displayExercises(List<AdaptersExercise> exercises) {
        RecyclerView exercisesRecyclerView = findViewById(R.id.recyclerViewWorkouts);
        ExercisesAdapter exercisesAdapter = new ExercisesAdapter(exercises);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setAdapter(exercisesAdapter);
    }
}
