package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AddWorkoutActivity extends AppCompatActivity {

    private LinearLayout workoutContainer;
    private HashMap<String, Integer> exerciseIconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        workoutContainer = findViewById(R.id.workout_container);

        // Initialize a map for exercise names to corresponding icons
        initializeExerciseIconMap();

        // Example: Adding an exercise dynamically based on user selection
        addExercise("Bench Press", "Chest");
        addExercise("Push ups", "Chest");

        // Add Exercise button functionality (for adding more exercises)
        Button addExerciseBtn = findViewById(R.id.add_exercise_button);
        addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logic for adding more exercises, you might open a dialog to select the exercise
                // For example, let's assume the user selected Deadlift
                addExercise("Deadlift", "Lower Back");
            }
        });
    }

    // Function to dynamically add an exercise card
    private void addExercise(String exerciseName, String category) {
        View exerciseCard = getLayoutInflater().inflate(R.layout.exercise_card, workoutContainer, false);

        // Set exercise name
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        exerciseNameText.setText(exerciseName);

        // Set exercise category
        TextView exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);
        exerciseCategoryText.setText(category);

        // Set exercise icon based on the exercise name
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        Integer iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon); // Default icon if none is found
        }

        // Finally, add the card to the workout container
        workoutContainer.addView(exerciseCard);
    }

    // Initialize the mapping between exercise names and their corresponding icons
    private void initializeExerciseIconMap() {
        exerciseIconMap = new HashMap<>();
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon); // assuming you have these icons in drawable
        exerciseIconMap.put("Pull ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Deadlift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
        // Add more exercises and their corresponding drawables
    }
}
