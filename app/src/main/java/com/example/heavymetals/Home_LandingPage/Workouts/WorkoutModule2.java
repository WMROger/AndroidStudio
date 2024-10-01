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
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;
import com.google.gson.Gson;

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
    private ArrayList<AdaptersExercise> selectedAdaptersExercises; // Now matching the AdaptersExercise
    private HashMap<String, Integer> exerciseSetsMap = new HashMap<>();
    private HashMap<String, Integer> exerciseIconMap = new HashMap<>();
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
        ArrayList<Exercise> selectedExercises = (ArrayList<Exercise>) getIntent().getSerializableExtra("selectedExercises");

        // Convert each Exercise to AdaptersExercise
        selectedAdaptersExercises = new ArrayList<>();
        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            for (Exercise exercise : selectedExercises) {
                AdaptersExercise adaptersExercise = convertToAdaptersExercise(exercise);
                selectedAdaptersExercises.add(adaptersExercise);
                addExercise(adaptersExercise.getName());
            }
        }

        // Save or discard logic
        WM2discard_txt.setOnClickListener(v -> {
            if (WM2discard_txt.getText().toString().equals("Save")) {
                String workoutName = workoutNameInput.getText().toString();
                if (workoutName.isEmpty()) {
                    workoutName = "Unnamed Workout";
                }

                List<AdaptersExercise> adaptersExerciseList = new ArrayList<>();
                for (AdaptersExercise adaptersExercise : selectedAdaptersExercises) {
                    int sets = exerciseSetsMap.getOrDefault(adaptersExercise.getName(), 0);
                    adaptersExercise.setSets(sets);
                    adaptersExercise.setReps(10);  // Default reps value
                    adaptersExerciseList.add(adaptersExercise);
                }

                Workout newWorkout = new Workout(workoutName, adaptersExerciseList);
                saveWorkoutForUser(newWorkout);

                Intent intent = new Intent(this, WorkoutModule4.class);
                intent.putExtra("workout", newWorkout);
                startActivity(intent);
            } else {
                finish();  // Discard changes
            }
        });

        checkWorkoutContainerHeight();
    }

    // Method to convert Exercise to AdaptersExercise
    private AdaptersExercise convertToAdaptersExercise(Exercise exercise) {
        // For now, assign default values for sets and reps
        return new AdaptersExercise(exercise.getName(), 0, 10, false);
    }

    private void checkUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);

        if (loggedInUser == null) {
            Toast.makeText(this, "No user session found. Please log in.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } else {
            Log.d(TAG, "User is logged in: " + loggedInUser);
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        TextView exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        ImageButton removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);
        Button addSetButton = exerciseCard.findViewById(R.id.add_set_button);

        exerciseNameText.setText(exerciseName);

        Integer iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon);
        }

        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            exerciseSetsMap.remove(exerciseName);
            checkWorkoutContainerHeight();
        });

        addSetButton.setOnClickListener(v -> {
            if (!exerciseSetsMap.containsKey(exerciseName)) {
                exerciseSetsMap.put(exerciseName, 0);
            }

            int currentSetCount = exerciseSetsMap.get(exerciseName);
            currentSetCount += 1;
            exerciseSetsMap.put(exerciseName, currentSetCount);

            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);
            TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);
            EditText repsEditText = newSetLayout.findViewById(R.id.reps_edit_text);

            setNumberTextView.setText(String.valueOf(currentSetCount));
            repsEditText.setText("10");

            setsContainer.addView(newSetLayout);
        });

        workoutContainer.addView(exerciseCard);
        checkWorkoutContainerHeight();
    }

    private void checkWorkoutContainerHeight() {
        workoutContainer.post(() -> {
            int containerHeight = workoutContainer.getHeight();
            if (containerHeight > dpToPx(100)) {
                WM2discard_txt.setText("Save");
            } else {
                WM2discard_txt.setText("Discard");
                WM2discard_txt.setOnClickListener(v -> finish());
            }
        });
    }

    // Utility method for converting dp to pixels
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void saveWorkoutForUser(Workout workout) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (loggedInUser == null) {
            Toast.makeText(this, "No logged-in user found. Please login.", Toast.LENGTH_LONG).show();
            redirectToLogin();
        } else {
            new SendWorkoutTask(this).execute(workout);
        }
    }

    private static class SendWorkoutTask extends AsyncTask<Workout, Void, Boolean> {
        private WeakReference<WorkoutModule2> activityReference;

        SendWorkoutTask(WorkoutModule2 activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(Workout... workouts) {
            WorkoutModule2 activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return false;

            SharedPreferences sharedPreferences = activity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("loggedInUser", null);
            String authToken = sharedPreferences.getString("auth_token", null);

            if (userEmail == null || authToken == null) {
                return false;
            }

            try {
                URL url = new URL("https://your-server-url.com/HeavyMetals/exercise_save/add_exercise.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                Gson gson = new Gson();
                String workoutName = workouts[0].getTitle();
                String exercisesJson = gson.toJson(workouts[0].getExercises());

                String postData = "action=add&workoutName=" + workoutName + "&exercises=" + exercisesJson + "&user_email=" + userEmail + "&auth_token=" + authToken;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                Log.e(TAG, "Error saving workout to server", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            WorkoutModule2 activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (!success) {
                Toast.makeText(activity, "Failed to save workout. Try again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
