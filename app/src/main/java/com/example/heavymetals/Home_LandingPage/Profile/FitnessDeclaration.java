package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FitnessDeclaration extends AppCompatActivity {

    Button btnPFDnext;
    ArrayList<String> selectedGoals = new ArrayList<>();

    Button btnLoseWeight, btnIncreaseStrength, btnBuildMuscle, btnMobility, btnWellness, btnFitness;
    static String userId;  // Ensure userId is passed

    // Constants for progress tracking
    private static final String PREFS_NAME = "UserProgressPrefs";
    private static final String PROGRESS_KEY = "progress";
    private static final String STEP_COMPLETED_KEY = "fitness_declaration_completed";
    private static final int FITNESS_DECLARATION_PROGRESS = 25;  // Each step gives 25% progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration);

        // Initialize buttons
        btnLoseWeight = findViewById(R.id.button);
        btnIncreaseStrength = findViewById(R.id.button6);
        btnBuildMuscle = findViewById(R.id.button7);
        btnMobility = findViewById(R.id.button8);
        btnWellness = findViewById(R.id.button9);
        btnFitness = findViewById(R.id.button14);
        btnPFDnext = findViewById(R.id.btnPFDnext1);
        final String[] userId = {getIntent().getStringExtra("user_id")};

        // Set up click listeners for the goal buttons
        btnLoseWeight.setOnClickListener(v -> toggleGoalSelection(btnLoseWeight, "Lose Weight"));
        btnIncreaseStrength.setOnClickListener(v -> toggleGoalSelection(btnIncreaseStrength, "Increase Strength"));
        btnBuildMuscle.setOnClickListener(v -> toggleGoalSelection(btnBuildMuscle, "Build Muscle"));
        btnMobility.setOnClickListener(v -> toggleGoalSelection(btnMobility, "Mobility"));
        btnWellness.setOnClickListener(v -> toggleGoalSelection(btnWellness, "Wellness and Reduce Stress"));
        btnFitness.setOnClickListener(v -> toggleGoalSelection(btnFitness, "Fitness"));

        // Send selected goals to the server if they meet the criteria
        btnPFDnext.setOnClickListener(v -> {
            if (selectedGoals.isEmpty()) {
                Toast.makeText(FitnessDeclaration.this, "Please select at least one goal", Toast.LENGTH_SHORT).show();
            } else if (selectedGoals.size() > 3) {
                Toast.makeText(FitnessDeclaration.this, "You can only select up to 3 goals", Toast.LENGTH_SHORT).show();
            } else {
                // Fetch userId before sending data
                userId[0] = getIntent().getStringExtra("user_id");

                // Log the userId for debugging purposes
                Log.d("UserID", "User ID being sent: " + userId[0]);

                // Send data to server
                new SendDataToServer(FitnessDeclaration.this, selectedGoals, userId[0]).execute();

                // Update progress after data submission
                markStepAsCompleted();  // Mark this step as completed
                updateProgress(FITNESS_DECLARATION_PROGRESS);  // Increment progress

                // Proceed to the next activity
                Intent intent = new Intent(FitnessDeclaration.this, FitnessDeclaration2.class);
                startActivity(intent);
            }
        });
    }

    // Helper method to toggle goal selection and update the button appearance
    private void toggleGoalSelection(Button button, String goal) {
        if (selectedGoals.contains(goal)) {
            // If the goal is already selected, deselect it
            selectedGoals.remove(goal);
            button.setTextColor(getResources().getColor(R.color.unselected_color));
            button.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset button style

            // Reset icon tint to original color (black or unselected color)
            button.setCompoundDrawableTintList(getResources().getColorStateList(R.color.custom_orange));
        } else {
            // Only allow selection if less than 3 goals are already selected
            if (selectedGoals.size() < 3) {
                selectedGoals.add(goal);
                button.setTextColor(getResources().getColor(R.color.white));
                button.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight button style

                // Set icon tint to white when selected
                button.setCompoundDrawableTintList(getResources().getColorStateList(R.color.white));
            } else {
                // If 3 goals are already selected, show a message
                Toast.makeText(FitnessDeclaration.this, "You can only select up to 3 goals", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to mark this step as completed in SharedPreferences
    private void markStepAsCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STEP_COMPLETED_KEY, true);  // Mark this step as completed
        editor.apply();
    }

    // Method to update the progress in SharedPreferences
    private void updateProgress(int progressIncrement) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentProgress = sharedPreferences.getInt(PROGRESS_KEY, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, currentProgress + progressIncrement);  // Increment progress
        editor.apply();
    }

    // Static inner class to send selected goals to the server
    private static class SendDataToServer extends AsyncTask<Void, Void, String> {
        private WeakReference<FitnessDeclaration> activityReference;
        private ArrayList<String> selectedGoals;
        private String userId;
        private String urlString = "https://heavymetals.scarlet2.io/HeavyMetals/profile/handle_user_goals.php";

        // Constructor
        SendDataToServer(FitnessDeclaration context, ArrayList<String> goals, String userId) {
            activityReference = new WeakReference<>(context);
            this.selectedGoals = goals;
            this.userId = userId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true); // Allow following redirects
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String postData = "user_id=" + userId
                        + "&lose_weight=" + (selectedGoals.contains("Lose Weight") ? 1 : 0)
                        + "&increase_strength=" + (selectedGoals.contains("Increase Strength") ? 1 : 0)
                        + "&build_muscle=" + (selectedGoals.contains("Build Muscle") ? 1 : 0)
                        + "&mobility=" + (selectedGoals.contains("Mobility") ? 1 : 0)
                        + "&wellness_reduce_stress=" + (selectedGoals.contains("Wellness and Reduce Stress") ? 1 : 0)
                        + "&fitness=" + (selectedGoals.contains("Fitness") ? 1 : 0);

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("HTTP Response", "Response Code: " + responseCode);

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Log.d("HTTP Response", "Response Body: " + response.toString());

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return response.toString();
                } else {
                    return "Failed to send data, response code: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception occurred: " + e.getMessage();
            } finally {
                try {
                    if (conn != null) conn.disconnect();
                    if (reader != null) reader.close();
                } catch (Exception ignored) {}
            }
        }

        @Override
        protected void onPostExecute(String result) {
            FitnessDeclaration activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            Log.d("POST Result", "Result: " + result);

            if (result.contains("success")) {
                Intent intent = new Intent(activity, FitnessDeclaration2.class);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
