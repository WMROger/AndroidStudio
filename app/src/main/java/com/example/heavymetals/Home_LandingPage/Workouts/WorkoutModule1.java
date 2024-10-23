package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkoutModule1 extends Fragment {

    private TextView planWorkoutCounter_txt;
    private Button button2, button3, button4, button5, button10, button11;
    private ArrayList<Exercise> selectedExercises; // Initialize the list

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_plan, container, false);

        // Initialize goal buttons
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);
        button5 = view.findViewById(R.id.button5);
        button10 = view.findViewById(R.id.button10);
        button11 = view.findViewById(R.id.button11);

        // Initialize selectedExercises list
        selectedExercises = new ArrayList<>();

        // Initially hide all goal buttons
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        button4.setVisibility(View.GONE);
        button5.setVisibility(View.GONE);
        button10.setVisibility(View.GONE);
        button11.setVisibility(View.GONE);

        // Fetch goals from the server
        fetchGoalsFromDatabase();

        return view;
    }

    private void fetchGoalsFromDatabase() {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/user_details/get_user_goals.php";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            JSONObject goals = jsonResponse.getJSONObject("goals");

                            // Update button visibility based on returned goals
                            updateButtonVisibility(goals);
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch goals.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Network error.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Retrieve the logged-in user ID from SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
                String userId = sharedPreferences.getString("user_id", null);

                Map<String, String> params = new HashMap<>();
                if (userId != null) {
                    params.put("user_id", userId);
                } else {
                    Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
                }
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void updateButtonVisibility(JSONObject goals) throws JSONException {
        // Show or hide buttons based on the returned goals
        toggleButtonVisibility(button2, goals.getInt("lose_weight") == 1, new Exercise(120, "Lose Weight", "Goal", "image_url"));
        toggleButtonVisibility(button3, goals.getInt("fitness") == 1, new Exercise(125, "Fitness", "Goal", "image_url"));
        toggleButtonVisibility(button4, goals.getInt("wellness") == 1, new Exercise(124, "Wellness", "Goal", "image_url"));
        toggleButtonVisibility(button5, goals.getInt("mobility") == 1, new Exercise(123, "Mobility", "Goal", "image_url"));
        toggleButtonVisibility(button10, goals.getInt("build_muscle") == 1, new Exercise(122, "Build Muscle", "Goal", "image_url"));
        toggleButtonVisibility(button11, goals.getInt("increase_strength") == 1, new Exercise(121, "Increase Strength", "Goal", "image_url"));
    }

    private void toggleButtonVisibility(Button button, boolean isVisible, Exercise goal) {
        if (isVisible) {
            button.setVisibility(View.VISIBLE);
            setupToggleButton(button, goal);  // Set up click listener if button is shown
        } else {
            button.setVisibility(View.GONE);  // Hide button if the goal is not selected
        }
    }

    // Modify this method to work with Button instead of ImageButton
    private void setupToggleButton(Button button, Exercise goal) {
        if (selectedExercises.contains(goal)) {
            button.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Orange background
            button.setTag(R.color.custom_orange);
        } else {
            button.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Black background
            button.setTag(R.color.black);
        }

        button.setOnClickListener(v -> {
            int currentColor = (int) button.getTag();
            if (currentColor == R.color.black) {
                button.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Change to orange
                button.setTag(R.color.custom_orange);
                selectedExercises.add(goal);
            } else {
                button.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Change back to black
                button.setTag(R.color.black);
                selectedExercises.remove(goal);
            }
        });
    }
}
