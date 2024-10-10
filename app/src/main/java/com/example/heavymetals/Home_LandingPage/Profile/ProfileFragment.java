package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.heavymetals.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ProfileFragment extends Fragment {

    // UI elements
    private TextView firstNameTextView, emailTextView, editProfile;
    private Button continue_btn;
    private ProgressBar accountProgress;
    private TextView progressText;

    // SharedPreferences constants
    static final String PREFS_NAME = "UserProgressPrefs";
    static final String PROGRESS_KEY = "progress";

    // Step completion keys for SharedPreferences
    static final String STEP1_COMPLETED_KEY = "profile_creation_completed";
    static final String STEP2_COMPLETED_KEY = "fitness_declaration1_completed";
    static final String STEP3_COMPLETED_KEY = "fitness_declaration2_completed";
    static final String STEP4_COMPLETED_KEY = "fitness_declaration3_completed";
    private static final int MAX_PROGRESS = 100; // Maximum progress value

    // Fragment lifecycle: create and display UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        firstNameTextView = view.findViewById(R.id.Menu_User_Firstname);
        emailTextView = view.findViewById(R.id.Menu_User_Email);
        editProfile = view.findViewById(R.id.edit_profile);
        continue_btn = view.findViewById(R.id.continue_button);
        accountProgress = view.findViewById(R.id.account_progress);
        progressText = view.findViewById(R.id.account_progress_text);

        // Ensure the views are not null before using them
        if (accountProgress == null || progressText == null) {
            Log.e("ProfileFragment", "Progress bar or progress text view is missing in the layout");
            return view;  // Return early to prevent further errors
        }

        // Fetch user email from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null);
        if (userEmail != null) {
            // Fetch user details from the database
            fetchUserDetails(userEmail);  // Use the function we're importing from ProfileCreation
        } else {
            Toast.makeText(getActivity(), "No logged-in user found.", Toast.LENGTH_SHORT).show();
        }

        // Handle click for "Continue" button
        continue_btn.setOnClickListener(v -> {
            int progress = sharedPreferences.getInt(PROGRESS_KEY, 0);

            // Reference to the progress section layout
            View progressSection = view.findViewById(R.id.progress_section);

            if (progressSection == null) {
                Log.e("ProfileFragment", "Progress section not found in the layout");
                return;
            }

            if (progress >= 100) {
                // Hide the progress section when progress is 100%
                progressSection.setVisibility(View.GONE);
            } else if (progress == 0) {
                // Redirect to ProfileCreation if progress is 0
                Intent intent = new Intent(getActivity(), ProfileCreation.class);
                startActivity(intent);
            } else if (progress == 25) {
                // Redirect to FitnessDeclaration if progress is 25%
                Intent intent = new Intent(getActivity(), FitnessDeclaration.class);
                startActivity(intent);
            } else if (progress == 50) {
                // Redirect to the next step (e.g., Second FitnessDeclaration)
                Intent intent = new Intent(getActivity(), FitnessDeclaration2.class);
                startActivity(intent);
            } else if (progress == 75) {
                // Redirect to the final step (e.g., Third FitnessDeclaration)
                Intent intent = new Intent(getActivity(), FitnessDeclaration3.class);
                startActivity(intent);
            }
        });

        // Handle profile edit button click
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });

        // Update the progress bar based on saved progress
        updateProgressBar(view);

        return view;
    }

    // Method to update the progress bar and UI
    private void updateProgressBar(View view) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int progress = sharedPreferences.getInt(PROGRESS_KEY, 0);  // Get current progress

        // Ensure that progress never exceeds 100%
        if (progress > MAX_PROGRESS) {
            progress = MAX_PROGRESS;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PROGRESS_KEY, MAX_PROGRESS);
            editor.apply();
        }

        ProgressBar accountProgress = view.findViewById(R.id.account_progress);
        TextView progressText = view.findViewById(R.id.account_progress_text);
        View progressSection = view.findViewById(R.id.progress_section);  // Reference to the progress section

        if (accountProgress == null || progressText == null) {
            Log.e("ProfileFragment", "Progress bar or progress text view is missing in the layout");
            return;
        }

        // Update progress bar and text
        accountProgress.setProgress(progress);
        progressText.setText("Your Account is " + progress + "% complete");

        // Show or hide the progress section based on current progress
        if (progress == MAX_PROGRESS) {
            if (progressSection != null) {
                progressSection.setVisibility(View.GONE);  // Show progress section when 100% is reached
            }

            // Optionally, adjust other layouts if needed (moving elements up)
            View mainLayout = view.findViewById(R.id.workout_layout);
            View trackerLayout = view.findViewById(R.id.Tracker_layout);
            View belowLayout = view.findViewById(R.id.below_workout_layout);

            if (mainLayout != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainLayout.getLayoutParams();
                params.topMargin = 50; // Adjust this value based on your layout needs
                mainLayout.setLayoutParams(params);
            }
            if (belowLayout != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) belowLayout.getLayoutParams();
                params.topMargin = 50; // Adjust this value based on your layout needs
                belowLayout.setLayoutParams(params);
            }
            if (trackerLayout != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) trackerLayout.getLayoutParams();
                params.topMargin = 30;  // Adjust this value as needed for tracker layout
                trackerLayout.setLayoutParams(params);
            }
        } else {
            if (progressSection != null) {
                progressSection.setVisibility(View.VISIBLE);  // Ensure the progress section is visible if progress < 100%
            }
        }
    }

    // Method to fetch user details from the server (adapted from ProfileCreation)
    private void fetchUserDetails(String email) {
        new Thread(() -> {
            try {
                // Check if email is null or empty
                if (email == null || email.isEmpty()) {
                    Log.e("ProfileFragment", "No email provided.");
                    return;
                }

                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/get_username.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the POST data with the actual user email
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "email=" + URLEncoder.encode(email, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Get the response
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    String firstName = jsonResponse.getString("first_name");
                    String lastName = jsonResponse.getString("last_name");

                    // Update the UI with the user's name on the main thread
                    getActivity().runOnUiThread(() -> {
                        firstNameTextView.setText(firstName + " " + lastName);
                        emailTextView.setText(email);
                    });
                } else {
                    String message = jsonResponse.getString("message");
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("ProfileFragment", "Error fetching user details", e);
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Error fetching user details. Please try again.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
