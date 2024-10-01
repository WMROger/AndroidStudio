package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.heavymetals.R;

import java.util.ArrayList;

public class WorkoutModule1 extends Fragment {
    private Button FEPAddworkout;
    private ImageButton addItemBtn1, addItemBtn2, addItemBtn3, addItemBtn4, addItemBtn5, addItemBtn6;
    private ArrayList<String> selectedExercises = new ArrayList<>();
    private TextView planWorkoutCounter_txt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_plan, container, false);

        planWorkoutCounter_txt = view.findViewById(R.id.planWorkoutCounter_txt);
        FEPAddworkout = view.findViewById(R.id.FEPAddWorkout);

        addItemBtn1 = view.findViewById(R.id.addItemBtn1);
        addItemBtn2 = view.findViewById(R.id.addItemBtn2);
        addItemBtn3 = view.findViewById(R.id.addItemBtn3);
        addItemBtn4 = view.findViewById(R.id.addItemBtn4);
        addItemBtn5 = view.findViewById(R.id.addItemBtn5);
        addItemBtn6 = view.findViewById(R.id.addItemBtn6);

        setupToggleButton(addItemBtn1, "Bench Press");
        setupToggleButton(addItemBtn2, "Treadmill");
        setupToggleButton(addItemBtn3, "Dead lift");
        setupToggleButton(addItemBtn4, "Plank");
        setupToggleButton(addItemBtn5, "Pull ups");
        setupToggleButton(addItemBtn6, "Bicep Curls");

        // Retrieve the workout count saved in Module 4
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("WorkoutData", getActivity().MODE_PRIVATE);
        int workoutCount = sharedPreferences.getInt("workoutCount", 0);  // Default to 0 if no data

        // Update the workout counter text to show the number of workouts in Module 4
        planWorkoutCounter_txt.setText("Workouts Created: " + workoutCount);

        FEPAddworkout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutModule2.class);
            intent.putStringArrayListExtra("selectedExercises", selectedExercises);
            startActivity(intent);
        });

        return view;
    }


    private void setupToggleButton(ImageButton button, String exerciseName) {
        button.setOnClickListener(v -> {
            int currentIcon = (int) button.getTag();

            if (currentIcon == R.drawable.additem_black) {
                // Change the button icon to indicate the item is selected
                button.setImageResource(R.drawable.additem_orange);
                button.setTag(R.drawable.additem_orange);
                selectedExercises.add(exerciseName);  // Add exercise to the list
            } else {
                // Change the button icon to indicate the item is deselected
                button.setImageResource(R.drawable.additem_black);
                button.setTag(R.drawable.additem_black);
                selectedExercises.remove(exerciseName);  // Remove exercise from the list
            }

        });

        button.setTag(R.drawable.additem_black);
    }



}
