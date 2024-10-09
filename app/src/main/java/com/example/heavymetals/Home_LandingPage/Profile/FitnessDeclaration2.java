package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.R;

import java.util.HashMap;
import java.util.Map;

public class FitnessDeclaration2 extends AppCompatActivity {

    private TextView Profile_Declaration_2;
    private Button btnPFDnext2, MaleButton, FemaleButton;
    private String selectedGender;

    // Declare all EditText fields for the measurements
    private EditText etBodyWeight, etHeight, etChest, etShoulder, etWaist, etHips, etLeftBicep, etRightBicep, etLeftForearm, etRightForearm, etLeftCalf, etRightCalf;

    // Constants for progress tracking
    private static final String PREFS_NAME = "UserProgressPrefs";
    private static final String PROGRESS_KEY = "progress";
    private static final String FITNESS_DECLARATION_2_COMPLETED = "fitness_declaration_2_completed";
    private static final int FITNESS_DECLARATION_2_PROGRESS = 25;  // Each step gives 25% progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration_2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        Profile_Declaration_2 = findViewById(R.id.Profile_Declaration_2);
        btnPFDnext2 = findViewById(R.id.btnPFDnext2);
        MaleButton = findViewById(R.id.MaleRadioButton);
        FemaleButton = findViewById(R.id.FemaleRadioButton);

        // Initialize EditText fields
        etBodyWeight = findViewById(R.id.et_body_weight);
        etHeight = findViewById(R.id.et_height);
        etChest = findViewById(R.id.et_chest);
        etShoulder = findViewById(R.id.et_shoulder);
        etWaist = findViewById(R.id.et_waist);
        etHips = findViewById(R.id.et_hips);
        etLeftBicep = findViewById(R.id.et_left_bicep);
        etRightBicep = findViewById(R.id.et_right_bicep);
        etLeftForearm = findViewById(R.id.et_left_forearm);
        etRightForearm = findViewById(R.id.et_right_forearm);
        etLeftCalf = findViewById(R.id.et_left_calf);
        etRightCalf = findViewById(R.id.et_right_calf);

        // Disable the next button initially
        btnPFDnext2.setEnabled(false);

        // Set up TextWatchers to monitor the text fields
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if all fields are filled
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Add TextWatcher to all required fields
        etBodyWeight.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etChest.addTextChangedListener(textWatcher);
        etShoulder.addTextChangedListener(textWatcher);
        etWaist.addTextChangedListener(textWatcher);
        etHips.addTextChangedListener(textWatcher);
        etLeftBicep.addTextChangedListener(textWatcher);
        etRightBicep.addTextChangedListener(textWatcher);
        etLeftForearm.addTextChangedListener(textWatcher);
        etRightForearm.addTextChangedListener(textWatcher);
        etLeftCalf.addTextChangedListener(textWatcher);
        etRightCalf.addTextChangedListener(textWatcher);

        // Set up click listeners for the gender buttons
        MaleButton.setOnClickListener(v -> {
            selectedGender = "male";
            // Highlight Male Button and Reset Female Button
            MaleButton.setTextColor(getResources().getColor(R.color.white));
            MaleButton.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight Male

            FemaleButton.setTextColor(getResources().getColor(R.color.unselected_color));
            FemaleButton.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset Female
        });

        FemaleButton.setOnClickListener(v -> {
            selectedGender = "female";
            // Highlight Female Button and Reset Male Button
            FemaleButton.setTextColor(getResources().getColor(R.color.white));
            FemaleButton.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight Female

            MaleButton.setTextColor(getResources().getColor(R.color.unselected_color));
            MaleButton.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset Male
        });

        // Next button to proceed to the next step
        btnPFDnext2.setOnClickListener(v -> {
            String weightText = etBodyWeight.getText().toString();
            String heightText = etHeight.getText().toString();

            if (!weightText.isEmpty() && !heightText.isEmpty()) {
                try {
                    double weight = Double.parseDouble(weightText);
                    double height = Double.parseDouble(heightText) / 100; // Convert height to meters

                    double bmi = weight / (height * height);

                    // Send data to the server and proceed to the next activity
                    sendDataToServer();

                    // Update progress and mark step as completed
                    updateProgress(FITNESS_DECLARATION_2_PROGRESS);
                    markStepAsCompleted();  // Call this method here

                    Intent intent = new Intent(FitnessDeclaration2.this, FitnessDeclaration3.class);
                    intent.putExtra("BMI_VALUE", bmi);  // Passing the BMI
                    startActivity(intent);

                } catch (NumberFormatException e) {
                    Toast.makeText(FitnessDeclaration2.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FitnessDeclaration2.this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to previous activity
        Profile_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration2.this, FitnessDeclaration.class);
            startActivity(intent);
        });
    }

    // Method to check if all fields are filled
    private void checkFieldsForEmptyValues() {
        String weight = etBodyWeight.getText().toString();
        String height = etHeight.getText().toString();
        String chest = etChest.getText().toString();
        String shoulder = etShoulder.getText().toString();
        String waist = etWaist.getText().toString();
        String hips = etHips.getText().toString();
        String leftBicep = etLeftBicep.getText().toString();
        String rightBicep = etRightBicep.getText().toString();
        String leftForearm = etLeftForearm.getText().toString();
        String rightForearm = etRightForearm.getText().toString();
        String leftCalf = etLeftCalf.getText().toString();
        String rightCalf = etRightCalf.getText().toString();

        btnPFDnext2.setEnabled(!weight.isEmpty() && !height.isEmpty() && !chest.isEmpty() && !shoulder.isEmpty()
                && !waist.isEmpty() && !hips.isEmpty() && !leftBicep.isEmpty() && !rightBicep.isEmpty()
                && !leftForearm.isEmpty() && !rightForearm.isEmpty() && !leftCalf.isEmpty() && !rightCalf.isEmpty()
                && selectedGender != null);  // Make sure gender is selected too
    }

    // Method to send data to PHP server using Volley
    private void sendDataToServer() {
        String url = "http://heavymetals.scarlet2.io/HeavyMetals/profile/save_fitness_declaration.php";  // Replace with your server URL

        RequestQueue queue = Volley.newRequestQueue(FitnessDeclaration2.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle response from the server
                    Toast.makeText(FitnessDeclaration2.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(FitnessDeclaration2.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Assume user_id is passed from previous activity or stored
                params.put("user_id", "123");  // Replace with actual user_id or retrieve dynamically
                params.put("gender", selectedGender);
                params.put("body_weight", etBodyWeight.getText().toString());
                params.put("height", etHeight.getText().toString());
                params.put("chest", etChest.getText().toString());
                params.put("shoulder", etShoulder.getText().toString());
                params.put("waist", etWaist.getText().toString());
                params.put("hips", etHips.getText().toString());
                params.put("left_bicep", etLeftBicep.getText().toString());
                params.put("right_bicep", etRightBicep.getText().toString());
                params.put("left_forearm", etLeftForearm.getText().toString());
                params.put("right_forearm", etRightForearm.getText().toString());
                params.put("left_calf", etLeftCalf.getText().toString());
                params.put("right_calf", etRightCalf.getText().toString());
                return params;
            }
        };

        // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    // Method to update the progress in SharedPreferences
    private void updateProgress(int progressIncrement) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentProgress = sharedPreferences.getInt(PROGRESS_KEY, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, currentProgress + progressIncrement);  // Increment progress
        editor.apply();
    }

    // Method to mark the step as completed
    private void markStepAsCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FITNESS_DECLARATION_2_COMPLETED, true);  // Mark step as completed
        editor.apply();
    }
}
