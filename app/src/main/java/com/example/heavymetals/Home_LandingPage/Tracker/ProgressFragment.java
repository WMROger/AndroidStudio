package com.example.heavymetals.Home_LandingPage.Tracker;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.heavymetals.Home_LandingPage.HomeFragment;
import com.example.heavymetals.R;
import com.google.android.material.button.MaterialButton;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ProgressBar;

public class ProgressFragment extends Fragment {

    private Button addScheduleButton, sun, mon, tue, wed, thu, fri, sat, AddGoal;
    private View scheduleContainer;
    private TextView emptyScheduleText, trackerBack, SaveGoals;
    private LinearLayout goalContainer;
    private ImageView emptyScheduleIcon;
    private ProgressBar progressCircle;
    private int goalCount = 0;
    private final int MAX_GOALS = 5;

    private SharedPreferences sharedPreferences;

    // State to track if the buttons are toggled
    private boolean isSunToggled = false;
    private boolean isMonToggled = false;
    private boolean isTueToggled = false;
    private boolean isWedToggled = false;
    private boolean isThuToggled = false;
    private boolean isFriToggled = false;
    private boolean isSatToggled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("Goals", Context.MODE_PRIVATE);

        // Initialize views
        addScheduleButton = view.findViewById(R.id.btn_add_schedule);
        scheduleContainer = view.findViewById(R.id.schedule_container);
        emptyScheduleText = view.findViewById(R.id.tv_empty_schedule);
        emptyScheduleIcon = view.findViewById(R.id.iv_empty_schedule_icon);
        trackerBack = view.findViewById(R.id.tracker_back);
        goalContainer = view.findViewById(R.id.goal_container);
        AddGoal = view.findViewById(R.id.btn_add_goal);
        SaveGoals = view.findViewById(R.id.tv_save); // Save button
        progressCircle = view.findViewById(R.id.progress_circle); // Assuming you added a ProgressBar in your layout

        sun = view.findViewById(R.id.tv_sunday);
        mon = view.findViewById(R.id.tv_monday);
        tue = view.findViewById(R.id.tv_tuesday);
        wed = view.findViewById(R.id.tv_wednesday);
        thu = view.findViewById(R.id.tv_thursday);
        fri = view.findViewById(R.id.tv_friday);
        sat = view.findViewById(R.id.tv_saturday);

        // Initially, hide the schedule container
        scheduleContainer.setVisibility(View.GONE);
        progressCircle.setVisibility(View.GONE); // Hide progress circle initially

        // Load previously saved goals
        loadGoals();

        // Handle "Add Schedule" button click
        addScheduleButton.setOnClickListener(v -> {
            addScheduleButton.setVisibility(View.GONE);
            emptyScheduleText.setVisibility(View.GONE);
            emptyScheduleIcon.setVisibility(View.GONE);
            scheduleContainer.setVisibility(View.VISIBLE);
        });

        // Handle "Add Goal" button click to add a new goal dynamically
        AddGoal.setOnClickListener(v -> addNewGoal());

        // Handle "Save" button click to save goals
        SaveGoals.setOnClickListener(v -> {
            saveGoals();
            displayProgressCircle(); // Show the progress circle when saving
        });

