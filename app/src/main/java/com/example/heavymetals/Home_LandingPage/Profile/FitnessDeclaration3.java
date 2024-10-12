package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.R;

import java.util.HashMap;
import java.util.Map;

public class FitnessDeclaration3 extends AppCompatActivity {
    private TextView Fitness_Declaration_2, BMI;
    private Button btnPFDnext3;
    private Button btn33, btn34, btn27, btn28, btn29;  // Declare the YES and NO buttons
    private String selectedDays; // Declare selectedDays variable here
    private String workoutExperience; // Declare workoutExperience (YES/NO)

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "UserProgressPrefs";
    private static final String PROGRESS_KEY = "progress";
    private static final String FITNESS_DECLARATION_3_COMPLETED = "fitness_declaration_3_completed";
    private static final int FITNESS_DECLARATION_3_PROGRESS = 25; // 25% for this step

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration_3);

        // Initialize Views
        Fitness_Declaration_2 = findViewById(R.id.Fitness_Declaration_2);
        btnPFDnext3 = findViewById(R.id.btnPFDnext3);
        btn33 = findViewById(R.id.button33); // YES Button
        btn34 = findViewById(R.id.button34); // NO Button
        btn27 = findViewById(R.id.button27); // 1-2 days button
        btn28 = findViewById(R.id.button28); // 5+ days button
        btn29 = findViewById(R.id.button29); // 3-4 days button
        Spinner spinnerStrengthExperience = findViewById(R.id.spinner_strength_experience);
        BMI = findViewById(R.id.BMI);  // Initialize the TextView for BMI

        // Set Listeners for the Days buttons (btn27, btn28, btn29)
        btn27.setOnClickListener(v -> {
            selectedDays = "1-2 days"; // Store the selection
            btn27.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn28.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
            btn29.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color

            // Change the background color
            btn27.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight 1-2 days
            btn28.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset 5+
            btn29.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset 3-4 days
        });

        btn28.setOnClickListener(v -> {
            selectedDays = "5+ days"; // Store the selection
            btn28.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn27.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
            btn29.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color

            // Change the background color
            btn28.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight 5+
            btn27.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset 1-2 days
            btn29.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset 3-4 days
        });

        btn29.setOnClickListener(v -> {
            selectedDays = "3-4 days"; // Store the selection
            btn29.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn27.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
            btn28.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color

            // Change the background color
            btn29.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight 3-4 days
            btn27.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset 1-2 days
            btn28.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset 5+
        });

        // Set Listeners for YES/NO buttons for workout experience
        btn33.setOnClickListener(v -> {
            workoutExperience = "YES";  // Set workoutExperience to YES
            btn33.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn34.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
            btn33.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight YES
            btn34.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset NO
        });

        btn34.setOnClickListener(v -> {
            workoutExperience = "NO";  // Set workoutExperience to NO
            btn34.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn33.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
            btn34.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight NO
            btn33.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset YES
        });

        // Set Listeners for other navigation buttons
        Fitness_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, FitnessDeclaration2.class);
            startActivity(intent);
        });

        // When Next button is clicked
        btnPFDnext3.setOnClickListener(v -> {
            btnPFDnext3.setTextColor(getResources().getColor(R.color.white));
            markStepAsCompleted();
            updateProgress(FITNESS_DECLARATION_3_PROGRESS);
            sendDataToServer();  // Call sendDataToServer to upload data
            Intent intent = new Intent(FitnessDeclaration3.this, ProfileFinish.class);
            startActivity(intent);
        });

        // Spinner for strength experience
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strength_experience_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinnerStrengthExperience.setAdapter(adapter);

        setBmiText();
    }

    // Method to send data to PHP server using Volley
    // Method to send data to PHP server using Volley
    private void sendDataToServer() {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/user_details/save_fitness_declaration_3.php";  // Replace with your server URL
        RequestQueue queue = Volley.newRequestQueue(FitnessDeclaration3.this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);  // Get user_id from SharedPreferences

        // Check if user_id is null and handle it
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(FitnessDeclaration3.this, "User ID is missing. Please log in again.", Toast.LENGTH_LONG).show();
            // Optionally redirect to login or take other appropriate action
            return;  // Don't proceed if user_id is null
        }

        // Proceed with sending the data if user_id is valid
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(FitnessDeclaration3.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FitnessDeclaration3.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(FitnessDeclaration3.this, "Network Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                // Retrieve other values for the POST request
                String strengthExperience = ((Spinner) findViewById(R.id.spinner_strength_experience)).getSelectedItem().toString();
                String consistency = btn33.getCurrentTextColor() == getResources().getColor(R.color.white) ? "YES" : "NO";  // Check which button is highlighted

                // Add POST parameters
                params.put("user_id", userId);  // Make sure user_id is not null
                params.put("bmi", BMI.getText().toString());
                params.put("training_days", selectedDays);  // From the selected button
                params.put("workout_experience", workoutExperience);  // From YES/NO button
                params.put("strength_experience", strengthExperience);
                params.put("consistency", consistency);

                return params;
            }
        };
        queue.add(stringRequest);
    }


    // Method to mark this step as completed
    private void markStepAsCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FITNESS_DECLARATION_3_COMPLETED, true);  // Mark step as completed
        editor.apply();
    }

    // Method to update progress
    private void updateProgress(int progressIncrement) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentProgress = sharedPreferences.getInt(PROGRESS_KEY, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, currentProgress + progressIncrement);  // Increment progress
        editor.apply();
    }

    private void setBmiText() {
        Intent intent = getIntent();
        double bmi = intent.getDoubleExtra("BMI_VALUE", 0.0);  // Default value is 0.0 if not found
        BMI.setText(String.format("%.2f", bmi));  // Display BMI
    }
}
