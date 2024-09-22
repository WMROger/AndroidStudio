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

public class WorkoutModule1 extends Fragment {
    private Button FEPAddworkout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercise_plan, container, true);

        // Now find the button from the inflated view
        FEPAddworkout = view.findViewById(R.id.FEPAddWorkout);

        FEPAddworkout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutModule2.class);
            startActivity(intent);
        });

        return view;
    }

}
