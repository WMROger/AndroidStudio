package com.example.heavymetals.Home_LandingPage.Settings;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
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

public class GoalsShowActivity extends AppCompatActivity {

    private Button loseWeightButton, increaseStrengthButton, buildMuscleButton, mobilityButton, wellnessButton, fitnessButton;
    private TextView bck_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_show);

        // Initialize buttons
        bck_txt = findViewById(R.id.backtxt);
        loseWeightButton = findViewById(R.id.button);
        increaseStrengthButton = findViewById(R.id.button6);
        buildMuscleButton = findViewById(R.id.button7);
        mobilityButton = findViewById(R.id.button8);
        wellnessButton = findViewById(R.id.button9);
        fitnessButton = findViewById(R.id.button14);

        // Fetch user goals from the server
        fetchUserGoals();

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bck_txt.setOnClickListener(view -> {
            finish();
        });
    }

    private void fetchUserGoals() {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/user_details/get_user_goals.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONObject userGoals = jsonResponse.getJSONObject("user_goals");

                            // Highlight buttons based on the fetched goals
                            highlightSelectedGoals(userGoals);
                        } else {
                            Toast.makeText(GoalsShowActivity.this, "Failed to fetch goals.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(GoalsShowActivity.this, "Failed to parse response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(GoalsShowActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", "1"); // Replace with dynamic user_id
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void highlightSelectedGoals(JSONObject userGoals) throws JSONException {
        // Check each goal and highlight the corresponding button if the value is 1
        updateButtonStyle(loseWeightButton, userGoals.getInt("lose_weight") == 1);
        updateButtonStyle(increaseStrengthButton, userGoals.getInt("increase_strength") == 1);
        updateButtonStyle(buildMuscleButton, userGoals.getInt("build_muscle") == 1);
        updateButtonStyle(mobilityButton, userGoals.getInt("mobility") == 1);
        updateButtonStyle(wellnessButton, userGoals.getInt("wellness_reduce_stress") == 1);
        updateButtonStyle(fitnessButton, userGoals.getInt("fitness") == 1);
    }

    // Update button style based on goal selection
    private void updateButtonStyle(Button button, boolean isSelected) {
        if (isSelected) {
            // Set background to orange and text/icon color to black
            button.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Orange background
            button.setTextColor(Color.BLACK); // Black text
            button.setCompoundDrawableTintList(getResources().getColorStateList(R.color.black)); // Black icon tint
        } else {
            // Set default background and text/icon color
            button.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Black background
            button.setTextColor(Color.WHITE); // White text
            button.setCompoundDrawableTintList(getResources().getColorStateList(R.color.custom_orange)); // Orange icon tint
        }
    }
}
