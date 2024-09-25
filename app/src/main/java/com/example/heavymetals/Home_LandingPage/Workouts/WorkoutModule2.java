package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutModule2 extends AppCompatActivity {
    private TextView WM2discard_txt, exerciseNameText, exerciseCategoryText;
    private LinearLayout workoutContainer;
    private HashMap<String, Integer> exerciseIconMap;
    private ImageView exerciseIcon;
    private Integer iconResource;
    private ArrayList<String> selectedExercises;
    private Button addSetButton;
    private ImageButton removeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);  // Use the correct layout here

        // Initialize the layout where exercises will be added
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

        // Update the button text depending on whether exercises are present
        updateSaveOrDiscardText();
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

        // Set the default reps for the exercise
        EditText repsEditText = exerciseCard.findViewById(R.id.reps_edit_text);
        repsEditText.setText("10");  // Default reps value

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
            updateSaveOrDiscardText(); // Update the button text after removal
        });

        // Find the container for sets inside this specific exercise card
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);

        // Set a click listener to add new sets
        addSetButton.setOnClickListener(v -> {
            // Inflate a new set layout
            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.exercise_item, setsContainer, false);

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

        // Update the button text since we added an exercise
        updateSaveOrDiscardText();
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

    // Helper function to update the button text based on the content of the workout container
    private void updateSaveOrDiscardText() {
        int childCount = workoutContainer.getChildCount();
        Log.d("WorkoutModule2", "Child count: " + childCount);

        if (childCount > 0) {
            WM2discard_txt.setText("Save");
            WM2discard_txt.setOnClickListener(v -> {
                Intent intent = new Intent(WorkoutModule2.this, WorkoutModule3.class);
                startActivity(intent);
            });
        } else {
            WM2discard_txt.setText("Discard");
            WM2discard_txt.setOnClickListener(v -> finish());
        }
    }

}
