package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.heavymetals.Models.Exercise; // Ensure the correct import
import com.example.heavymetals.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Exercises_All extends AppCompatActivity {

    private Button FEPAddExercise;
    private ArrayList<Exercise> selectedExercises = new ArrayList<>(); // Store Exercise objects

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_all);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        FEPAddExercise = findViewById(R.id.FEPAddExercise);

        // Set click listener for Add Exercise button
        FEPAddExercise.setOnClickListener(v -> {
            Intent intent = new Intent(Exercises_All.this, WorkoutModule2.class);
            intent.putExtra("selectedExercises", selectedExercises);  // Pass selected exercises
            startActivityForResult(intent, 100);  // Use request code to get result back
        });

        fetchExercises();
    }

    // Handle the result from WorkoutModule2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedExercises = (ArrayList<Exercise>) data.getSerializableExtra("selectedExercises");
            // Update the UI accordingly
        }
    }

    private void setupToggleButton(ImageButton button, String exerciseName) {
        if (selectedExercises.stream().anyMatch(exercise -> exercise.getName().equals(exerciseName))) {
            button.setImageResource(R.drawable.additem_orange);  // Already selected
            button.setTag(R.drawable.additem_orange);
        } else {
            button.setImageResource(R.drawable.additem_black);  // Not selected
            button.setTag(R.drawable.additem_black);
        }

        button.setOnClickListener(v -> {
            int currentIcon = (int) button.getTag();

            if (currentIcon == R.drawable.additem_black) {
                button.setImageResource(R.drawable.additem_orange);  // Change to selected state
                button.setTag(R.drawable.additem_orange);

                // Create a new Exercise object to add to the selectedExercises
                Exercise newExercise = new Exercise(exerciseName, "Description here", "Image URL here"); // Update as needed
                selectedExercises.add(newExercise);  // Add exercise to list
            } else {
                button.setImageResource(R.drawable.additem_black);  // Change to deselected state
                button.setTag(R.drawable.additem_black);

                // Remove the exercise from the list
                selectedExercises.removeIf(exercise -> exercise.getName().equals(exerciseName));  // Remove exercise from list
            }
        });

        // Initially set to black icon (unselected)
        button.setTag(R.drawable.additem_black);
    }

    private void fetchExercises() {
        // Create a request to the server to get the exercises
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/exercises_list/get_exercises_list.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Handle the JSON response
                    try {
                        JSONArray exercisesArray = response.getJSONArray("exercises");
                        // Find the LinearLayout where we'll be adding exercise views
                        LinearLayout exercisesLayout = findViewById(R.id.scrollViewLinearLayout);  // Assuming this is where to add views

                        for (int i = 0; i < exercisesArray.length(); i++) {
                            JSONObject exercise = exercisesArray.getJSONObject(i);
                            String name = exercise.getString("exercise_name");
                            String description = exercise.getString("description");
                            String imageUrl = exercise.getString("image_url");  // Add image URL

                            // Inflate a new RelativeLayout for each exercise
                            RelativeLayout exerciseItemLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.exercise_item_layout, null);

                            // Set the text for the exercise name and description
                            TextView exerciseNameText = exerciseItemLayout.findViewById(R.id.exercise_name);
                            TextView exerciseDescriptionText = exerciseItemLayout.findViewById(R.id.exercise_description);
                            exerciseNameText.setText(name);
                            exerciseDescriptionText.setText(description);

                            // Handle button toggle behavior
                            ImageButton toggleButton = exerciseItemLayout.findViewById(R.id.addItemBtn);
                            setupToggleButton(toggleButton, name);

                            // Load the image using Glide
                            ImageView exerciseImageView = exerciseItemLayout.findViewById(R.id.exercise_image);
                            Glide.with(Exercises_All.this)
                                    .load(imageUrl)  // Image URL from the server
                                    .placeholder(R.drawable.human_icon)  // Fallback image if the URL fails
                                    .into(exerciseImageView);  // Set the ImageView

                            // Add the dynamically created view to the exercises layout
                            exercisesLayout.addView(exerciseItemLayout);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error
                    error.printStackTrace();
                }
        );

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
