package com.example.heavymetals.Home_LandingPage.Tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.heavymetals.Home_LandingPage.HomeFragment;
import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutDetailActivity;
import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutModule4;
import com.example.heavymetals.R;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ProgressFragment extends Fragment {

    private TextView workoutTitleTextView, exerciseCountTextView;
    private Button addScheduleButton, viewWorkoutButton, addWorkout, sun, mon, tue, wed, thu, fri, sat, AddGoal;
    private View scheduleContainer;
    private TextView emptyScheduleText, trackerBack, SaveGoals;
    private LinearLayout goalContainer, workoutContainer;
    private ImageView emptyScheduleIcon;
    private int goalCount = 0;
    private int doneCount = 0; // Track how many goals are marked as done
    private final int MAX_GOALS = 5;
    private MaterialProgressBar progressCircle;
    private boolean isWorkoutSelected = false;  // Track if a workout has been added
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
        workoutContainer = view.findViewById(R.id.workout_container);
        workoutTitleTextView = view.findViewById(R.id.workoutTitle);
        exerciseCountTextView = view.findViewById(R.id.exerciseCount);
        viewWorkoutButton = view.findViewById(R.id.btn_view_workout);
        addScheduleButton = view.findViewById(R.id.btn_add_schedule);
        addWorkout = view.findViewById(R.id.btn_add_workout);
        scheduleContainer = view.findViewById(R.id.schedule_container);
        emptyScheduleText = view.findViewById(R.id.tv_empty_schedule);
        emptyScheduleIcon = view.findViewById(R.id.iv_empty_schedule_icon);
        trackerBack = view.findViewById(R.id.tracker_back);
        goalContainer = view.findViewById(R.id.goal_container);
        AddGoal = view.findViewById(R.id.btn_add_goal);  // Button to add new goals
        SaveGoals = view.findViewById(R.id.tv_save);
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
        workoutContainer.setVisibility(View.GONE); // Hide workout container initially
        progressCircle.setVisibility(View.GONE);

        // Load previously saved goals
        loadGoals();

        // Handle "Add Workout" button click
        addWorkout.setOnClickListener(v -> {
            if (!isWorkoutSelected) {
                // If no workout is selected, open the workout module to add a workout
                Intent intent = new Intent(getActivity(), WorkoutModule4.class);
                intent.putExtra("fromTracker", true);  // Pass 'true' when coming from the tracker
                startActivity(intent);
            } else {
                // If a workout is already selected, allow the user to choose another one
                chooseAnotherWorkout();
            }
        });

        // Handle "View Workout" button click
        viewWorkoutButton.setOnClickListener(v -> {
            int workoutId = sharedPreferences.getInt("workout_id", -1);  // Get workout ID
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String sessionToken = sharedPreferences.getString("auth_token", null);  // Fetch the session token

            if (workoutId > 0) {
                Intent intent = new Intent(getActivity(), WorkoutDetailActivity.class);
                intent.putExtra("workout_id", workoutId);  // Pass workout ID
                intent.putExtra("session_token", sessionToken);  // Pass session token, even if it might be null
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "No workout selected.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Add Schedule" button click
        addScheduleButton.setOnClickListener(v -> {
            // Hide the "Add Schedule" button and other placeholders
            addScheduleButton.setVisibility(View.GONE);
            emptyScheduleText.setVisibility(View.GONE);
            emptyScheduleIcon.setVisibility(View.GONE);

            // Show the schedule container
            scheduleContainer.setVisibility(View.VISIBLE);
        });

        // Get workout details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String workoutTitle = args.getString("workout_title");
            int exerciseCount = args.getInt("exercise_count");
            int workoutId = args.getInt("workout_id", -1);  // Default to -1 if not passed

            Log.d("ProgressFragment", "Workout Title: " + workoutTitle + ", Exercise Count: " + exerciseCount + ", Workout ID: " + workoutId);

            if (workoutId > 0) {
                // Call method to display the workout
                displayWorkout(workoutTitle, exerciseCount, workoutId);
            } else {
                Log.d("ProgressFragment", "Workout details are missing or invalid");
            }
        }
        // Handle "Save Goals" button click
        SaveGoals.setOnClickListener(v -> saveGoals());  // Save goals when Save button is clicked

        // Set up toggle actions for each day button
        sun.setOnClickListener(v -> toggleDay(sun, "Sun"));
        mon.setOnClickListener(v -> toggleDay(mon, "Mon"));
        tue.setOnClickListener(v -> toggleDay(tue, "Tue"));
        wed.setOnClickListener(v -> toggleDay(wed, "Wed"));
        thu.setOnClickListener(v -> toggleDay(thu, "Thu"));
        fri.setOnClickListener(v -> toggleDay(fri, "Fri"));
        sat.setOnClickListener(v -> toggleDay(sat, "Sat"));

        // Handle "Back" button click
        trackerBack.setOnClickListener(v -> handleBackAction());  // Handle back button

        // Handle "Add Goal" button click (this is your Add button functionality)
        AddGoal.setOnClickListener(v -> addNewGoal());  // This calls the addNewGoal method

        return view;
    }

    private void chooseAnotherWorkout() {
        workoutContainer.setVisibility(View.GONE);
        isWorkoutSelected = false;
        addWorkout.setText("Add Workout");
        Toast.makeText(getContext(), "Choose a different workout plan.", Toast.LENGTH_SHORT).show();
    }

    private void displayWorkout(String workoutTitle, int exerciseCount, int workoutId) {
        // Logic to display the workout in the UI
        Log.d("ProgressFragment", "Displaying workout: " + workoutTitle + " with " + exerciseCount + " exercises, ID: " + workoutId);

        // Hide the first workout details
        workoutTitleTextView.setVisibility(View.GONE);  // Hide the first workout title
        exerciseCountTextView.setVisibility(View.GONE);  // Hide the first exercise count
        viewWorkoutButton.setVisibility(View.GONE);  // Hide the "View Workout" button

        // Add the workout item to the container below (second one)
        addWorkoutItem(workoutTitle, exerciseCount, workoutId);  // Pass workoutId to the method
    }

    // Save the selected workout details in SharedPreferences when returning from WorkoutModule4
    @Override
    public void onResume() {
        super.onResume();

        // Ensure the workout container is visible if workout is selected
        workoutContainer.setVisibility(View.VISIBLE);

        // Load the selected workout details into the UI
        loadSelectedWorkout(workoutTitleTextView, exerciseCountTextView, workoutContainer);
    }


    private void loadSelectedWorkout(TextView workoutTitleTextView, TextView exerciseCountTextView, LinearLayout workoutContainer) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SelectedWorkout", Context.MODE_PRIVATE);

        // Retrieve workout details from SharedPreferences
        String workoutTitle = sharedPreferences.getString("workout_title", "No workout selected");
        int exerciseCount = sharedPreferences.getInt("exercise_count", 0);
        int workoutId = sharedPreferences.getInt("workout_id", -1); // Retrieve workout ID

        // Clear previous workout items to avoid duplicates
        workoutContainer.removeAllViews();

        // Check if the workout has been selected
        if (workoutId != -1 && !workoutTitle.equals("No workout selected")) {
            // Inflate the workout item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View workoutItemView = inflater.inflate(R.layout.workout_item, workoutContainer, false);

            // Find the TextViews for workout details
            TextView workoutTitleView = workoutItemView.findViewById(R.id.workoutTitle);
            TextView exerciseCountView = workoutItemView.findViewById(R.id.exerciseCount);
            Button viewWorkoutButton = workoutItemView.findViewById(R.id.viewWorkoutButton);

            // Set the workout data to the views
            workoutTitleView.setText(workoutTitle);
            exerciseCountView.setText("Exercises: " + exerciseCount);

            // Set up the 'View Workout' button
            viewWorkoutButton.setOnClickListener(v -> viewWorkoutDetails(workoutId));

            // Add the workout item to the container
            workoutContainer.addView(workoutItemView);

            // Ensure the container is visible
            workoutContainer.setVisibility(View.VISIBLE);
        } else {
            // Hide the workout container if no workout is selected
            workoutContainer.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No workout selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewWorkoutDetails(int workoutId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String sessionToken = sharedPreferences.getString("auth_token", null);  // Fetch the session token

        if (workoutId > 0) {
            // Start WorkoutDetailActivity with workout_id
            Intent intent = new Intent(getActivity(), WorkoutDetailActivity.class);
            intent.putExtra("workout_id", workoutId);
            intent.putExtra("session_token", sessionToken);  // Pass session token, even if it's null
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Invalid workout ID.", Toast.LENGTH_SHORT).show();
        }
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

        goalCount = goalContainer.getChildCount() + 1; // Ensure it starts from 1

        // Create a new LinearLayout for the goal row
        LinearLayout goalRow = new LinearLayout(requireContext());
        goalRow.setOrientation(LinearLayout.HORIZONTAL);
        goalRow.setPadding(8, 8, 8, 8);

        // Create a TextView for the goal number
        TextView goalNumber = new TextView(requireContext());
        goalNumber.setText(String.valueOf(goalCount));
        goalNumber.setTextSize(12);
        goalNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        // Set layout parameters for the goal number
        LinearLayout.LayoutParams numberParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        goalNumber.setLayoutParams(numberParams);

        // Create an EditText for goal input
        EditText goalInput = new EditText(requireContext());
        goalInput.setHint("Add goal here");
        goalInput.setTextSize(12);
        goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        goalInput.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray));

        // Set layout parameters to make EditText take more space (weight=1)
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                0, // 0 width, as weight will determine the width
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // weight of 1 to make it take up the remaining space
        );
        goalInput.setLayoutParams(inputParams);

        // Create a Delete button
        TextView deleteButton = new TextView(requireContext());
        deleteButton.setText("Delete");
        deleteButton.setTextSize(12);
        deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_orange));

        // Handle deleting the specific goal row
        deleteButton.setOnClickListener(v -> {
            goalContainer.removeView(goalRow);
            updateGoalNumbers();  // Update the numbering after deleting a goal
        });

        // Set layout parameters for the delete button
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        deleteButton.setLayoutParams(deleteParams);

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
                addNewGoal();
                View goalRow = goalContainer.getChildAt(goalContainer.getChildCount() - 1);  // Get the added row
                if (goalRow instanceof LinearLayout) {
                    EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1);
                    goalInput.setText(goalText);
                }
            }
        }

        goalCount = goalContainer.getChildCount();
        updateGoalNumbers();
    }

    // Method to update goal numbers after a change
    private void updateGoalNumbers() {
        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                TextView goalNumber = (TextView) ((LinearLayout) goalRow).getChildAt(0);
                goalNumber.setText(String.valueOf(i + 1));
            }
        }
        goalCount = goalContainer.getChildCount(); // Ensure goalCount reflects the actual number of goals
    }

    // Method to save the goals in SharedPreferences
    private void saveGoals() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear previous data

        boolean hasValidGoal = false; // Track if there's at least one valid goal

        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                EditText goalInput = (EditText) ((LinearLayout) goalRow).getChildAt(1);
                TextView deleteButton = (TextView) ((LinearLayout) goalRow).getChildAt(2);

                String goalText = goalInput.getText().toString().trim();

                if (!goalText.isEmpty()) {
                    editor.putString("goal_" + i, goalText);
                    hasValidGoal = true;

                    // Make EditText uneditable
                    goalInput.setEnabled(false);
                    goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                    deleteButton.setText("Done");
                    deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

                    deleteButton.setOnClickListener(v -> {
                        goalInput.setPaintFlags(goalInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        goalInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_orange));

                        doneCount++;
                        updateProgress();  // Update progress here
                        deleteButton.setEnabled(false);
                    });
                } else {
                    goalContainer.removeView(goalRow);
                    i--;
                }
            }
        }

        if (hasValidGoal) {
            editor.apply();
            Toast.makeText(requireContext(), "Goals saved!", Toast.LENGTH_SHORT).show();
            disableDayButtons();
            updateGoalNumbers();
            AddGoal.setVisibility(View.GONE);
            SaveGoals.setVisibility(View.GONE);

            // Call displayProgressCircle after goals are saved successfully
            displayProgressCircle();
        } else {
            Toast.makeText(requireContext(), "Please add at least one goal before saving.", Toast.LENGTH_SHORT).show();
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

    private void disableButton(Button dayButton) {
        dayButton.setClickable(false);
        dayButton.setFocusable(false);
        dayButton.setAlpha(1.0f);
    }

    // Method to show the progress bar and move the goals when it appears
    private void displayProgressCircle() {
        progressCircle.setVisibility(View.VISIBLE);

        // Move the goals to the right
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) goalContainer.getLayoutParams();
        params.setMarginStart(350);
        goalContainer.setLayoutParams(params);

        updateProgress();
    }

    private void updateProgress() {
        if (goalContainer.getChildCount() > 0) {
            int progress = (int) ((doneCount / (float) goalContainer.getChildCount()) * 100);
            progressCircle.setProgress(progress);
        } else {
            progressCircle.setVisibility(View.GONE);
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

    private void addWorkoutItem(String workoutTitle, int exerciseCount, int workoutId) {
        // Inflate the workout item layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View workoutItemView = inflater.inflate(R.layout.workout_item, workoutContainer, false);

        // Find the TextViews for title and exercise count
        TextView workoutTitleView = workoutItemView.findViewById(R.id.workoutTitle);
        TextView exerciseCountView = workoutItemView.findViewById(R.id.exerciseCount);
        Button viewWorkoutButton = workoutItemView.findViewById(R.id.viewWorkoutButton);

        // Set the text to display the workout's title and exercise count
        workoutTitleView.setText(workoutTitle);
        exerciseCountView.setText("Exercises: " + exerciseCount);

        // Handle the view workout button click
        viewWorkoutButton.setOnClickListener(v -> viewWorkoutDetails(workoutId));

        // Make sure to set visibility here to ensure the item is visible
        workoutItemView.setVisibility(View.VISIBLE);

        // Add the workout item to the container
        workoutContainer.addView(workoutItemView);
    }

}
