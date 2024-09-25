package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.R;

import java.util.ArrayList;

public class WorkoutDetailActivity extends AppCompatActivity {

    private LinearLayout exercisesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        exercisesContainer = findViewById(R.id.exercises_container);

        // Get the list of Exercise objects from the intent
        ArrayList<Exercise> exerciseList = (ArrayList<Exercise>) getIntent().getSerializableExtra("exercises");

        if (exerciseList != null && !exerciseList.isEmpty()) {
            // Log the exercises to ensure the data is being passed
            Log.d("WorkoutDetailActivity", "Exercises: " + exerciseList.toString());

            // Loop through the exercises and display each one with sets, reps, and done status
            for (Exercise exercise : exerciseList) {
                // Dynamically create a TextView for each exercise and add it to the container
                TextView exerciseTextView = new TextView(this);

                // Format the text to show the exercise name, sets, reps, and done status
                String exerciseDetails = "Exercise: " + exercise.getName() +
                        "\nSets: " + exercise.getSets() +
                        "\nReps: " + exercise.getReps() +
                        "\nDone: " + (exercise.isDone() ? "Yes" : "No");

                exerciseTextView.setText(exerciseDetails);

                // Optionally, style the TextView for each exercise
                exerciseTextView.setTextSize(18);
                exerciseTextView.setPadding(32, 8, 32, 8);

                // Add the exercise details TextView to the container
                exercisesContainer.addView(exerciseTextView);

                // Add a checkbox for the "done" status
                CheckBox doneCheckbox = new CheckBox(this);
                doneCheckbox.setChecked(exercise.isDone());
                doneCheckbox.setText("Completed");
                exercisesContainer.addView(doneCheckbox);
            }
        } else {
            // Log if no exercises were passed
            Log.e("WorkoutDetailActivity", "No exercises passed to the activity.");
        }
    }
}
