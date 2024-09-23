package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.heavymetals.R;
import android.widget.ImageButton;

public class WorkoutModule1 extends Fragment {
    private Button FEPAddworkout;
    private ImageButton addItemBtn1, addItemBtn2, addItemBtn3, addItemBtn4, addItemBtn5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment without attaching it to the root (container)
        View view = inflater.inflate(R.layout.fragment_exercise_plan, container, false);

        // Find the main button from the inflated view
        FEPAddworkout = view.findViewById(R.id.FEPAddWorkout);

        // Find ImageButtons
        addItemBtn1 = view.findViewById(R.id.addItemBtn1);
        addItemBtn2 = view.findViewById(R.id.addItemBtn2);
        addItemBtn3 = view.findViewById(R.id.addItemBtn3);
        addItemBtn4 = view.findViewById(R.id.addItemBtn4);
        addItemBtn5 = view.findViewById(R.id.addItemBtn5);

        // Set the onClickListener for the main button
        FEPAddworkout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutModule2.class);
            startActivity(intent);
        });

        // Add toggle functionality for each ImageButton
        setupToggleButton(addItemBtn1);
        setupToggleButton(addItemBtn2);
        setupToggleButton(addItemBtn3);
        setupToggleButton(addItemBtn4);
        setupToggleButton(addItemBtn5);

        return view;
    }

    private void setupToggleButton(ImageButton button) {
        button.setOnClickListener(v -> {
            // Get the current icon
            int currentIcon = (int) button.getTag(); // Use a tag to keep track of the current state

            if (currentIcon == R.drawable.additem_black) {
                // Change to orange icon
                button.setImageResource(R.drawable.additem_orange);
                button.setTag(R.drawable.additem_orange); // Update the tag to reflect the new state
            } else {
                // Change back to black icon
                button.setImageResource(R.drawable.additem_black);
                button.setTag(R.drawable.additem_black); // Update the tag to reflect the new state
            }
        });

        // Initialize tag with the default icon
        button.setTag(R.drawable.additem_black);
    }
}
