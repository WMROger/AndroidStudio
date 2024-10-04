package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FitnessDeclaration extends AppCompatActivity {

    Button btnPFDnext;
    ArrayList<String> selectedGoals = new ArrayList<>();

    Button btnLoseWeight, btnIncreaseStrength, btnBuildMuscle, btnMobility, btnWellness, btnFitness;

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

        // Set up click listeners for the goal buttons
        btnLoseWeight.setOnClickListener(v -> toggleGoalSelection(btnLoseWeight, "Lose Weight"));
        btnIncreaseStrength.setOnClickListener(v -> toggleGoalSelection(btnIncreaseStrength, "Increase Strength"));
        btnBuildMuscle.setOnClickListener(v -> toggleGoalSelection(btnBuildMuscle, "Build Muscle"));
        btnMobility.setOnClickListener(v -> toggleGoalSelection(btnMobility, "Mobility"));
        btnWellness.setOnClickListener(v -> toggleGoalSelection(btnWellness, "Wellness and Reduce Stress"));
        btnFitness.setOnClickListener(v -> toggleGoalSelection(btnFitness, "Fitness"));

        // Send selected goals to server if they meet the criteria
        btnPFDnext.setOnClickListener(v -> {
            if (selectedGoals.isEmpty()) {
                Toast.makeText(FitnessDeclaration.this, "Please select at least one goal", Toast.LENGTH_SHORT).show();
            } else {
                new SendDataToServer(FitnessDeclaration.this, selectedGoals).execute();
            }
        });
    }

    // Helper method to toggle goal selection
    private void toggleGoalSelection(Button button, String goal) {
        if (selectedGoals.contains(goal)) {
            selectedGoals.remove(goal);
            button.setTextColor(getResources().getColor(R.color.unselected_color));
            button.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset button style
        } else {
            if (selectedGoals.size() < 3) {
                selectedGoals.add(goal);
                button.setTextColor(getResources().getColor(R.color.white));
                button.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight button style
            } else {
                Toast.makeText(FitnessDeclaration.this, "You can select a maximum of 3 goals", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Static inner class to send selected goals to server
    private static class SendDataToServer extends AsyncTask<Void, Void, String> {
        private WeakReference<FitnessDeclaration> activityReference;
        private ArrayList<String> selectedGoals;
        private String urlString = "http://heavymetals.scarlet2.io/HeavyMetals/profile/handle_user_goals.php";

        // Constructor
        SendDataToServer(FitnessDeclaration context, ArrayList<String> goals) {
            activityReference = new WeakReference<>(context);
            this.selectedGoals = goals;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String userId = "123"; // Replace this with the actual user ID from session or intent
                String goals = android.text.TextUtils.join(",", selectedGoals);
                String postData = "user_id=" + userId + "&selected_goals=" + goals;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Data sent successfully!";
                } else {
                    return "Failed to send data, response code: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception occurred: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            FitnessDeclaration activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result.equals("Data sent successfully!")) {
                Intent intent = new Intent(activity, FitnessDeclaration2.class);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
