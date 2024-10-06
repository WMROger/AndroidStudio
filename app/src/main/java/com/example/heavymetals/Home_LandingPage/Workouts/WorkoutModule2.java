package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.heavymetals.Models.Adapters.Workout;

import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutModule2 extends AppCompatActivity {
    private TextView WM2discard_txt;
    private LinearLayout workoutContainer;
    private ArrayList<AdaptersExercise> selectedAdaptersExercises;
    private HashMap<String, Integer> exerciseSetsMap = new HashMap<>();
    private HashMap<String, Integer> exerciseIconMap = new HashMap<>();
    private EditText workoutNameInput;
    private Button WM2AddExercisebtn;
    private static final String TAG = "WorkoutModule2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);

        // Initialize layout elements
        workoutNameInput = findViewById(R.id.workout_name_input);
        workoutContainer = findViewById(R.id.workout_container);
        WM2discard_txt = findViewById(R.id.WM2discard_txt);
        WM2AddExercisebtn = findViewById(R.id.WM2AddExercisebtn);


        // Initialize the icon map
        initializeExerciseIconMap();

        // Check user login session
        checkUserSession();


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

                // Set workoutId as 0 initially since this is a new workout
                Workout newWorkout = new Workout(0, workoutName, adaptersExerciseList);

                // Save the workout ID to SharedPreferences for future use
                saveWorkoutIdToPreferences(newWorkout.getWorkoutId());

                saveWorkoutForUser(newWorkout);

                Intent intent = new Intent(this, WorkoutModule4.class);
                intent.putExtra("workout", newWorkout);
                startActivity(intent);

            } else {
                finish();  // Discard changes
            }
        });
        WM2AddExercisebtn.setOnClickListener(view -> {
            finish();
        });

        checkWorkoutContainerHeight();
    }

    private void saveWorkoutIdToPreferences(int workoutId) {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_workout_id", workoutId);  // Save the workout ID
        editor.apply();  // Commit the changes
    }

    // Method to convert Exercise to AdaptersExercise
    private AdaptersExercise convertToAdaptersExercise(Exercise exercise) {
        return new AdaptersExercise(exercise.getName(), 1, 10, false);
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
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon);
        exerciseIconMap.put("Pull ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Dead lift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
    }

    private void addExercise(String exerciseName) {
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        ImageButton removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);
        Button addSetButton = exerciseCard.findViewById(R.id.add_set_button);

        // Set the exercise name and icon
        exerciseNameText.setText(exerciseName);
        Integer iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon);  // Default icon if exercise is not found
        }

        // Add logic to remove an exercise
        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            exerciseSetsMap.remove(exerciseName);
            checkWorkoutContainerHeight();
        });

        // Automatically add 1 set when an exercise is added
        exerciseSetsMap.put(exerciseName, 1);  // Set the default set count to 1
        addSetToContainer(setsContainer, 1);  // Add 1 set to the container

        // Button to add more sets
        addSetButton.setOnClickListener(v -> {
            int currentSetCount = exerciseSetsMap.getOrDefault(exerciseName, 0);
            currentSetCount += 1;
            exerciseSetsMap.put(exerciseName, currentSetCount);
            addSetToContainer(setsContainer, currentSetCount);
        });

        workoutContainer.addView(exerciseCard);
        checkWorkoutContainerHeight();
    }

    // Helper method to add a set layout to the container
    private void addSetToContainer(LinearLayout setsContainer, int setCount) {
        View newSetLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);
        TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);
        TextView repsTextView = newSetLayout.findViewById(R.id.reps_edit_text);  // This was previously an EditText, now it's a TextView.

        setNumberTextView.setText(String.valueOf(setCount));  // Display the set number
        repsTextView.setText("10");  // Set default reps to 10

        setsContainer.addView(newSetLayout);
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
            // Call SendWorkoutTask to send workout to server
            new SendWorkoutTask(this).execute(workout);
        }
    }
}
