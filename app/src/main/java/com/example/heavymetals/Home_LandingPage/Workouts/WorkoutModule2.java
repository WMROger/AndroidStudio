package com.example.heavymetals.Home_LandingPage.Workouts;

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

import com.example.heavymetals.R;

import java.util.ArrayList;
import java.util.HashMap;

public class WorkoutModule2 extends AppCompatActivity {
    private TextView exerciseNameText, exerciseCategoryText;
    private LinearLayout workoutContainer;
    private HashMap<String, Integer> exerciseIconMap;
    private ImageView exerciseIcon;
    private Integer iconResource;
    private ArrayList<String> selectedExercises;
    private Button addSetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);  // Use the correct layout here

        // Initialize the layout where exercises will be added
        workoutContainer = findViewById(R.id.workout_container);

        // Ensure that workoutContainer is not null
        if (workoutContainer == null) {
            throw new NullPointerException("workoutContainer is null. Check your layout.");
        }

        // Get the selected exercises from the intent
        selectedExercises = getIntent().getStringArrayListExtra("selectedExercises");

        // Initialize the exercise icon map
        initializeExerciseIconMap();

        // Add exercises dynamically based on the selected exercises passed from WorkoutModule1
        if (selectedExercises != null) {
            for (String exercise : selectedExercises) {
                addExercise(exercise);
            }
        }
    }

    // Function to add an exercise dynamically
    // Function to add an exercise dynamically
    private void addExercise(String exerciseName) {
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);

        // Set exercise name and category (dummy category for now)
        addSetButton = exerciseCard.findViewById(R.id.add_set_button);
        exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);
        exerciseNameText.setText(exerciseName);



        // Find the reps EditText (you will need to add this in your XML layout for each set)
        EditText repsEditText = exerciseCard.findViewById(R.id.reps_edit_text); // Assuming you added an EditText in the layout
        repsEditText.setText("10"); // Set default reps to 10

        // You can later retrieve the reps input from the EditText when needed
        String reps = repsEditText.getText().toString();

        // Set the click listener to add a new set when clicked
        addSetButton.setOnClickListener(v -> {
            // Inflate a new set layout
            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);

            // Update the set number dynamically (e.g., Set 3, Set 4)
            TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);
            int currentSetNumber = workoutContainer.getChildCount(); // This assumes each child is a set
            setNumberTextView.setText(String.valueOf(currentSetNumber + 1));

            // Add the new set layout to the exercise card
            workoutContainer.addView(newSetLayout);
        });

        // Assign a dummy category based on exercise name
        String exerciseCategory = getExerciseCategory(exerciseName);
        exerciseCategoryText.setText(exerciseCategory);

        // Set the appropriate image for the exercise
        exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon); // Use default if not found
        }

        // Set the click listener for the remove button
        ImageButton removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        removeButton.setOnClickListener(v -> workoutContainer.removeView(exerciseCard));

        // Add the exercise card to the workout container
        workoutContainer.addView(exerciseCard);
    }


    // Function to initialize the mapping between exercises and their corresponding icons
    private void initializeExerciseIconMap() {
        exerciseIconMap = new HashMap<>();
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon);
        exerciseIconMap.put("Push ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Deadlift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
    }

    // A helper method to return the category for each exercise
    private String getExerciseCategory(String exerciseName) {
        switch (exerciseName) {
            case "Bench Press":
            case "Push ups":
                return "Chest";
            case "Deadlift":
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
}
