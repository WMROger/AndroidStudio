package com.example.heavymetals.Home_LandingPage.Tracker;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.heavymetals.Home_LandingPage.HomeFragment;
import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutModule4;
import com.example.heavymetals.R;

import android.content.Context;
import android.content.SharedPreferences;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ProgressFragment extends Fragment {

    private Button addScheduleButton,addWorkout, sun, mon, tue, wed, thu, fri, sat, AddGoal;
    private View scheduleContainer;
    private TextView emptyScheduleText, trackerBack, SaveGoals;
    private LinearLayout goalContainer;
    private ImageView emptyScheduleIcon;
    private int goalCount = 0;
    private int doneCount = 0; // Track how many goals are marked as done
    private final int MAX_GOALS = 5;
    private MaterialProgressBar progressCircle;


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
        addWorkout = view.findViewById(R.id.btn_add_workout);
        scheduleContainer = view.findViewById(R.id.schedule_container);
        emptyScheduleText = view.findViewById(R.id.tv_empty_schedule);
        emptyScheduleIcon = view.findViewById(R.id.iv_empty_schedule_icon);
        trackerBack = view.findViewById(R.id.tracker_back);
        goalContainer = view.findViewById(R.id.goal_container);
        AddGoal = view.findViewById(R.id.btn_add_goal);
        SaveGoals = view.findViewById(R.id.tv_save); // Save button
        progressCircle = view.findViewById(R.id.circular_progress_bar);
        progressCircle.setMax(100); // Max value for progress

        sun = view.findViewById(R.id.tv_sunday);
        mon = view.findViewById(R.id.tv_monday);
        tue = view.findViewById(R.id.tv_tuesday);
        wed = view.findViewById(R.id.tv_wednesday);
        thu = view.findViewById(R.id.tv_thursday);
        fri = view.findViewById(R.id.tv_friday);
        sat = view.findViewById(R.id.tv_saturday);

        // Initially, hide the schedule container and progress circle
        scheduleContainer.setVisibility(View.INVISIBLE);
        progressCircle.setVisibility(View.GONE);

        // Load previously saved goals
        loadGoals();

        // Set up toggle listeners for each day button
        sun.setOnClickListener(v -> toggleDay(sun, "Sun"));
        mon.setOnClickListener(v -> toggleDay(mon, "Mon"));
        tue.setOnClickListener(v -> toggleDay(tue, "Tue"));
        wed.setOnClickListener(v -> toggleDay(wed, "Wed"));
        thu.setOnClickListener(v -> toggleDay(thu, "Thu"));
        fri.setOnClickListener(v -> toggleDay(fri, "Fri"));
        sat.setOnClickListener(v -> toggleDay(sat, "Sat"));

        // Handle "Add Goal" button click to add a new goal dynamically
        AddGoal.setOnClickListener(v -> addNewGoal());

        // Handle "Save" button click to save goals
        SaveGoals.setOnClickListener(v -> {
            saveGoals();
            displayProgressCircle(); // Show the progress circle when saving
        });
        trackerBack.setOnClickListener(v -> handleBackAction());



        // Handle "Add Schedule" button click
        addScheduleButton.setOnClickListener(v -> {
            // Hide the "Add Schedule" button and other placeholders
            addScheduleButton.setVisibility(View.GONE);
            emptyScheduleText.setVisibility(View.GONE);
            emptyScheduleIcon.setVisibility(View.GONE);

            // Show the schedule container
            scheduleContainer.setVisibility(View.VISIBLE);
        });

        addWorkout.setOnClickListener(view1 -> {
            // In ProgressFragment, when navigating to WorkoutModule4
            Intent intent = new Intent(getActivity(), WorkoutModule4.class);
            intent.putExtra("fromTracker", true);  // Pass 'true' when coming from the tracker
            startActivity(intent);

        });
        // Clear previous goals at the start
        goalContainer.removeAllViews();
        goalCount = 0;
        doneCount = 0;

        return view;
    }

    // Method to toggle the state of a day button
    private void toggleDay(Button dayButton, String day) {
        boolean isToggled = false;

        switch (day) {
            case "Sun":
                isSunToggled = !isSunToggled;
                isToggled = isSunToggled;
                break;
            case "Mon":
                isMonToggled = !isMonToggled;
                isToggled = isMonToggled;
                break;
            case "Tue":
                isTueToggled = !isTueToggled;
                isToggled = isTueToggled;
                break;
            case "Wed":
                isWedToggled = !isWedToggled;
                isToggled = isWedToggled;
                break;
            case "Thu":
                isThuToggled = !isThuToggled;
                isToggled = isThuToggled;
                break;
            case "Fri":
                isFriToggled = !isFriToggled;
                isToggled = isFriToggled;
                break;
            case "Sat":
                isSatToggled = !isSatToggled;
                isToggled = isSatToggled;
                break;
        }

        // Change button background color based on toggle state
        if (isToggled) {
            dayButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray_orange)));
        } else {
            dayButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.custom_orange)));
        }

        // Optionally save the toggle state in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(day + "_toggled", isToggled);
        editor.apply();
    }

    // Method to add a new goal dynamically
    private void addNewGoal() {
        // Check if the actual number of children exceeds the limit
        if (goalContainer.getChildCount() >= MAX_GOALS) {
            Toast.makeText(requireContext(), "Maximum goal limit reached", Toast.LENGTH_SHORT).show();
            return;
        }

        // Increment goalCount properly for each added goal
        goalCount = goalContainer.getChildCount() + 1; // Ensure it starts from 1

        // Create a new LinearLayout for the goal row
        LinearLayout goalRow = new LinearLayout(requireContext());
        goalRow.setOrientation(LinearLayout.HORIZONTAL);
        goalRow.setPadding(8, 8, 8, 8);

        // Create a TextView for the goal number
        TextView goalNumber = new TextView(requireContext());
        goalNumber.setText(String.valueOf(goalCount));  // Set the goal number correctly based on total children
        goalNumber.setTextSize(12);
        goalNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        goalNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create an EditText for goal input
        EditText goalInput = new EditText(requireContext());
        goalInput.setHint("Add goal here");
        goalInput.setTextSize(12);
        goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        goalInput.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        goalInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));

        // Create a Delete button (which will later turn to Done after saving)
        TextView deleteButton = new TextView(requireContext());
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_orange));
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Handle deleting the specific goal row
        deleteButton.setOnClickListener(v -> {
            goalContainer.removeView(goalRow);
            updateGoalNumbers();  // Update the numbering after deleting a goal
        });

        // Add the TextView, EditText, and Delete button to the goal row
        goalRow.addView(goalNumber);
        goalRow.addView(goalInput);
        goalRow.addView(deleteButton);

        // Add the newly created row to the goal container
        goalContainer.addView(goalRow);
    }

    // Method to load the saved goals from SharedPreferences
    private void loadGoals() {
        goalCount = 0;  // Reset goal count before loading saved goals
        goalContainer.removeAllViews();  // Clear all views

        for (int i = 0; i < MAX_GOALS; i++) {
            String goalText = sharedPreferences.getString("goal_" + i, null);
            if (goalText != null) {
                addNewGoal();  // Adds new goal and correctly updates goal count and numbering
                View goalRow = goalContainer.getChildAt(goalContainer.getChildCount() - 1);  // Get the added row
                if (goalRow instanceof LinearLayout) {
                    EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1);
                    goalInput.setText(goalText);  // Set the saved goal text
                }
            }
        }

        // Ensure goalCount is updated based on actual loaded goals
        goalCount = goalContainer.getChildCount();
        updateGoalNumbers();  // Ensure goals are renumbered after loading
    }

    // Method to update goal numbers after a change
    private void updateGoalNumbers() {
        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                TextView goalNumber = (TextView) ((LinearLayout) goalRow).getChildAt(0);
                goalNumber.setText(String.valueOf(i + 1));  // Reassign the number sequentially starting from 1
            }
        }
        goalCount = goalContainer.getChildCount(); // Ensure goalCount reflects the actual number of goals
    }

    // Method to save the goals in SharedPreferences
    private void saveGoals() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear previous data

        boolean hasValidGoal = false; // Track if there's at least one valid goal

        // Loop through the goals in the goal container
        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1); // EditText is the second child
                TextView deleteButton = (TextView) ((LinearLayout) goalRow).getChildAt(2); // Delete button is the third child

                String goalText = goalInput.getText().toString().trim(); // Get the goal text and trim whitespace

                if (!goalText.isEmpty()) {
                    // Save the goal text if it's not empty
                    editor.putString("goal_" + i, goalText);
                    hasValidGoal = true; // There's at least one valid goal

                    // Make EditText uneditable
                    goalInput.setEnabled(false);
                    goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.white)); // Set text color to white

                    // Change Delete button to Done
                    deleteButton.setText("Done");
                    deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                    // Add click listener for Done (mark goal as complete)
                    deleteButton.setOnClickListener(v -> {
                        // Apply strikethrough and change the text color to orange
                        goalInput.setPaintFlags(goalInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Add strikethrough
                        goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_orange)); // Set the text color to custom orange

                        doneCount++;  // Increment done count
                        updateProgress();  // Update progress bar
                        deleteButton.setEnabled(false); // Disable "Done" button after clicking
                    });
                } else {
                    // If the goal input is empty, remove the row
                    goalContainer.removeView(goalRow);
                    i--; // Adjust the index since we just removed a row
                }
            }
        }

        // Apply changes to SharedPreferences only if there's at least one valid goal
        if (hasValidGoal) {
            editor.apply();
            Toast.makeText(requireContext(), "Goals saved!", Toast.LENGTH_SHORT).show();

            // Disable all the day toggle buttons after saving
            disableDayButtons();

            // Update goal numbering after empty rows have been removed
            updateGoalNumbers();

            // Hide the Add and Save buttons after successful saving
            AddGoal.setVisibility(View.GONE);
            SaveGoals.setVisibility(View.GONE);

        } else {
            // If no valid goals are present, show an error message
            Toast.makeText(requireContext(), "Please add at least one goal before saving.", Toast.LENGTH_SHORT).show();

            // Keep the Add and Save buttons visible so the user can continue adding goals
            AddGoal.setVisibility(View.VISIBLE);
            SaveGoals.setVisibility(View.VISIBLE);
        }
    }

    // Method to disable the day buttons after saving
    private void disableDayButtons() {
        disableButton(sun);
        disableButton(mon);
        disableButton(tue);
        disableButton(wed);
        disableButton(thu);
        disableButton(fri);
        disableButton(sat);
    }

    // Helper method to disable a button but retain its color
    private void disableButton(Button dayButton) {
        dayButton.setClickable(false); // Disable the click functionality
        dayButton.setFocusable(false); // Disable focusable, to make sure no interaction can happen
        dayButton.setAlpha(1.0f); // Retain the original opacity (no grayed-out effect)
    }

    // Method to show the progress bar and move the goals when it appears
    private void displayProgressCircle() {
        progressCircle.setVisibility(View.VISIBLE);  // Show the progress circle

        // Move the goals and other views to the right when the progress bar is visible
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) goalContainer.getLayoutParams();
        params.setMarginStart(350);  // Adjust margin to create space (100dp or adjust as needed)
        goalContainer.setLayoutParams(params);  // Apply the new layout parameters


        // Update progress value as well
        updateProgress();
    }

    // Method to hide the progress bar and reset the layout
    private void hideProgressCircle() {
        progressCircle.setVisibility(View.GONE);  // Hide the progress circle

        // Reset the layout margins when the progress bar is hidden
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) goalContainer.getLayoutParams();
        params.setMarginStart(0);  // Reset margin to its original position
        goalContainer.setLayoutParams(params);

    }

    // Method to update the progress value
    private void updateProgress() {
        if (goalContainer.getChildCount() > 0) {
            int progress = (int) ((doneCount / (float) goalContainer.getChildCount()) * 100);  // Calculate progress percentage
            progressCircle.setProgress(progress);  // Update circular progress bar
        } else {
            progressCircle.setVisibility(View.GONE);  // Hide if no goals exist
        }
    }

    private void handleBackAction() {
        if (scheduleContainer.getVisibility() == View.VISIBLE) {
            scheduleContainer.setVisibility(View.INVISIBLE);
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
