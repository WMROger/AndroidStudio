package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WorkoutModule2 extends AppCompatActivity {
    private TextView WM2discard_txt;
    private LinearLayout workoutContainer;
    private ArrayList<String> selectedExercises;
    private HashMap<String, Integer> exerciseSetsMap = new HashMap<>(); // To store sets for each exercise
    private HashMap<String, Integer> exerciseIconMap = new HashMap<>(); // To store icons for each exercise
    private List<Workout> workoutList;
    private EditText workoutNameInput;

    private static final String TAG = "WorkoutModule2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);

        // Initialize layout elements
        workoutNameInput = findViewById(R.id.workout_name_input);
        workoutContainer = findViewById(R.id.workout_container);
        WM2discard_txt = findViewById(R.id.WM2discard_txt);

        // Initialize the icon map
        initializeExerciseIconMap();

        // Check user login session
        checkUserSession();

        WM2discard_txt.setOnClickListener(v -> finish());

        // Get the selected exercises passed from another activity
        selectedExercises = getIntent().getStringArrayListExtra("selectedExercises");

        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            for (String exercise : selectedExercises) {
                addExercise(exercise);
            }
        }

        checkWorkoutContainerHeight();
    }
    private void checkUserSession() {
        // Access shared preferences to check for user login details
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);

        if (loggedInUser == null) {
            // No logged-in user found, redirect to login
            Toast.makeText(this, "No user session found. Please log in.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } else {
            // User is logged in, you can proceed with the rest of the logic
            Log.d(TAG, "User is logged in: " + loggedInUser);
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();  // Close current activity to prevent going back to it
    }

    // Initialize the exercise icon map
    private void initializeExerciseIconMap() {
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon);  // Replace with your actual icon drawable
        exerciseIconMap.put("Pull ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Dead lift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
    }

    private void addExercise(String exerciseName) {
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);

        // Find and initialize views within the inflated exercise card
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        TextView exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon); // Use the ImageView for the icon
        ImageButton removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);
        Button addSetButton = exerciseCard.findViewById(R.id.add_set_button);

        // Set exercise name and category
        exerciseNameText.setText(exerciseName);
        exerciseCategoryText.setText(getExerciseCategory(exerciseName));

        // Set exercise icon from the map
        Integer iconResource = exerciseIconMap.get(exerciseName);  // Get the icon from the map
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);  // Set the icon if found in the map
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon);  // Fallback icon if not found
        }

        // Add click listener to remove the exercise card
        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            exerciseSetsMap.remove(exerciseName); // Remove the exercise's set data
            checkWorkoutContainerHeight();  // Recalculate the container height after removing
        });

        // Add click listener to add sets
        addSetButton.setOnClickListener(v -> {
            // Check if the exercise is already in the map, if not, initialize it with 0 sets
            if (!exerciseSetsMap.containsKey(exerciseName)) {
                exerciseSetsMap.put(exerciseName, 0);  // Initialize with 0 sets
            }

            // Now it is safe to retrieve the current set count
            int currentSetCount = exerciseSetsMap.get(exerciseName);  // This will not be null
            currentSetCount += 1;
            exerciseSetsMap.put(exerciseName, currentSetCount); // Update the set count

            // Inflate a new set item layout and set its values
            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);
            TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);
            EditText repsEditText = newSetLayout.findViewById(R.id.reps_edit_text);

            setNumberTextView.setText(String.valueOf(currentSetCount));
            repsEditText.setText("10");  // Default reps value

            setsContainer.addView(newSetLayout);  // Add the new set to the container
        });

        workoutContainer.addView(exerciseCard);
        checkWorkoutContainerHeight();
    }


    private void checkWorkoutContainerHeight() {
        workoutContainer.post(() -> {
            int containerHeight = workoutContainer.getHeight();
            if (containerHeight > dpToPx(100)) {
                WM2discard_txt.setText("Save");
                WM2discard_txt.setOnClickListener(v -> {
                    String workoutName = workoutNameInput.getText().toString();
                    if (workoutName.isEmpty()) {
                        workoutName = "Unnamed Workout";
                    }

                    List<Exercise> exerciseList = new ArrayList<>();
                    for (String exerciseName : selectedExercises) {
                        int sets = exerciseSetsMap.getOrDefault(exerciseName, 0);
                        Exercise exercise = new Exercise(exerciseName, sets, 10, false);  // Default reps value is 10
                        exerciseList.add(exercise);
                    }

                    Workout newWorkout = new Workout(workoutName, exerciseList.size(), exerciseList);

                    // Save workout locally and send to server
                    saveWorkoutForUser(newWorkout);

                    // After saving, navigate to another activity
                    Intent intent = new Intent(this, WorkoutModule4.class);
                    intent.putExtra("workout", newWorkout);
                    startActivity(intent);
                });
            } else {
                WM2discard_txt.setText("Discard");
                WM2discard_txt.setOnClickListener(v -> finish());
            }
        });
    }

    // Save workout to SharedPreferences or send it to the server
    private void saveWorkoutForUser(Workout workout) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);  // Retrieve the user email
        if (loggedInUser == null) {
            Log.e(TAG, "No logged-in user found");
            Toast.makeText(this, "No logged-in user found. Please login.", Toast.LENGTH_LONG).show();
            redirectToLogin();
        } else {
            // If user is logged in, continue saving workout
            new SendWorkoutTask(this).execute(workout);
        }
    }

    // Converts dp (density-independent pixels) to px (pixels)
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Get exercise category based on the exercise name
    private String getExerciseCategory(String exerciseName) {
        switch (exerciseName) {
            case "Bench Press":
                return "Chest";
            case "Pull ups":
                return "Upper Body";
            case "Dead lift":
                return "Back";
            case "Treadmill":
                return "Cardio";
            case "Plank":
                return "Core";
            case "Bicep Curls":
                return "Arms";
            default:
                return "General";
        }
    }

    private static class SendWorkoutTask extends AsyncTask<Workout, Void, Void> {
        private WeakReference<WorkoutModule2> activityReference;

        SendWorkoutTask(WorkoutModule2 activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Workout... workouts) {
            WorkoutModule2 activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            SharedPreferences sharedPreferences = activity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("loggedInUser", null);
            String authToken = sharedPreferences.getString("auth_token", null);

            if (userEmail == null || authToken == null) {
                Log.e(TAG, "No user session found, skipping workout save");
                return null;
            }

            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/workout_save/save_workout.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                Gson gson = new Gson();
                String workoutName = workouts[0].getTitle();
                String exercisesJson = gson.toJson(workouts[0].getExercises());

                String postData = "workoutName=" + workoutName + "&exercises=" + exercisesJson + "&user_email=" + userEmail + "&auth_token=" + authToken;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.d(TAG, "Server response: " + response.toString());
                } else {
                    Log.e(TAG, "Failed to save workout, response code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error saving workout to server", e);
            }
            return null;
        }
    }
}
