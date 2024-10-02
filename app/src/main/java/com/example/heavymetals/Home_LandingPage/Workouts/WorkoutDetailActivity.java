package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.R;
import java.util.ArrayList;

public class WorkoutDetailActivity extends AppCompatActivity {

    private LinearLayout exercisesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        // Find the LinearLayout inside the ScrollView
        exercisesContainer = findViewById(R.id.exercises_linear_layout);

        // Get the list of Exercise objects from the intent
        ArrayList<AdaptersExercise> adaptersExerciseList = (ArrayList<AdaptersExercise>) getIntent().getSerializableExtra("exercises");

        if (adaptersExerciseList != null && !adaptersExerciseList.isEmpty()) {
            // Log the exercises to ensure the data is being passed
            Log.d("WorkoutDetailActivity", "Exercises: " + adaptersExerciseList.toString());

            // Loop through the exercises and display each one
            for (AdaptersExercise adaptersExercise : adaptersExerciseList) {
                // Add exercise details and checkbox to the container
                addExerciseToContainer(adaptersExercise);
            }
        } else {
            // Log if no exercises were passed
            Log.e("WorkoutDetailActivity", "No exercises passed to the activity.");
        }
    }

    // Helper method to add an exercise to the LinearLayout container
    private void addExerciseToContainer(AdaptersExercise adaptersExercise) {
        // Create and add the TextView for the exercise details
        TextView exerciseTextView = new TextView(this);
        exerciseTextView.setText(formatExerciseDetails(adaptersExercise));
        exerciseTextView.setTextColor(getResources().getColor(R.color.white));
        exerciseTextView.setTextSize(18);
        exerciseTextView.setPadding(32, 8, 32, 8);

        // Add margins to the TextView
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16); // Adding vertical margins between exercises
        exerciseTextView.setLayoutParams(layoutParams);

        // Add the exercise details TextView to the container
        exercisesContainer.addView(exerciseTextView);

        // Create and add the CheckBox for the "done" status
        CheckBox doneCheckbox = new CheckBox(this);
        doneCheckbox.setChecked(adaptersExercise.isDone());
        doneCheckbox.setText("Completed");
        doneCheckbox.setButtonTintList(getResources().getColorStateList(R.color.white));
        doneCheckbox.setTextColor(getResources().getColor(R.color.white));

        // Add margins to the CheckBox
        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkboxParams.setMargins(0, 8, 0, 55); // Adding margins below the checkbox
        doneCheckbox.setLayoutParams(checkboxParams);

        // Optionally, update the exercise "isDone" status on checkbox click
        doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adaptersExercise.setDone(isChecked);
        });

        // Add the CheckBox to the container
        exercisesContainer.addView(doneCheckbox);
    }

    // Helper method to format exercise details
    private String formatExerciseDetails(AdaptersExercise exercise) {
        return "Exercise: " + exercise.getName() +
                "\nSets: " + exercise.getSets() +
                "\nReps: " + exercise.getReps() +
                "\nDone: " + (exercise.isDone() ? "Yes" : "No");
    }
}
