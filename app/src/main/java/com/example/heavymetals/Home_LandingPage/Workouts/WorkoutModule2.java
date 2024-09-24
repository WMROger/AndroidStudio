package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heavymetals.R;
import java.util.HashMap;

public class WorkoutModule2 extends AppCompatActivity {

    private LinearLayout workoutContainer;
    private HashMap<String, Integer> exerciseIconMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        // Initialize the layout where exercises will be added
        workoutContainer = findViewById(R.id.workout_container);

        // Initialize the exercise icon map (mapping exercise names to drawable resources)
        initializeExerciseIconMap();

        // Adding some exercises dynamically for demonstration
        addExercise("Bench Press", "Chest");
        addExercise("Push ups", "Chest");

        // Handle "Add Exercise" button
        Button addExerciseButton = findViewById(R.id.add_exercise_button);
        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add more exercises as needed
                addExercise("Deadlift", "Back");
            }
        });
    }

    // Function to add an exercise dynamically
    private void addExercise(String exerciseName, String exerciseCategory) {
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_card, workoutContainer, false);

        // Set exercise name and category
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        TextView exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);
        exerciseNameText.setText(exerciseName);
        exerciseCategoryText.setText(exerciseCategory);

        // Set the appropriate image for the exercise
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        Integer iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon); // Use default if not found
        }

        // Add the exercise card to the workout container
        workoutContainer.addView(exerciseCard);
    }

    // Function to initialize the mapping between exercises and their corresponding icons
    private void initializeExerciseIconMap() {
        exerciseIconMap = new HashMap<>();
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon); // Replace with actual drawable names
        exerciseIconMap.put("Push ups", R.drawable.pullup_icon); // Replace with actual drawable names
        exerciseIconMap.put("Deadlift", R.drawable.deadlift_icon); // Replace with actual drawable names
        // Add more exercises as needed
    }
}
