package com.example.heavymetals.Home_LandingPage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.heavymetals.Home_LandingPage.Profile.ProfileFragment;
import com.example.heavymetals.Home_LandingPage.Workouts.Exercises_All;
import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutModule1;
import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutModule4;
import com.example.heavymetals.Models.API;
import com.example.heavymetals.Models.CurrentTimeResponse;
import com.example.heavymetals.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private Button CreateWorkoutShortcut_btn, RecommendationsShortcut_btn;
    private TextView textViewCurrentTime;
    private API apiService;
    private final Handler handler = new Handler();
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find views using the inflated view
        Button yourWorkoutBtn = view.findViewById(R.id.YourWorkout_btn);
        textViewCurrentTime = view.findViewById(R.id.textDate);
        CreateWorkoutShortcut_btn = view.findViewById(R.id.CreateWorkoutShortcut_btn);
        RecommendationsShortcut_btn = view.findViewById(R.id.RecommendationsShortcut_btn);

        // Fix for finding RelativeLayouts
        ImageButton settingsHome = view.findViewById(R.id.Settings_home);
        ImageButton profileHome = view.findViewById(R.id.Profile_home);

        // Set click listener for Settings
        settingsHome.setOnClickListener(v -> {
            Log.d("HomeFragment", "Settings clicked");
            // Navigate to SettingsFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SettingsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Set click listener for Profile
        profileHome.setOnClickListener(v -> {
            Log.d("HomeFragment", "Profile clicked");
            // Navigate to ProfileFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ProfileFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Set click listeners for other buttons
        CreateWorkoutShortcut_btn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), Exercises_All.class);
            startActivity(intent);
        });

        RecommendationsShortcut_btn.setOnClickListener(v -> {
            WorkoutModule1 fragment = new WorkoutModule1();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        yourWorkoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), WorkoutModule4.class);
            startActivity(intent);
        });

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://heavymetals.scarlet2.io/HeavyMetals/miscellaneous/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(API.class);

        // Fetch the time from server once and start local updating
        getCurrentTimeFromServer();

        return view;  // Return the view for the fragment
    }

    private void getCurrentTimeFromServer() {
        Call<CurrentTimeResponse> call = apiService.getCurrentTime();
        call.enqueue(new Callback<CurrentTimeResponse>() {
            @Override
            public void onResponse(Call<CurrentTimeResponse> call, Response<CurrentTimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String serverTime = response.body().getCurrentTime();
                    textViewCurrentTime.setText(serverTime);  // Set the server time
                    startLocalTimeUpdate(serverTime);  // Start local updates
                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentTimeResponse> call, Throwable t) {
                Log.e("API Error", t.getMessage(), t);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLocalTimeUpdate(String serverTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

        try {
            Date parsedServerTime = dateFormat.parse(serverTime);
            if (parsedServerTime != null) {
                final long[] serverTimeMillis = {parsedServerTime.getTime()};

                runnable = new Runnable() {
                    @Override
                    public void run() {
                        serverTimeMillis[0] += 1000;  // Increment by 1 second
                        Date updatedTime = new Date(serverTimeMillis[0]);
                        String currentTime = dateFormat.format(updatedTime);
                        textViewCurrentTime.setText(currentTime);  // Update TextView
                        handler.postDelayed(this, 1000);  // Repeat every second
                    }
                };

                handler.post(runnable);  // Start the update
            } else {
                Log.e("Time Parse Error", "Parsed time is null.");
            }
        } catch (Exception e) {
            Log.e("Time Parse Error", e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);  // Stop updates when the fragment is destroyed
    }
}
