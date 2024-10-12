package com.example.heavymetals.Home_LandingPage.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
    private TextView bmiTextView;
    private Button editMeasurementsButton;
    private boolean isEditing = false; // Flag to track edit mode
    private Map<String, String> originalValues = new HashMap<>(); // To store original values

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        // Initialize your views
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
        // Send the updated measurements back to the server
        Toast.makeText(this, "Measurements saved successfully.", Toast.LENGTH_SHORT).show();
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

        // Get the BMI value
        double bmiValue = measurements.getDouble("bmi");

        // Update BMI with classification and color the BMI value
        String bmiClassification = classifyBMI(bmiValue);
        bmiTextView.setText(createColoredText("BMI: ", bmiValue, " (" + bmiClassification + ")", customOrangeColor));

        // Update the other TextViews and color only the numbers (labels include the unit)
        weightTextView.setText(createColoredText("Body Weight(kg): ", measurements.getDouble("body_weight"), "", customOrangeColor));
        heightTextView.setText(createColoredText("Height(cm): ", measurements.getDouble("height"), "", customOrangeColor));
        chestTextView.setText(createColoredText("Chest(cm): ", measurements.getDouble("chest"), "", customOrangeColor));
        shoulderTextView.setText(createColoredText("Shoulder(cm): ", measurements.getDouble("shoulder"), "", customOrangeColor));
        waistTextView.setText(createColoredText("Waist(cm): ", measurements.getDouble("waist"), "", customOrangeColor));
        hipsTextView.setText(createColoredText("Hips(cm): ", measurements.getDouble("hips"), "", customOrangeColor));
        leftBicepTextView.setText(createColoredText("L Bicep(cm): ", measurements.getDouble("left_bicep"), "", customOrangeColor));
        rightBicepTextView.setText(createColoredText("R Bicep(cm): ", measurements.getDouble("right_bicep"), "", customOrangeColor));
        leftForearmTextView.setText(createColoredText("L Forearm(cm): ", measurements.getDouble("left_forearm"), "", customOrangeColor));
        rightForearmTextView.setText(createColoredText("R Forearm(cm): ", measurements.getDouble("right_forearm"), "", customOrangeColor));
        leftCalfTextView.setText(createColoredText("L Calf(cm): ", measurements.getDouble("left_calf"), "", customOrangeColor));
        rightCalfTextView.setText(createColoredText("R Calf(cm): ", measurements.getDouble("right_calf"), "", customOrangeColor));
    }


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

    private SpannableString createColoredText(String label, double value, String unit, int color) {
        // Remove the unit from the value part, keeping it only in the label
        String fullText = label + String.format("%.2f", value); // No need for `unit` after the value
        SpannableString spannableString = new SpannableString(fullText);

        // Color only the numeric part of the string
        int start = label.length();  // Start coloring after the label
        int end = start + String.format("%.2f", value).length();  // Color only the number

        spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

}
