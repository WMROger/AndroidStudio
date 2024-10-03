package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.R;
import java.util.ArrayList;

public class WorkoutModule1 extends Fragment {

    private ArrayList<Exercise> selectedExercises = new ArrayList<>();
    private TextView planWorkoutCounter_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_plan, container, false);

        planWorkoutCounter_txt = view.findViewById(R.id.planWorkoutCounter_txt);

        // Initialize exercise buttons
        ImageButton addItemBtn1 = view.findViewById(R.id.addItemBtn1);
        ImageButton addItemBtn2 = view.findViewById(R.id.addItemBtn2);
        ImageButton addItemBtn3 = view.findViewById(R.id.addItemBtn3);
        ImageButton addItemBtn4 = view.findViewById(R.id.addItemBtn4);
        ImageButton addItemBtn5 = view.findViewById(R.id.addItemBtn5);
        ImageButton addItemBtn6 = view.findViewById(R.id.addItemBtn6);

        // Setup toggle buttons for exercises
        setupToggleButton(addItemBtn1, new Exercise("Bench Press", "Chest exercise", "image_url"));
        setupToggleButton(addItemBtn2, new Exercise("Treadmill", "Cardio exercise", "image_url"));
        setupToggleButton(addItemBtn3, new Exercise("Dead lift", "Back exercise", "image_url"));
        setupToggleButton(addItemBtn4, new Exercise("Plank", "Core exercise", "image_url"));
        setupToggleButton(addItemBtn5, new Exercise("Pull ups", "Back exercise", "image_url"));
        setupToggleButton(addItemBtn6, new Exercise("Bicep Curls", "Arm exercise", "image_url"));

        // Retrieve workout count from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("WorkoutData", getActivity().MODE_PRIVATE);
        int workoutCount = sharedPreferences.getInt("workoutCount", 0);  // Default to 0 if no data found
        planWorkoutCounter_txt.setText("Workouts Created: " + workoutCount);

        // Add button listener for creating workout
        view.findViewById(R.id.FEPAddWorkout).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutModule2.class);
            intent.putParcelableArrayListExtra("selectedExercises", selectedExercises);
            startActivity(intent);
        });

        return view;
    }

    // Setup the toggle button for adding/removing exercises
    private void setupToggleButton(ImageButton button, Exercise exercise) {
        if (selectedExercises.contains(exercise)) {
            button.setImageResource(R.drawable.additem_orange);  // Already selected
            button.setTag(R.drawable.additem_orange);
        } else {
            button.setImageResource(R.drawable.additem_black);  // Not selected
            button.setTag(R.drawable.additem_black);
        }

        button.setOnClickListener(v -> {
            int currentIcon = (int) button.getTag();
            if (currentIcon == R.drawable.additem_black) {
                button.setImageResource(R.drawable.additem_orange);
                button.setTag(R.drawable.additem_orange);
                selectedExercises.add(exercise);  // Add exercise to the list
            } else {
                button.setImageResource(R.drawable.additem_black);
                button.setTag(R.drawable.additem_black);
                selectedExercises.remove(exercise);  // Remove exercise from the list
            }
        });
    }
}
