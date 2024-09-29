package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        addWorkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        wm4_Save_txt = findViewById(R.id.wm4_Save_txt);  // Save button

        recyclerView = findViewById(R.id.recyclerViewWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize workout list
        workoutList = new ArrayList<>();

        // Load saved workouts from server or SharedPreferences
        fetchWorkoutsFromServer();

        // Back button listener to navigate to MainActivity
        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        addWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
            startActivity(intent);
        });

        // Load workout data if passed from the previous activity
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        if (workout != null) {
            // Add the new workout to the list
            addNewWorkout(workout);
        }

        // Save button listener for manual save
        wm4_Save_txt.setOnClickListener(v -> {
            if (!workoutList.isEmpty()) {
                saveWorkoutsForUser(workoutList);  // Save the workouts when the save button is clicked
                Toast.makeText(WorkoutModule4.this, "Workout manually saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WorkoutModule4.this, "No workout to save.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add a new workout and update the RecyclerView
    private void addNewWorkout(Workout workout) {
        if (workoutList == null) {
            workoutList = new ArrayList<>();
        }

        // Add the new workout to the list
        workoutList.add(workout);

        // Notify adapter about new item
        updateRecyclerView();
    }

    // Update RecyclerView data
    private void updateRecyclerView() {
        runOnUiThread(() -> {
            if (workoutAdapter == null) {
                workoutAdapter = new WorkoutAdapter(workoutList, this::onWorkoutViewClicked);
                recyclerView.setAdapter(workoutAdapter);
            } else {
                // Notify only about the newly added item
                workoutAdapter.notifyItemInserted(workoutList.size() - 1);
                recyclerView.scrollToPosition(workoutList.size() - 1);  // Scroll to the newly added item
            }
        });
    }

    // Method to fetch workouts from the server or SharedPreferences
    private void fetchWorkoutsFromServer() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (!loggedInUser.isEmpty()) {
            new FetchWorkoutsTask().execute("https://heavymetals.scarlet2.io/HeavyMetals/workout_save/get_workouts.php?user_id=" + loggedInUser);
        } else {
            loadSavedWorkouts();  // Load from SharedPreferences if no user is found
        }
    }

    private void saveWorkoutsForUser(List<Workout> workouts) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        // Check if a user is logged in
        if (loggedInUser == null || loggedInUser.isEmpty()) {
            // No logged-in user, display a message and skip the save process
            Log.e("SaveWorkout", "No logged-in user found. Workouts will not be saved.");
            Toast.makeText(this, "No logged-in user found. Cannot save workouts.", Toast.LENGTH_LONG).show();
            return;  // Exit the method to prevent saving
        }

        // Proceed with saving if the user is logged in
        Gson gson = new Gson();
        String workoutJson = gson.toJson(workouts);  // Save the entire workout list

        SharedPreferences workoutPrefs = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        SharedPreferences.Editor workoutEditor = workoutPrefs.edit();
        workoutEditor.putString("workout_" + loggedInUser, workoutJson);
        workoutEditor.apply();

        Log.d("SaveWorkout", "Workouts saved for user: " + loggedInUser);

        // Send workout to the server
        new SaveWorkoutToServerTask().execute(workoutJson, loggedInUser);
    }



    // AsyncTask to save workout and exercises to the server
    private class SaveWorkoutToServerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String workoutJson = params[0];
            String loggedInUser = params[1];
            String urlString = "https://heavymetals.scarlet2.io/HeavyMetals/workout_save/save_workout.php"; // Replace with your actual URL

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);  // 15 seconds timeout
                conn.setReadTimeout(10000);     // 10 seconds read timeout
                conn.setRequestProperty("Content-Type", "application/json");

                // Write the JSON workout and exercises data
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(workoutJson);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("SaveWorkout", "Server response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d("SaveWorkout", "Server response: " + response.toString());
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
                    Log.d("SaveWorkout", "Workout and exercises successfully saved to server.");
                    Toast.makeText(WorkoutModule4.this, "Workout and exercises saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("SaveWorkout", "Failed to save workout and exercises.");
                    Toast.makeText(WorkoutModule4.this, "Failed to save workout and exercises.", Toast.LENGTH_SHORT).show();
                }
            });
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
                conn.setConnectTimeout(15000);  // 15 seconds timeout
                conn.setReadTimeout(10000);     // 10 seconds read timeout
                int responseCode = conn.getResponseCode();

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
                    Log.e("FetchWorkouts", "Failed to fetch workouts. Server response code: " + responseCode);
                    return null;
                }
            } catch (Exception e) {
                Log.e("FetchWorkouts", "Error fetching workouts from server", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Parse the JSON and load it into the RecyclerView
                loadWorkoutsFromJson(result);
            } else {
                // Load from SharedPreferences if the server fetch fails
                loadSavedWorkouts();
            }
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

    private void loadSavedWorkouts() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Log.e("LoadWorkouts", "No logged-in user found.");
            Toast.makeText(this, "No logged-in user found. Cannot load workouts.", Toast.LENGTH_LONG).show();
            return;  // Exit the method to prevent loading
        }

        SharedPreferences workoutPrefs = getSharedPreferences("WorkoutData", MODE_PRIVATE);
        String workoutJson = workoutPrefs.getString("workout_" + loggedInUser, null);

        if (workoutJson != null) {
            Gson gson = new Gson();
            try {
                Type workoutListType = new TypeToken<List<Workout>>() {}.getType();
                workoutList = gson.fromJson(workoutJson, workoutListType);

                if (workoutList != null && !workoutList.isEmpty()) {
                    updateRecyclerView();
                }
            } catch (JsonSyntaxException e) {
                Log.e("WorkoutModule4", "Failed to parse workout JSON", e);
            }
        } else {
            Log.d("LoadWorkouts", "No saved workout found for the user.");
        }
    }


    private void onWorkoutViewClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<Exercise>) workout.getExercises());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        navigateToMainActivity();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();  // Ensure this activity is finished
    }
}