        return view;
    }

    // Method to add a new goal dynamically
    private void addNewGoal() {
        if (goalCount >= MAX_GOALS) {
            Toast.makeText(requireContext(), "Maximum goal limit reached", Toast.LENGTH_SHORT).show();
            return;
        }

        goalCount++;  // Increment the goal count

        // Create a new LinearLayout for the goal row
        LinearLayout goalRow = new LinearLayout(requireContext());
        goalRow.setOrientation(LinearLayout.HORIZONTAL);
        goalRow.setPadding(8, 8, 8, 8);

        // Create a TextView for the goal number
        TextView goalNumber = new TextView(requireContext());
        goalNumber.setText(String.valueOf(goalCount));
        goalNumber.setTextSize(16);
        goalNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        goalNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create an EditText for goal input
        EditText goalInput = new EditText(requireContext());
        goalInput.setHint("Add goal here");
        goalInput.setTextSize(16);
        goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        goalInput.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        goalInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));

        // Create a Delete button
        TextView deleteButton = new TextView(requireContext());
        deleteButton.setText("Delete");
        deleteButton.setTextSize(16);
        deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_orange));
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Handle deleting the specific goal row
        deleteButton.setOnClickListener(v -> {
            goalContainer.removeView(goalRow);
            goalCount--;
            updateGoalNumbers();
        });

        // Add the TextView, EditText, and Delete button to the goal row
        goalRow.addView(goalNumber);
        goalRow.addView(goalInput);
        goalRow.addView(deleteButton);

        // Add the newly created row to the goal container
        goalContainer.addView(goalRow);
    }

//    // Method to save the goals in SharedPreferences
//    private void saveGoals() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.clear(); // Clear previous data
//
//        for (int i = 0; i < goalContainer.getChildCount(); i++) {
//            View goalRow = goalContainer.getChildAt(i);
//            if (goalRow instanceof LinearLayout) {
//                EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1); // EditText is the second child
//                editor.putString("goal_" + i, goalInput.getText().toString()); // Save the goal text
//            }
//        }
//        editor.apply();
//        Toast.makeText(requireContext(), "Goals saved!", Toast.LENGTH_SHORT).show();
//    }
    // Method to save the goals in SharedPreferences
    private void saveGoals() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear previous data

        // Disable the Add button, Save button, and set goals uneditable
        AddGoal.setVisibility(View.GONE); // Hide Add button
        SaveGoals.setVisibility(View.GONE); // Hide Save button

        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                // Disable EditText and change Delete to Done
                EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1); // EditText is the second child
                TextView deleteButton = (TextView) ((LinearLayout) goalRow).getChildAt(2); // Delete button is the third child

                // Save the goal text
                editor.putString("goal_" + i, goalInput.getText().toString());

                // Make EditText uneditable
                goalInput.setEnabled(false);
                goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // Set text color to white

                // Change Delete button to Done
                deleteButton.setText("Done");
                deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            }
        }

        editor.apply(); // Apply changes to SharedPreferences
        Toast.makeText(requireContext(), "Goals saved!", Toast.LENGTH_SHORT).show();

        // Show progress circle after saving
        displayProgressCircle();
    }

    // Method to load the saved goals from SharedPreferences
    private void loadGoals() {
        goalCount = 0; // Reset goal count
        goalContainer.removeAllViews(); // Clear all views

        for (int i = 0; i < MAX_GOALS; i++) {
            String goalText = sharedPreferences.getString("goal_" + i, null);
            if (goalText != null) {
                goalCount++;
                // Add the goal row back
                addNewGoal();
                View goalRow = goalContainer.getChildAt(goalCount - 1);
                if (goalRow instanceof LinearLayout) {
                    EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1);
                    goalInput.setText(goalText); // Set the saved goal text
                }
            }
        }
    }

    // Method to display the progress circle when the user saves goals
    private void displayProgressCircle() {
        progressCircle.setVisibility(View.VISIBLE);
        progressCircle.setProgress(100); // You can adjust this based on actual progress
    }

    // Update goal numbers after deleting
    private void updateGoalNumbers() {
        int count = 1;
        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                TextView goalNumber = (TextView) ((LinearLayout) goalRow).getChildAt(0);
                goalNumber.setText(String.valueOf(count));
                count++;
            }
        }
    }

    private void handleBackAction() {
        if (scheduleContainer.getVisibility() == View.VISIBLE) {
            scheduleContainer.setVisibility(View.GONE);
            addScheduleButton.setVisibility(View.VISIBLE);
            emptyScheduleText.setVisibility(View.VISIBLE);
            emptyScheduleIcon.setVisibility(View.VISIBLE);
        } else {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
