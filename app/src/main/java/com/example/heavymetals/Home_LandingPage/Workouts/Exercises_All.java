package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.R;

import java.util.ArrayList;

public class Exercises_All extends AppCompatActivity {

    private Button FEPAddExercise;
    private ImageButton addItemBtn1, addItemBtn2, addItemBtn3, addItemBtn4, addItemBtn5, addItemBtn6;
    private ArrayList<String> selectedExercises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_all);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        FEPAddExercise = findViewById(R.id.FEPAddExercise);

        addItemBtn1 = findViewById(R.id.addItemBtn1);
        addItemBtn2 = findViewById(R.id.addItemBtn2);
        addItemBtn3 = findViewById(R.id.addItemBtn3);
        addItemBtn4 = findViewById(R.id.addItemBtn4);
        addItemBtn5 = findViewById(R.id.addItemBtn5);
        addItemBtn6 = findViewById(R.id.addItemBtn6);

        // Setup toggle functionality for each button
        setupToggleButton(addItemBtn1, "Bench Press");
        setupToggleButton(addItemBtn2, "Treadmill");
        setupToggleButton(addItemBtn3, "Deadlift");
        setupToggleButton(addItemBtn4, "Plank");
        setupToggleButton(addItemBtn5, "Pull ups");
        setupToggleButton(addItemBtn6, "Bicep Curls");

        // Set click listener for Add Exercise button
        FEPAddExercise.setOnClickListener(v -> {
            Intent intent = new Intent(Exercises_All.this, WorkoutModule2.class);
            intent.putStringArrayListExtra("selectedExercises", selectedExercises);  // Pass selected exercises
            startActivity(intent);
        });
    }

    // Method to toggle the state of the add button and add/remove exercises
    private void setupToggleButton(ImageButton button, String exerciseName) {
        button.setOnClickListener(v -> {
            int currentIcon = (int) button.getTag();

            if (currentIcon == R.drawable.additem_black) {
                button.setImageResource(R.drawable.additem_orange);  // Change to selected state
                button.setTag(R.drawable.additem_orange);
                selectedExercises.add(exerciseName);  // Add exercise to list
            } else {
                button.setImageResource(R.drawable.additem_black);  // Change to deselected state
                button.setTag(R.drawable.additem_black);
                selectedExercises.remove(exerciseName);  // Remove exercise from list
            }
        });

        // Initially set to black icon (unselected)
        button.setTag(R.drawable.additem_black);
    }
}
