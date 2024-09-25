package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.Workout;
import com.example.heavymetals.R;


import android.widget.LinearLayout;


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
                // Dynamically add TextViews for each exercise
                TextView exerciseTextView = new TextView(this);
                exerciseTextView.setText(exercise);
                exercisesContainer.addView(exerciseTextView);
            }
        }
    }
}
