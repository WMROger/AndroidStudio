package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private TextView firstNameTextView, emailTextView, editProfile;
    private Button continue_btn;
    private ProgressBar accountProgress;
    private TextView progressText;
    static final String PREFS_NAME = "UserProgressPrefs";
    static final String PROGRESS_KEY = "progress";
    private static final String STEP1_COMPLETED_KEY = "profile_creation_completed";
    private static final String STEP2_COMPLETED_KEY = "fitness_declaration1_completed";
    private static final String STEP3_COMPLETED_KEY = "fitness_declaration2_completed";
    private static final String STEP4_COMPLETED_KEY = "fitness_declaration3_completed";
    private static final int MAX_PROGRESS = 100; // Maximum progress value

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the TextViews and other views
        firstNameTextView = view.findViewById(R.id.Menu_User_Firstname);
        emailTextView = view.findViewById(R.id.Menu_User_Email);
        editProfile = view.findViewById(R.id.edit_profile);
        continue_btn = view.findViewById(R.id.continue_button);
        accountProgress = view.findViewById(R.id.account_progress);
        progressText = view.findViewById(R.id.account_progress_text);

        // Fetch user email from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null);
        if (userEmail != null) {
            // Fetch user details from the database
            fetchUserDetails(userEmail);
        } else {
            Toast.makeText(getActivity(), "No logged-in user found.", Toast.LENGTH_SHORT).show();
        }

        continue_btn.setOnClickListener(v -> {
            SharedPreferences sharedPrefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int progress = sharedPrefs.getInt(PROGRESS_KEY, 0);

            // Reference to the progress section layout
            View progressSection = getView().findViewById(R.id.progress_section);

            if (progress >= MAX_PROGRESS) {
                // Hide the progress section when progress reaches 100%
                progressSection.setVisibility(View.GONE);
            } else if (progress == 0) {
                if (!sharedPrefs.getBoolean(STEP1_COMPLETED_KEY, false)) {
                    // Start ProfileCreation (first step)
                    Intent intent = new Intent(getActivity(), ProfileCreation.class);
                    startActivity(intent);
                }
            } else if (progress == 25) {
                if (!sharedPrefs.getBoolean(STEP2_COMPLETED_KEY, false)) {
                    // Start FitnessDeclaration1 (second step)
                    Intent intent = new Intent(getActivity(), FitnessDeclaration.class);
                    startActivity(intent);
                }
            } else if (progress == 50) {
                if (!sharedPrefs.getBoolean(STEP3_COMPLETED_KEY, false)) {
                    // Start FitnessDeclaration2 (third step)
                    Intent intent = new Intent(getActivity(), FitnessDeclaration2.class);
                    startActivity(intent);
                }
            } else if (progress == 75) {
                if (!sharedPrefs.getBoolean(STEP4_COMPLETED_KEY, false)) {
                    // Start FitnessDeclaration3 (final step)
                    Intent intent = new Intent(getActivity(), FitnessDeclaration3.class);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getActivity(), "Invalid progress state", Toast.LENGTH_SHORT).show();
            }
        });

        // Set onClickListener to redirect to ProfileEditActivity
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
            startActivity(intent);
        });

        // Update the progress bar based on saved progress
        updateProgressBar();

        return view;
    }

    private void updateProgressBar() {
        // Get progress from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int progress = sharedPreferences.getInt(PROGRESS_KEY, 0);

        // Limit the progress to 100%
        if (progress > MAX_PROGRESS) {
            progress = MAX_PROGRESS;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PROGRESS_KEY, MAX_PROGRESS);
            editor.apply();
        }

        // Update progress bar and text
        accountProgress.setProgress(progress);
        progressText.setText("Your Account is " + progress + "% complete");

        // Hide the progress section if progress is 100%
        if (progress == MAX_PROGRESS) {
            View progressSection = getView().findViewById(R.id.progress_section);
            progressSection.setVisibility(View.GONE);
        }
    }

    // Method to mark a step as completed and update progress
    private void markStepAsCompleted(String stepKey, int progressIncrement) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Check if the step has already been completed
        boolean isStepCompleted = sharedPreferences.getBoolean(stepKey, false);

        if (!isStepCompleted) {
            // Increment progress and cap it at 100%
            int currentProgress = sharedPreferences.getInt(PROGRESS_KEY, 0);
            int newProgress = Math.min(currentProgress + progressIncrement, MAX_PROGRESS);
            editor.putInt(PROGRESS_KEY, newProgress);

            // Mark the step as completed
            editor.putBoolean(stepKey, true);
            editor.apply();

            // Update the UI
            updateProgressBar();
        } else {
            Toast.makeText(getActivity(), "This step has already been completed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch the user details from the server (asynchronous network request)
    private void fetchUserDetails(String email) {
        new Thread(() -> {
            try {
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
                e.printStackTrace();
            }
        }).start();
    }
}
