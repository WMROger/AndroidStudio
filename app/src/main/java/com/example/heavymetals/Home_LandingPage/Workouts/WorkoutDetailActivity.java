package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

import java.util.ArrayList;

public class WorkoutDetailActivity extends AppCompatActivity {

    private LinearLayout exercisesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        exercisesContainer = findViewById(R.id.exercises_container);

        // Get the list of exercises from the intent
        ArrayList<String> exercises = getIntent().getStringArrayListExtra("exercises");

        if (exercises != null && !exercises.isEmpty()) {
            for (String exercise : exercises) {
                // Dynamically create TextViews for each exercise and add them to the container
                TextView exerciseTextView = new TextView(this);
                exerciseTextView.setText(exercise);

                // Optionally, set some layout params or text style
                exerciseTextView.setTextSize(18);  // Set text size
                exerciseTextView.setPadding(16, 16, 16, 16);  // Add some padding

                // Add the TextView to the exercises container
                exercisesContainer.addView(exerciseTextView);
            }
        }
    }
}
