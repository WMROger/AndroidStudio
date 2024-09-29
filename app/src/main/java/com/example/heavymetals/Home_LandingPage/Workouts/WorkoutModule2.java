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
    private TextView WM2discard_txt, exerciseNameText, exerciseCategoryText;
    private LinearLayout workoutContainer;
    private HashMap<String, Integer> exerciseIconMap;
    private ImageView exerciseIcon;
    private Integer iconResource;
    private ArrayList<String> selectedExercises;
    private Button addSetButton, WM2AddExercisebtn;
    private ImageButton removeButton;
    private List<Workout> workoutList;
    private EditText workoutNameInput;

    // Define the threshold height in dp
    private static final int THRESHOLD_HEIGHT_DP = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);

        // Retrieve the user email or token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null); // Get the user email
        String authToken = sharedPreferences.getString("auth_token", null); // Get the auth token if needed

        if (userEmail == null || authToken == null) {
            // Handle the case where no user is logged in
            Log.e("WorkoutModule2", "No logged-in user found");
            Toast.makeText(this, "No logged-in user found. Please login.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(WorkoutModule2.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();  // Finish the current activity so the user can't proceed without logging in
            return;  // Stop further execution in this method
        } else {
            Log.d("WorkoutModule2", "Logged-in user: " + userEmail);
        }

        // Initialize layout elements
        workoutNameInput = findViewById(R.id.workout_name_input);
        workoutContainer = findViewById(R.id.workout_container);
        WM2discard_txt = findViewById(R.id.WM2discard_txt);
        WM2AddExercisebtn = findViewById(R.id.WM2AddExercisebtn);

        WM2discard_txt.setOnClickListener(v -> finish());

        WM2AddExercisebtn.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule2.this, Exercises_All.class);
            startActivity(intent);
        });

        // Get the selected exercises passed from another activity
        selectedExercises = getIntent().getStringArrayListExtra("selectedExercises");
        initializeExerciseIconMap();

        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            for (String exercise : selectedExercises) {
                addExercise(exercise);
            }
        }

        checkWorkoutContainerHeight();
    }


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void addExercise(String exerciseName) {
        // Inflate the exercise item layout (assuming you have a layout for each exercise)
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);

        // Find and initialize views within the inflated exercise card
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        TextView exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        ImageButton removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);
        Button addSetButton = exerciseCard.findViewById(R.id.add_set_button);

        // Set exercise name and category
        exerciseNameText.setText(exerciseName);
        exerciseCategoryText.setText(getExerciseCategory(exerciseName));

        // Set exercise icon
        iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon);  // Fallback icon if none found
        }

        // Add click listener to remove the exercise card
        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            checkWorkoutContainerHeight();  // Recalculate the container height after removing
        });

        // Add click listener to add sets
        addSetButton.setOnClickListener(v -> {
            // Inflate new set layout (assuming you have a set item layout)
            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);
            TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);
            EditText repsEditText = newSetLayout.findViewById(R.id.reps_edit_text);

            int currentSetCount = setsContainer.getChildCount();
            setNumberTextView.setText(String.valueOf(currentSetCount + 1));
            repsEditText.setText("10");  // Default reps value

            setsContainer.addView(newSetLayout);  // Add the new set to the container
        });

        // Add the inflated exercise card to the workout container
        workoutContainer.addView(exerciseCard);

        // Recalculate container height after adding the exercise
        checkWorkoutContainerHeight();
    }

    private void checkWorkoutContainerHeight() {
        workoutContainer.post(() -> {
            int containerHeight = workoutContainer.getHeight();
            if (containerHeight > dpToPx(THRESHOLD_HEIGHT_DP)) {
                WM2discard_txt.setText("Save");
                WM2discard_txt.setOnClickListener(v -> {
                    String workoutName = workoutNameInput.getText().toString();
                    if (workoutName.isEmpty()) {
                        workoutName = "Unnamed Workout";
                    }

                    List<Exercise> exerciseList = new ArrayList<>();
                    for (String exerciseName : selectedExercises) {
                        Exercise exercise = new Exercise(exerciseName, 3, 10, false);
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


    private void initializeExerciseIconMap() {
        exerciseIconMap = new HashMap<>();
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon);
        exerciseIconMap.put("Pull ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Dead lift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
    }

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

    // Save workout to SharedPreferences or Database
    private void saveWorkoutForUser(Workout workout) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);  // Retrieve the user email

        if (loggedInUser == null) {
            Log.e("WorkoutModule2", "No logged-in user found");
            Toast.makeText(this, "No logged-in user found. Please login.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(WorkoutModule2.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            Log.d("WorkoutModule2", "Logged-in user: " + loggedInUser);
            // Continue with other logic for logged-in user
        }

    }


    // Save user login information in SharedPreferences
    private void saveUserLogin(String email, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUser", email); // Save the user email
        editor.putString("auth_token", token);   // Save auth token if needed
        editor.apply();
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

            // Retrieve user email and token from SharedPreferences
            SharedPreferences sharedPreferences = activity.getSharedPreferences("user_prefs", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("user_email", null);
            String authToken = sharedPreferences.getString("auth_token", null);

            if (userEmail == null || authToken == null) {
                // If no user is logged in, skip sending the request
                return null;
            }

            try {
                URL url = new URL("http://heavymetals.scarlet2.io/HeavyMetals/workout_save/save_workout.php"); // Corrected URL
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                Gson gson = new Gson();
                String workoutName = workouts[0].getTitle();
                String exercisesJson = gson.toJson(workouts[0].getExercises());

                // Add user authentication details to the POST data
                String postData = "workoutName=" + workoutName + "&exercises=" + exercisesJson + "&user_email=" + userEmail + "&auth_token=" + authToken;

                // Write data to the server
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("SaveWorkoutTask", "Response code: " + responseCode);

                // Check for success (HTTP 200 OK)
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the server response
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d("SaveWorkoutTask", "Server response: " + response.toString());
                } else {
                    // Log any errors from the server
                    Log.e("SaveWorkoutTask", "Failed to save workout. Server responded with: " + responseCode);
                    BufferedReader errorStream = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorStream.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorStream.close();
                    Log.e("SaveWorkoutTask", "Error response from server: " + errorResponse.toString());
                }
            } catch (Exception e) {
                Log.e("SaveWorkoutTask", "Error saving workout to server", e);
            }
            return null;
        }
    }


}
