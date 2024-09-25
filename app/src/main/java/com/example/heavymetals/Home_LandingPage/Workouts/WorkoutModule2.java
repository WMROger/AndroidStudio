package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;

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
    private Button addSetButton;
    private ImageButton removeButton;
    private List<Workout> workoutList; // Declaring workoutList
    private EditText workoutNameInput;  // For naming the workout

    // Define the threshold height in dp
    private static final int THRESHOLD_HEIGHT_DP = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);  // Use the correct layout here

        // Initialize the layout where exercises will be added
        workoutNameInput = findViewById(R.id.workout_name_input);  // Workout name input
        workoutContainer = findViewById(R.id.workout_container);
        WM2discard_txt = findViewById(R.id.WM2discard_txt);

        // Set click listener for "Discard" button, initially
        WM2discard_txt.setOnClickListener(v -> finish());

        // Ensure that workoutContainer is not null
        if (workoutContainer == null) {
            throw new NullPointerException("workoutContainer is null. Check your layout.");
        }

        // Get the selected exercises from the intent
        selectedExercises = getIntent().getStringArrayListExtra("selectedExercises");

        // Initialize the exercise icon map
        initializeExerciseIconMap();

        // Add exercises dynamically if any are passed through the intent
        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            for (String exercise : selectedExercises) {
                addExercise(exercise);
            }
        }

        // Check the height and update the button text accordingly
        checkWorkoutContainerHeight();
    }

    // Convert dp to pixels for accurate comparison
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Modify this function to save the workout name and pass it to WorkoutModule4
    private void checkWorkoutContainerHeight() {
        workoutContainer.post(() -> {
            // Get the current height of the workout container
            int containerHeight = workoutContainer.getHeight();

            // Compare it with the threshold
            if (containerHeight > dpToPx(THRESHOLD_HEIGHT_DP)) {
                WM2discard_txt.setText("Save");
                WM2discard_txt.setOnClickListener(v -> {
                    // Get the custom workout name from the input field
                    String workoutName = workoutNameInput.getText().toString();
                    if (workoutName.isEmpty()) {
                        workoutName = "Unnamed Workout";  // Default name if no input
                    }

                    // Convert the selectedExercises (ArrayList<String>) into a list of Exercise objects
                    List<Exercise> exerciseList = new ArrayList<>();

                    // Loop through the selected exercises and convert each to an Exercise object
                    for (String exerciseName : selectedExercises) {
                        // Set default sets, reps, and done status for each exercise
                        Exercise exercise = new Exercise(exerciseName, 3, 10, false);  // Default sets = 3, reps = 10, done = false
                        exerciseList.add(exercise);
                    }

                    // Create a new Workout object to hold the name and exercises
                    Workout newWorkout = new Workout(workoutName, exerciseList.size(), exerciseList);

                    // Create an intent and pass the workout object to WorkoutModule4
                    Intent intent = new Intent(this, WorkoutModule4.class);
                    intent.putExtra("workout", newWorkout);  // Pass the Workout object
                    startActivity(intent);

                });
            } else {
                WM2discard_txt.setText("Discard");
                WM2discard_txt.setOnClickListener(v -> finish());
            }
        });
    }


    // Function to add an exercise dynamically
    private void addExercise(String exerciseName) {
        // Inflate the exercise item view
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);

        // Set the exercise card visibility to VISIBLE once it’s added dynamically
        exerciseCard.setVisibility(View.VISIBLE);

        // Find the relevant views in the card (name, category, icon, etc.)
        addSetButton = exerciseCard.findViewById(R.id.add_set_button);
        exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);

        // Set the exercise name and category based on the input
        exerciseNameText.setText(exerciseName);
        String exerciseCategory = getExerciseCategory(exerciseName);
        exerciseCategoryText.setText(exerciseCategory);

        // Set the appropriate icon for the exercise
        exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon);  // Default icon if not found
        }

        // Set a click listener to remove the exercise card if necessary
        removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            // After removing an exercise, check the height and update the button text
            checkWorkoutContainerHeight();
        });

        // Find the container for sets inside this specific exercise card
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);

        addSetButton.setOnClickListener(v -> {
            // Inflate the new set layout (use the correct set_item_layout)
            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);

            // Find the set number TextView and update it dynamically
            TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);

            // Get the number of existing sets in this specific setsContainer
            int currentSetCount = setsContainer.getChildCount();
            setNumberTextView.setText(String.valueOf(currentSetCount + 1));  // Auto-increment set number

            // Optionally set default values like Reps
            EditText newRepsEditText = newSetLayout.findViewById(R.id.reps_edit_text);
            newRepsEditText.setText("10");  // Default value for reps

            // Add the new set layout to the specific setsContainer for this exercise
            setsContainer.addView(newSetLayout);
        });

        // Add the exercise card to the workout container
        workoutContainer.addView(exerciseCard);

        // After adding an exercise, check the height and update the button text
        checkWorkoutContainerHeight();
    }

    // Function to initialize the mapping between exercises and their corresponding icons
    private void initializeExerciseIconMap() {
        exerciseIconMap = new HashMap<>();
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon);
        exerciseIconMap.put("Pull ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Dead lift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
    }

    // A helper method to return the category for each exercise
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

    // Removed the previous updateSaveOrDiscardText() method since it duplicates functionality with checkWorkoutContainerHeight()

    // Helper method to determine if there are any valid exercises with values
    private boolean hasValidExercises() {
        // Loop through all added exercise cards in the workoutContainer
        for (int i = 0; i < workoutContainer.getChildCount(); i++) {
            View exerciseCard = workoutContainer.getChildAt(i);

            // Ensure that the exerciseCard is not null and contains the reps EditText
            if (exerciseCard != null) {
                EditText repsEditText = exerciseCard.findViewById(R.id.reps_edit_text);

                // Check if the repsEditText is not null
                if (repsEditText != null) {
                    String repsValue = repsEditText.getText().toString();

                    // If any exercise has valid reps or other fields are filled, return true
                    if (repsValue != null && !repsValue.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        // If none of the exercises have valid input, return false
        return false;
    }
}
