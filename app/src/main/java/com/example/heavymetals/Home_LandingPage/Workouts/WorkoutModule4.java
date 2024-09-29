package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.example.heavymetals.Models.Adapters.WorkoutAdapter;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutList = new ArrayList<>();  // Initialize workout list

        // Load saved workouts from server or SharedPreferences
        fetchWorkoutsFromServer();

        // Add Workout button listener
        addWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
            startActivity(intent);
        });

        // Back button listener
        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        // Save button listener
        wm4_Save_txt.setOnClickListener(v -> {
            if (!workoutList.isEmpty()) {
                saveWorkoutsForUser(workoutList);  // Save workouts to server/SharedPreferences
                Toast.makeText(WorkoutModule4.this, "Workout manually saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WorkoutModule4.this, "No workout to save.", Toast.LENGTH_SHORT).show();
            }
        });

        // Load workout data if passed from another activity
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");
        if (workout != null) {
            addNewWorkout(workout);
        }
    }

    // Add a new workout to the list and update the RecyclerView
    private void addNewWorkout(Workout workout) {
        workoutList.add(workout);  // Add new workout to the list
        updateRecyclerView();
    }

    // Update the RecyclerView
    private void updateRecyclerView() {
        if (workoutAdapter == null) {
            workoutAdapter = new WorkoutAdapter(workoutList, this::onWorkoutViewClicked);
            recyclerView.setAdapter(workoutAdapter);
        } else {
            workoutAdapter.notifyDataSetChanged();  // Refresh adapter data
        }
    }

    // Fetch workouts from the server or load from SharedPreferences
    private void fetchWorkoutsFromServer() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (!loggedInUser.isEmpty()) {
            String url = "https://heavymetals.scarlet2.io/HeavyMetals/workout_save/get_workouts.php?user_id=" + loggedInUser;
            Log.d("FetchWorkouts", "Fetching workouts from URL: " + url);  // Log the URL
            new FetchWorkoutsTask().execute(url);
        } else {
            Log.e("FetchWorkouts", "No logged-in user found.");
            loadSavedWorkouts();  // Load from SharedPreferences if no user is found
        }
    }

    // AsyncTask to fetch workouts from the server
    private class FetchWorkoutsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                int responseCode = conn.getResponseCode();

                Log.d("FetchWorkoutsTask", "Response code: " + responseCode);  // Log response code
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    Log.e("FetchWorkoutsTask", "Failed to fetch workouts. Server response code: " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                Log.e("FetchWorkoutsTask", "Error fetching workouts from server", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                loadWorkoutsFromJson(result);  // Load fetched workouts into the UI
            } else {
                loadSavedWorkouts();  // Load from SharedPreferences if fetch fails
            }
        }
    }

    // Save workouts for the user (SharedPreferences and send to server)
    private void saveWorkoutsForUser(List<Workout> workouts) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Log.e("SaveWorkout", "No logged-in user found. Cannot save workouts.");
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;
        }

        // Save workouts to SharedPreferences
        Gson gson = new Gson();
        String workoutJson = gson.toJson(workouts);
        SharedPreferences workoutPrefs = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        SharedPreferences.Editor workoutEditor = workoutPrefs.edit();
        workoutEditor.putString("workout_" + loggedInUser, workoutJson);
        workoutEditor.apply();

        // Send workouts to the server
        new SaveWorkoutToServerTask().execute(workoutJson, loggedInUser);
    }

    // AsyncTask to save workouts to the server
    private class SaveWorkoutToServerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String workoutJson = params[0];
            String loggedInUser = params[1];
            String urlString = "https://heavymetals.scarlet2.io/HeavyMetals/workout_save/save_workout.php";

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Content-Type", "application/json");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(workoutJson);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("SaveWorkout", "Server response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    Log.e("SaveWorkout", "Failed to save workout. Server response code: " + responseCode);
                    return false;
                }
            } catch (Exception e) {
                Log.e("SaveWorkout", "Error saving workout to server", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(WorkoutModule4.this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WorkoutModule4.this, "Failed to save workout.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Parse the JSON response and load the workouts into the RecyclerView
    private void loadWorkoutsFromJson(String jsonResponse) {
        Gson gson = new Gson();
        Type workoutListType = new TypeToken<List<Workout>>() {}.getType();
        workoutList = gson.fromJson(jsonResponse, workoutListType);

        if (workoutList != null && !workoutList.isEmpty()) {
            updateRecyclerView();
        } else {
            Log.d("LoadWorkouts", "No workouts found on the server.");
        }
    }

    // Load saved workouts from SharedPreferences
    private void loadSavedWorkouts() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (loggedInUser.isEmpty()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences workoutPrefs = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        String workoutJson = workoutPrefs.getString("workout_" + loggedInUser, null);

        if (workoutJson != null) {
            Gson gson = new Gson();
            Type workoutListType = new TypeToken<List<Workout>>() {}.getType();
            workoutList = gson.fromJson(workoutJson, workoutListType);

            if (workoutList != null && !workoutList.isEmpty()) {
                updateRecyclerView();
            }
        } else {
            Log.d("LoadWorkouts", "No saved workouts found.");
        }
    }

    // When a workout is clicked, go to WorkoutDetailActivity
    private void onWorkoutViewClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<Exercise>) workout.getExercises());
        startActivity(intent);
    }

    // Navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateToMainActivity();
    }
}
