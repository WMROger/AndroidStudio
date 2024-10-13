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

public class ProgressFragment extends Fragment {

    private Button addScheduleButton, sun, mon, tue, wed, thu, fri, sat, AddGoal;
    private View scheduleContainer;
    private TextView emptyScheduleText;
    private TextView trackerBack;
    private LinearLayout goalContainer;
    private ImageView emptyScheduleIcon;
    private int goalCount = 0;  // To keep track of how many goals have been added
    private final int MAX_GOALS = 5;  // Limit the number of goals to 5

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

        // Initialize views
        addScheduleButton = view.findViewById(R.id.btn_add_schedule);
        scheduleContainer = view.findViewById(R.id.schedule_container);
        emptyScheduleText = view.findViewById(R.id.tv_empty_schedule);
        emptyScheduleIcon = view.findViewById(R.id.iv_empty_schedule_icon);
        trackerBack = view.findViewById(R.id.tracker_back);
        goalContainer = view.findViewById(R.id.goal_container);  // This is the container for dynamically added goals
        AddGoal = view.findViewById(R.id.btn_add_goal);

        sun = view.findViewById(R.id.tv_sunday);
        mon = view.findViewById(R.id.tv_monday);
        tue = view.findViewById(R.id.tv_tuesday);
        wed = view.findViewById(R.id.tv_wednesday);
        thu = view.findViewById(R.id.tv_thursday);
        fri = view.findViewById(R.id.tv_friday);
        sat = view.findViewById(R.id.tv_saturday);

        // Initially, hide the schedule container
        scheduleContainer.setVisibility(View.GONE);

        // Handle "Add Schedule" button click
        addScheduleButton.setOnClickListener(v -> {
            // Hide the add schedule button, empty schedule icon, and text
            addScheduleButton.setVisibility(View.GONE);
            emptyScheduleText.setVisibility(View.GONE);
            emptyScheduleIcon.setVisibility(View.GONE);
            // Show the schedule container
            scheduleContainer.setVisibility(View.VISIBLE);
        });

        // Handle "Back" button logic
        trackerBack.setOnClickListener(v -> handleBackAction());

        // Handle back button press logic for physical back button
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    handleBackAction();
                }
            });
        }

        // Handle "Add Goal" button click to add a new goal dynamically
        AddGoal.setOnClickListener(v -> addNewGoal());

        // Set click listeners for the day buttons
        sun.setOnClickListener(v -> toggleButton((MaterialButton) sun, isSunToggled = !isSunToggled));
        mon.setOnClickListener(v -> toggleButton((MaterialButton) mon, isMonToggled = !isMonToggled));
        tue.setOnClickListener(v -> toggleButton((MaterialButton) tue, isTueToggled = !isTueToggled));
        wed.setOnClickListener(v -> toggleButton((MaterialButton) wed, isWedToggled = !isWedToggled));
        thu.setOnClickListener(v -> toggleButton((MaterialButton) thu, isThuToggled = !isThuToggled));
        fri.setOnClickListener(v -> toggleButton((MaterialButton) fri, isFriToggled = !isFriToggled));
        sat.setOnClickListener(v -> toggleButton((MaterialButton) sat, isSatToggled = !isSatToggled));

        return view;
    }

    // Method to add a new goal dynamically
    private void addNewGoal() {
        if (goalCount >= MAX_GOALS) {
            // If the number of goals has reached the maximum limit, notify the user
            Toast.makeText(requireContext(), "Maximum goal limit reached", Toast.LENGTH_SHORT).show();
            return;
        }

        goalCount++;  // Increment the goal count

        // Create a new LinearLayout for the goal row
        LinearLayout goalRow = new LinearLayout(requireContext());
        goalRow.setOrientation(LinearLayout.HORIZONTAL);
        goalRow.setPadding(8, 8, 8, 8);  // Add some padding

        // Create a TextView for the goal number
        TextView goalNumber = new TextView(requireContext());
        goalNumber.setText(String.valueOf(goalCount));  // Sequential numbering of goals (1, 2, 3, 4, 5)
        goalNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create an EditText for goal input
        EditText goalInput = new EditText(requireContext());
        goalInput.setHint("Add goal here");
        goalInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));

        // Create a Delete button
        TextView deleteButton = new TextView(requireContext());
        deleteButton.setText("Delete");
        deleteButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_orange));
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Handle deleting the specific goal row
        deleteButton.setOnClickListener(v -> {
            goalContainer.removeView(goalRow);
            goalCount--;  // Decrement the goal count when a goal is deleted
            updateGoalNumbers();  // Update goal numbers after deleting
        });

        // Add the TextView, EditText, and Delete button to the goal row
        goalRow.addView(goalNumber);
        goalRow.addView(goalInput);
        goalRow.addView(deleteButton);

        // Add the newly created row to the goal container
        goalContainer.addView(goalRow);
    }

    // Update the numbers for each goal after adding or deleting
    private void updateGoalNumbers() {
        int count = 1;  // Start numbering from 1
        for (int i = 0; i < goalContainer.getChildCount(); i++) {
            View goalRow = goalContainer.getChildAt(i);
            if (goalRow instanceof LinearLayout) {
                // Get the TextView that holds the goal number and update its text
                TextView goalNumber = (TextView) ((LinearLayout) goalRow).getChildAt(0);
                goalNumber.setText(String.valueOf(count));
                count++;
            }
        }
    }


    // Method to toggle MaterialButton background, stroke color, and text color
    private void toggleButton(MaterialButton button, boolean isToggled) {
        if (isToggled) {
            // Set the toggled state: background gray, stroke orange, and text white
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));
            button.setStrokeColorResource(R.color.custom_orange);
            button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));
        } else {
            // Revert to default state: background custom orange, stroke gray, and text black
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.custom_orange)));
            button.setStrokeColorResource(android.R.color.darker_gray);
            button.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));
        }
    }

    private void handleBackAction() {
        if (scheduleContainer.getVisibility() == View.VISIBLE) {
            // Hide the schedule container and show the original layout
            scheduleContainer.setVisibility(View.GONE);
            addScheduleButton.setVisibility(View.VISIBLE);
            emptyScheduleText.setVisibility(View.VISIBLE);
            emptyScheduleIcon.setVisibility(View.VISIBLE);
        } else {
            // Replace this fragment with HomeFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
