package com.example.heavymetals.Home_LandingPage.Tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.heavymetals.Home_LandingPage.HomeFragment;
import com.example.heavymetals.R;

public class ProgressFragment extends Fragment {

    private Button addScheduleButton;
    private View scheduleContainer;
    private ImageView emptyScheduleIcon;
    private TextView emptyScheduleText;
    private TextView trackerBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // Initialize views
        addScheduleButton = view.findViewById(R.id.btn_add_schedule);
        scheduleContainer = view.findViewById(R.id.schedule_container);
        emptyScheduleIcon = view.findViewById(R.id.iv_empty_schedule_icon);
        emptyScheduleText = view.findViewById(R.id.tv_empty_schedule);
        trackerBack = view.findViewById(R.id.tracker_back);

        // Initially, hide the schedule container
        scheduleContainer.setVisibility(View.GONE);

        // Handle "Add Schedule" button click
        addScheduleButton.setOnClickListener(v -> {
            // Hide the add schedule button, empty schedule icon, and text
            addScheduleButton.setVisibility(View.GONE);
            emptyScheduleIcon.setVisibility(View.GONE);
            emptyScheduleText.setVisibility(View.GONE);

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

        return view;
    }

    private void handleBackAction() {
        if (scheduleContainer.getVisibility() == View.VISIBLE) {
            // Hide the schedule container and show the original layout
            scheduleContainer.setVisibility(View.GONE);
            addScheduleButton.setVisibility(View.VISIBLE);
            emptyScheduleIcon.setVisibility(View.VISIBLE);
            emptyScheduleText.setVisibility(View.VISIBLE);
        } else {
            // Replace this fragment with HomeFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()) // Use your container and fragment
                    .addToBackStack(null)
                    .commit();
        }
    }

}
