package com.example.heavymetals.Home_LandingPage.Tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.heavymetals.R;

public class ProgressFragment extends Fragment {

    private Button addScheduleButton;
    private View scheduleContainer;
    private ImageView emptyScheduleIcon;
    private TextView emptyScheduleText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        // Initialize views
        addScheduleButton = view.findViewById(R.id.btn_add_schedule);
        scheduleContainer = view.findViewById(R.id.schedule_container);
        emptyScheduleIcon = view.findViewById(R.id.iv_empty_schedule_icon);
        emptyScheduleText = view.findViewById(R.id.tv_empty_schedule);

        // Handle "Add Schedule" button click
        addScheduleButton.setOnClickListener(v -> {
            // Hide the add schedule button, empty schedule icon, and text
            addScheduleButton.setVisibility(View.GONE);
            emptyScheduleIcon.setVisibility(View.GONE);
            emptyScheduleText.setVisibility(View.GONE);

            // Show the schedule container
            scheduleContainer.setVisibility(View.VISIBLE);
        });

        return view;
    }
}
