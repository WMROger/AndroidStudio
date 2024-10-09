package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

public class FitnessDeclaration3 extends AppCompatActivity {
    private TextView Fitness_Declaration_2,BMI;
    private Button btnPFDnext3;
    private Button btn33, btn34,btn27,btn28,btn29;  // Declare the YES and NO buttons
    private String selectedDays; // Declare selectedDays variable here


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

        // Set Listeners for the buttons to behave like a toggle
        btn33.setOnClickListener(v -> {
            // When YES is clicked, change its text color and reset NO button
            btn33.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn34.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color

            // Optional: You can also change the background or add other visual effects
            btn33.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight YES
            btn34.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset NO
        });

        btn34.setOnClickListener(v -> {
            // When NO is clicked, change its text color and reset YES button
            btn34.setTextColor(getResources().getColor(R.color.white)); // Selected color
            btn33.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color

            // Optional: You can also change the background or add other visual effects
            btn34.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight NO
            btn33.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset YES
        });

        // Set Listeners for other navigation buttons
        Fitness_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, FitnessDeclaration2.class);
            startActivity(intent);
        });

        // In your btnPFDnext3 onClickListener, after setting the next intent:
        btnPFDnext3.setOnClickListener(v -> {
            btnPFDnext3.setTextColor(getResources().getColor(R.color.white));

            // Mark this step as completed and update progress
            markStepAsCompleted();
            updateProgress(FITNESS_DECLARATION_3_PROGRESS);

            // Proceed to the next screen (ProfileFinish)
            Intent intent = new Intent(FitnessDeclaration3.this, ProfileFinish.class);
            startActivity(intent);
        });

        // Create an ArrayAdapter using the custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strength_experience_array, R.layout.custom_spinner_item);

        // Specify the custom layout to use for the dropdown list
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);

        // Apply the adapter to the spinner
        spinnerStrengthExperience.setAdapter(adapter);
        setBmiText();
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
        // Retrieve the BMI value from the Intent
        Intent intent = getIntent();
        double bmi = intent.getDoubleExtra("BMI_VALUE", 0.0);  // Default value is 0.0 if not found

        // Display the BMI in the TextView
        BMI.setText(String.format("%.2f", bmi));
    }

}
