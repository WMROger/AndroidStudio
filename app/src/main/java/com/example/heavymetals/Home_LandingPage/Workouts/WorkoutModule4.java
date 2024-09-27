package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.TextView;

import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.Models.Adapters.WorkoutAdapter;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WorkoutModule4 extends AppCompatActivity {
    private Button addworkout;
    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList;
    private TextView workoutTextView, wm4_Back_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module4);

        addworkout = findViewById(R.id.btnAddWorkout);
        wm4_Back_txt = findViewById(R.id.wm4_Back_txt);
        recyclerView = findViewById(R.id.recyclerViewWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Back button listener to navigate to MainActivity
        wm4_Back_txt.setOnClickListener(v -> navigateToMainActivity());

        addworkout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule4.this, Exercises_All.class);
            startActivity(intent);
        });

        // Load workout data if passed from the previous activity
        Workout workout = (Workout) getIntent().getSerializableExtra("workout");

        if (workout != null) {
            // Create a list containing this one workout
            workoutList = new ArrayList<>();
            workoutList.add(workout);

            // Initialize the adapter with a list of one workout and set it to RecyclerView
            workoutAdapter = new WorkoutAdapter(workoutList, this::onWorkoutViewClicked);
            recyclerView.setAdapter(workoutAdapter);

            // Save the workout for this specific user
            saveWorkoutForUser(workout);
        } else {
            // Load saved workouts from server or SharedPreferences
            fetchWorkoutsFromServer();  // Try to load from server first
        }
    }

    // Save workout to SharedPreferences
    private void saveWorkoutForUser(Workout workout) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (!loggedInUser.isEmpty()) {
            Gson gson = new Gson();
            String workoutJson = gson.toJson(workout);

            SharedPreferences workoutPrefs = getSharedPreferences("WorkoutData", MODE_PRIVATE);
            SharedPreferences.Editor workoutEditor = workoutPrefs.edit();
            workoutEditor.putString("workout_" + loggedInUser, workoutJson);
            workoutEditor.apply();
        } else {
            System.out.println("No logged-in user found. Workout will not be saved.");
        }
    }

    // Fetch workouts from server
    private void fetchWorkoutsFromServer() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (!loggedInUser.isEmpty()) {
            // Assuming you use the username or ID as the user identifier
            String userId = loggedInUser;

            // Fetch workouts for the logged-in user
            new FetchWorkoutsTask().execute("http://your-server-url/get_workouts.php?user_id=" + userId);
        } else {
            // Load from SharedPreferences if no user is found
            loadSavedWorkouts();
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
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            workoutAdapter = new WorkoutAdapter(workoutList, this::onWorkoutViewClicked);
            recyclerView.setAdapter(workoutAdapter);
        } else {
            System.out.println("No workouts found on the server.");
        }
    }

    // Load saved workouts from SharedPreferences
    private void loadSavedWorkouts() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", "");

        if (!loggedInUser.isEmpty()) {
            SharedPreferences workoutPrefs = getSharedPreferences("WorkoutData", MODE_PRIVATE);
            String workoutJson = workoutPrefs.getString("workout_" + loggedInUser, null);

            if (workoutJson != null) {
                Gson gson = new Gson();
                Workout savedWorkout = gson.fromJson(workoutJson, Workout.class);

                workoutList = new ArrayList<>();
                workoutList.add(savedWorkout);

                workoutAdapter = new WorkoutAdapter(workoutList, this::onWorkoutViewClicked);
                recyclerView.setAdapter(workoutAdapter);
            } else {
                System.out.println("No saved workout found for the user.");
            }
        } else {
            System.out.println("No logged-in user found.");
        }
    }

    private void onWorkoutViewClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutDetailActivity.class);
        intent.putExtra("exercises", (ArrayList<Exercise>) workout.getExercises());
        startActivity(intent);
    }

    // Handle physical back button press
    @Override
    public void onBackPressed() {
        navigateToMainActivity();
    }

    // Helper method to navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutModule4.this, MainActivity.class);
        // Clear the back stack to ensure MainActivity is the only one left
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
