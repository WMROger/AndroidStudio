package com.example.heavymetals.Home_LandingPage.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MeasurementsActivity extends AppCompatActivity {

    private EditText weightTextView, heightTextView, chestTextView, shoulderTextView,
            waistTextView, hipsTextView, leftBicepTextView, rightBicepTextView, leftForearmTextView,
            rightForearmTextView, leftCalfTextView, rightCalfTextView;
    private TextView bmiTextView, back_profile;
    private Button editMeasurementsButton;
    private boolean isEditing = false; // Flag to track edit mode
    private Map<String, String> originalValues = new HashMap<>(); // To store original values

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        // Initialize your views
        back_profile = findViewById(R.id.back_profile);
        bmiTextView = findViewById(R.id.View_BMI);
        weightTextView = findViewById(R.id.View_BodyWeight);
        heightTextView = findViewById(R.id.View_Height);
        chestTextView = findViewById(R.id.View_Chest);
        shoulderTextView = findViewById(R.id.View_Shoulder);
        waistTextView = findViewById(R.id.View_Waist);
        hipsTextView = findViewById(R.id.View_Hips);
        leftBicepTextView = findViewById(R.id.View_LBicep);
        rightBicepTextView = findViewById(R.id.View_RBicep);
        leftForearmTextView = findViewById(R.id.View_LForearm);
        rightForearmTextView = findViewById(R.id.View_RForearm);
        leftCalfTextView = findViewById(R.id.View_LCalf);
        rightCalfTextView = findViewById(R.id.View_RCalf);
        editMeasurementsButton = findViewById(R.id.Edit_Measurements);


        // Clear text when user clicks on each EditText for the first time
        clearTextOnFirstClick(weightTextView);
        clearTextOnFirstClick(heightTextView);
        clearTextOnFirstClick(chestTextView);
        clearTextOnFirstClick(shoulderTextView);
        clearTextOnFirstClick(waistTextView);
        clearTextOnFirstClick(hipsTextView);
        clearTextOnFirstClick(leftBicepTextView);
        clearTextOnFirstClick(rightBicepTextView);
        clearTextOnFirstClick(leftForearmTextView);
        clearTextOnFirstClick(rightForearmTextView);
        clearTextOnFirstClick(leftCalfTextView);
        clearTextOnFirstClick(rightCalfTextView);


        // Fetch the measurements from the server
        fetchMeasurements();

        // Initially, make all fields non-editable except for BMI
        disableEditing();

        // Set click listener for the "Edit Measurements" button
        editMeasurementsButton.setOnClickListener(v -> toggleEditMode());

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        back_profile.setOnClickListener(view -> {
            finish();
        });
    }

    private void clearTextOnFirstClick(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && editText.getTag() == null) {
                // Clear the text on the first click
                editText.setText("");
                // Mark it as already cleared so we don't clear it again
                editText.setTag("cleared");
            }
        });
    }

    private void toggleEditMode() {
        if (isEditing) {
            // Save the changes
            saveMeasurements();
            disableEditing();  // Disable fields after saving
            editMeasurementsButton.setText("Edit Measurements");
        } else {
            // Enable editing and store the current values in case the user cancels
            storeOriginalValues();
            enableEditing();  // Enable fields when user clicks "Edit"
            editMeasurementsButton.setText("Save");
        }
        isEditing = !isEditing; // Toggle the edit mode
    }

    private void storeOriginalValues() {
        // Store the original values before enabling edit mode
        originalValues.put("weight", weightTextView.getText().toString());
        originalValues.put("height", heightTextView.getText().toString());
        originalValues.put("chest", chestTextView.getText().toString());
        originalValues.put("shoulder", shoulderTextView.getText().toString());
        originalValues.put("waist", waistTextView.getText().toString());
        originalValues.put("hips", hipsTextView.getText().toString());
        originalValues.put("left_bicep", leftBicepTextView.getText().toString());
        originalValues.put("right_bicep", rightBicepTextView.getText().toString());
        originalValues.put("left_forearm", leftForearmTextView.getText().toString());
        originalValues.put("right_forearm", rightForearmTextView.getText().toString());
        originalValues.put("left_calf", leftCalfTextView.getText().toString());
        originalValues.put("right_calf", rightCalfTextView.getText().toString());
    }

    private void restoreOriginalValues() {
        // Restore the original values if editing is canceled
        weightTextView.setText(originalValues.get("weight"));
        heightTextView.setText(originalValues.get("height"));
        chestTextView.setText(originalValues.get("chest"));
        shoulderTextView.setText(originalValues.get("shoulder"));
        waistTextView.setText(originalValues.get("waist"));
        hipsTextView.setText(originalValues.get("hips"));
        leftBicepTextView.setText(originalValues.get("left_bicep"));
        rightBicepTextView.setText(originalValues.get("right_bicep"));
        leftForearmTextView.setText(originalValues.get("left_forearm"));
        rightForearmTextView.setText(originalValues.get("right_forearm"));
        leftCalfTextView.setText(originalValues.get("left_calf"));
        rightCalfTextView.setText(originalValues.get("right_calf"));
    }

    private void enableEditing() {
        // Enable all the EditText fields except for BMI
        weightTextView.setEnabled(true);
        weightTextView.setFocusableInTouchMode(true);

        heightTextView.setEnabled(true);
        heightTextView.setFocusableInTouchMode(true);

        chestTextView.setEnabled(true);
        chestTextView.setFocusableInTouchMode(true);

        shoulderTextView.setEnabled(true);
        shoulderTextView.setFocusableInTouchMode(true);

        waistTextView.setEnabled(true);
        waistTextView.setFocusableInTouchMode(true);

        hipsTextView.setEnabled(true);
        hipsTextView.setFocusableInTouchMode(true);

        leftBicepTextView.setEnabled(true);
        leftBicepTextView.setFocusableInTouchMode(true);

        rightBicepTextView.setEnabled(true);
        rightBicepTextView.setFocusableInTouchMode(true);

        leftForearmTextView.setEnabled(true);
        leftForearmTextView.setFocusableInTouchMode(true);

        rightForearmTextView.setEnabled(true);
        rightForearmTextView.setFocusableInTouchMode(true);

        leftCalfTextView.setEnabled(true);
        leftCalfTextView.setFocusableInTouchMode(true);

        rightCalfTextView.setEnabled(true);
        rightCalfTextView.setFocusableInTouchMode(true);
    }

    private void disableEditing() {
        // Disable all the EditText fields
        weightTextView.setEnabled(false);
        heightTextView.setEnabled(false);
        chestTextView.setEnabled(false);
        shoulderTextView.setEnabled(false);
        waistTextView.setEnabled(false);
        hipsTextView.setEnabled(false);
        leftBicepTextView.setEnabled(false);
        rightBicepTextView.setEnabled(false);
        leftForearmTextView.setEnabled(false);
        rightForearmTextView.setEnabled(false);
        leftCalfTextView.setEnabled(false);
        rightCalfTextView.setEnabled(false);

        // BMI remains non-editable (it's already a TextView)
    }

    private void saveMeasurements() {
        // First, calculate the new BMI before saving
        double weight = Double.parseDouble(weightTextView.getText().toString());
        double height = Double.parseDouble(heightTextView.getText().toString()) / 100; // Convert cm to meters
        double bmi = calculateBMI(weight, height);

        // Send the updated measurements and BMI back to the server
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/user_details/update_measurements.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Log the raw response for debugging
                    Log.d("ServerResponse", "Response: " + response);

                    try {
                        // Try to parse the JSON response
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            Toast.makeText(MeasurementsActivity.this, "Measurements updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MeasurementsActivity.this, "Failed to update measurements.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MeasurementsActivity.this, "Failed to parse JSON response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MeasurementsActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userId = sharedPreferences.getString("user_id", null);

                if (userId != null) {
                    params.put("user_id", userId);
                    params.put("body_weight", weightTextView.getText().toString());
                    params.put("height", heightTextView.getText().toString());
                    params.put("chest", chestTextView.getText().toString());
                    params.put("shoulder", shoulderTextView.getText().toString());
                    params.put("waist", waistTextView.getText().toString());
                    params.put("hips", hipsTextView.getText().toString());
                    params.put("left_bicep", leftBicepTextView.getText().toString());
                    params.put("right_bicep", rightBicepTextView.getText().toString());
                    params.put("left_forearm", leftForearmTextView.getText().toString());
                    params.put("right_forearm", rightForearmTextView.getText().toString());
                    params.put("left_calf", leftCalfTextView.getText().toString());
                    params.put("right_calf", rightCalfTextView.getText().toString());
                    params.put("bmi", String.format("%.2f", bmi));  // Send calculated BMI
                }
                return params;
            }
        };

    // Add the request to the RequestQueue
        queue.add(stringRequest);
    }

    // Method to calculate BMI
    private double calculateBMI(double weight, double heightInMeters) {
        return weight / (heightInMeters * heightInMeters);
    }



    private void fetchMeasurements() {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/user_details/get_measurements.php";  // Replace with your server URL
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if (success) {
                            // Get the measurements object from the JSON response
                            JSONObject measurements = jsonResponse.getJSONObject("measurements");

                            // Update the UI with the fetched data
                            updateUIWithMeasurements(measurements);

                        } else {
                            Toast.makeText(MeasurementsActivity.this, "Failed to retrieve measurements.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MeasurementsActivity.this, "Failed to parse JSON response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(MeasurementsActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Send user_id in the POST request
                Map<String, String> params = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userId = sharedPreferences.getString("user_id", null);

                // Check if user_id exists
                if (userId != null) {
                    params.put("user_id", userId);
                }
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void updateUIWithMeasurements(JSONObject measurements) throws JSONException {
        int customOrangeColor = getResources().getColor(R.color.custom_orange);

        // Retrieve the BMI value from the measurements
        double bmiValue = measurements.getDouble("bmi");
        String bmiClassification = classifyBMI(bmiValue);
        setColoredText(bmiTextView, "BMI: ", String.format("%.2f", bmiValue) + " (" + bmiClassification + ")", customOrangeColor);

        // Set the other measurement fields using the helper method
        setColoredText(weightTextView, "Body Weight(kg): ", String.format("%.2f", measurements.getDouble("body_weight")), customOrangeColor);
        setColoredText(heightTextView, "Height(cm): ", String.format("%.2f", measurements.getDouble("height")), customOrangeColor);
        setColoredText(chestTextView, "Chest(cm): ", String.format("%.2f", measurements.getDouble("chest")), customOrangeColor);
        setColoredText(shoulderTextView, "Shoulder(cm): ", String.format("%.2f", measurements.getDouble("shoulder")), customOrangeColor);
        setColoredText(waistTextView, "Waist(cm): ", String.format("%.2f", measurements.getDouble("waist")), customOrangeColor);
        setColoredText(hipsTextView, "Hips(cm): ", String.format("%.2f", measurements.getDouble("hips")), customOrangeColor);
        setColoredText(leftBicepTextView, "L Bicep(cm): ", String.format("%.2f", measurements.getDouble("left_bicep")), customOrangeColor);
        setColoredText(rightBicepTextView, "R Bicep(cm): ", String.format("%.2f", measurements.getDouble("right_bicep")), customOrangeColor);
        setColoredText(leftForearmTextView, "L Forearm(cm): ", String.format("%.2f", measurements.getDouble("left_forearm")), customOrangeColor);
        setColoredText(rightForearmTextView, "R Forearm(cm): ", String.format("%.2f", measurements.getDouble("right_forearm")), customOrangeColor);
        setColoredText(leftCalfTextView, "L Calf(cm): ", String.format("%.2f", measurements.getDouble("left_calf")), customOrangeColor);
        setColoredText(rightCalfTextView, "R Calf(cm): ", String.format("%.2f", measurements.getDouble("right_calf")), customOrangeColor);
    }

    // Helper method to create a SpannableString and set it on a TextView
    private void setColoredText(TextView textView, String label, String value, int color) {
        SpannableString spannable = new SpannableString(label + value);
        spannable.setSpan(new ForegroundColorSpan(color), label.length(), (label.length() + value.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannable);
    }

    // Method to classify BMI
    private String classifyBMI(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "Normal";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

}
