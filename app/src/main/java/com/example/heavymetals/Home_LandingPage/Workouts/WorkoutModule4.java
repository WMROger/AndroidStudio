package com.example.heavymetals.Home_LandingPage.Workouts;

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

public class WorkoutModule4 extends AppCompatActivity {

    private Button addWorkout;
    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private TextView wm4_Back_txt, wm4_Save_txt;
    private static final String CHANNEL_ID = "workout_notifications";  // Define CHANNEL_ID as a constant
    private static final int NOTIFICATION_ID = 100;  // Unique ID for notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module4);



        ScheduleDailyNotification notificationScheduler = new ScheduleDailyNotification();
        notificationScheduler.scheduleDailyNotification(this);  // `this` refers to the context (in this case, the activity context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Show a dialog to guide the user to grant the permission
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        // Initialize UI elements
        initializeUI();
        createNotificationChannel();  // Create notification channel

        ScheduleDailyNotification scheduleNotification = new ScheduleDailyNotification();
        scheduleNotification.scheduleDailyNotification(this);

        // Check if the user is logged in
        checkLoginStatus();

        // Initialize the workoutList to avoid NullPointerException
        workoutList = new ArrayList<>();

        // Load saved workouts from SharedPreferences before fetching from server
        loadSavedWorkouts();
        fetchWorkoutsFromServer();

        // Load workout data passed from another activity (if any)
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");
        if (workout != null) {
            addNewWorkout(workout);
        }

        // Trigger the notification after the workout list is initialized
        int workoutCount = (workoutList != null) ? workoutList.size() : 0;
        if (workoutCount > 0) {
            sendWorkoutNotification(workoutCount);
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

                    for (Workout workout : workoutList) {
                        Log.d("WorkoutModule4", "Fetched workout ID: " + workout.getWorkoutId() + ", workout name: " + workout.getTitle());
                    }

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


    // Create the Notification Channel for Android O and above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Workout Reminder";
            String description = "Reminds the user of pending workouts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Trigger the notification to remind the user about workouts
    private void sendWorkoutNotification(int workoutCount) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.human_icon)  // Set your notification icon
                .setContentTitle("Workout Reminder")
                .setContentText("You have " + workoutCount + " workouts to complete. Let's get to work!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Trigger the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void initializeUI() {
        addWorkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        wm4_Save_txt = findViewById(R.id.wm4_Save_txt);
        recyclerView = findViewById(R.id.recyclerViewWorkouts);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>(); // Ensure workoutList is initialized

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

            // Check for invalid workout IDs
            if (workoutList != null && !workoutList.isEmpty()) {
                for (Workout workout : workoutList) {
                    if (workout.getWorkoutId() > 0) {
                        Log.d("LoadWorkouts", "Loaded valid workout ID from SharedPreferences: " + workout.getWorkoutId());
                    } else {
                        Log.e("LoadWorkouts", "Invalid workout ID found in SharedPreferences: " + workout.getWorkoutId());
                        // You can skip this workout or clear it from SharedPreferences if needed
                    }
                }

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
                    Log.d("WorkoutModule4", "Workout deleted from server: " + workout.getTitle());

                    // Remove workout from local list
                    workoutList.remove(workout);
                    workoutAdapter.notifyDataSetChanged();

                    // Save the updated list locally
                    saveWorkoutsForUser(workoutList);

                    Toast.makeText(WorkoutModule4.this, "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WorkoutModule4.this, "Failed to delete workout on the server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(WorkoutModule4.this, "Error deleting workout: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onWorkoutViewClicked(Workout workout) {
        Log.d("WorkoutModule4", "Workout clicked with ID: " + workout.getWorkoutId());

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null);

        if (sessionToken != null && workout != null && workout.getWorkoutId() != 0) {
            Intent intent = new Intent(this, WorkoutDetailActivity.class);
            intent.putExtra("workout_id", workout.getWorkoutId());  // Pass the workout ID
            intent.putExtra("session_token", sessionToken);         // Pass the session token
            startActivity(intent);
        } else {
            Log.e("WorkoutModule4", "Workout ID is invalid or session token is null.");
            Toast.makeText(this, "Unable to open workout details. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }





    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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

                    // Handle the fetched exercises
                    if (exercises != null) {
                        Log.d("WorkoutModule4", "Fetched exercises for workout ID: " + workoutId);
                        // Display exercises, update UI or handle them as required
                        displayExercises(exercises);
                    }
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
        // Assuming you have a RecyclerView for displaying exercises
        // Assuming you have a RecyclerView for displaying exercises
        RecyclerView exercisesRecyclerView = findViewById(R.id.recyclerViewWorkouts);

// Create an adapter and pass the list of exercises to it
        ExercisesAdapter exercisesAdapter = new ExercisesAdapter(exercises);

// Set up the RecyclerView with the adapter
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exercisesRecyclerView.setAdapter(exercisesAdapter);
    }


    private String getSessionToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("auth_token", null);
    }
}
