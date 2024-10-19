package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Exercises_All extends AppCompatActivity {

    private static final String TAG = "Exercises_All";  // Add a tag for logging
    private Button FEPAddExercise;
    private ArrayList<Exercise> selectedExercises = new ArrayList<>();  // Store Exercise objects
    private ArrayList<Exercise> allExercises = new ArrayList<>();  // Declare allExercises

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

        // Log start of exercise fetching
        Log.d(TAG, "onCreate: Fetching exercises");
        fetchExercises();
    }

    // Handle the result from WorkoutModule2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedExercises = (ArrayList<Exercise>) data.getSerializableExtra("selectedExercises");
            Log.d(TAG, "onActivityResult: Selected exercises updated: " + selectedExercises.size());
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
                Exercise newExercise = new Exercise(exerciseName, "Category", "Image URL here");
                selectedExercises.add(newExercise);  // Add exercise to list
                Log.d(TAG, "setupToggleButton: Exercise added: " + exerciseName);
            } else {
                button.setImageResource(R.drawable.additem_black);  // Change to deselected state
                button.setTag(R.drawable.additem_black);
                selectedExercises.removeIf(exercise -> exercise.getName().equals(exerciseName));
                Log.d(TAG, "setupToggleButton: Exercise removed: " + exerciseName);
            }
        });
        button.setTag(R.drawable.additem_black);
    }

    private void filterExercisesByCategory(String category) {
        LinearLayout exercisesLayout = findViewById(R.id.scrollViewLinearLayout);
        exercisesLayout.removeAllViews();  // Clear current views
        Log.d(TAG, "filterExercisesByCategory: Filtering by category: " + category);

        for (Exercise exercise : allExercises) {
            // Show all exercises if "All" is selected, or filter by category
            if (category.equals("All") || exercise.getCategory().equals(category)) {
                addExerciseToView(exercise);  // Add the exercise to the view
                Log.d(TAG, "filterExercisesByCategory: Exercise added to view: " + exercise.getName());
            }
        }
    }

    private void addExerciseToView(Exercise exercise) {
        LinearLayout exercisesLayout = findViewById(R.id.scrollViewLinearLayout);
        RelativeLayout exerciseItemLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.exercise_item_layout, null);

        // Set the text for the exercise name and description
        TextView exerciseNameText = exerciseItemLayout.findViewById(R.id.exercise_name);
        TextView exerciseDescriptionText = exerciseItemLayout.findViewById(R.id.exercise_description);
        exerciseNameText.setText(exercise.getName());
        exerciseDescriptionText.setText(exercise.getCategory());

        // Handle button toggle behavior
        ImageButton toggleButton = exerciseItemLayout.findViewById(R.id.addItemBtn);
        setupToggleButton(toggleButton, exercise.getName());

        // Load the image using Glide
        ImageView exerciseImageView = exerciseItemLayout.findViewById(R.id.exercise_image);
        Glide.with(Exercises_All.this)
                .load(exercise.getImageUrl())
                .placeholder(R.drawable.human_icon)
                .error(R.drawable.orange_border)
                .skipMemoryCache(true)  // Skip memory cache
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Skip disk cache
                .into(exerciseImageView);


        exercisesLayout.addView(exerciseItemLayout);
        Log.d(TAG, "addExerciseToView: Exercise added to layout: " + exercise.getName());
    }

    private void fetchExercises() {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/exercises_list/get_exercises_list.php";
        Log.d(TAG, "fetchExercises: Making request to: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG, "fetchExercises: Received response");
                    try {
                        JSONArray exercisesArray = response.getJSONArray("exercises");

                        for (int i = 0; i < exercisesArray.length(); i++) {
                            JSONObject exerciseJson = exercisesArray.getJSONObject(i);
                            String name = exerciseJson.getString("exercise_name");

                            // Use optString instead of getString to handle missing category field
                            String category = exerciseJson.optString("category", "Unknown");  // Default to "Unknown" if not present

                            String imageUrl = exerciseJson.optString("image_url", "");  // Handle image_url similarly
                            Log.d(TAG, "Image URL for " + name + ": " + imageUrl);

                            // Create Exercise objects and add to the list
                            Exercise exercise = new Exercise(name, category, imageUrl);
                            allExercises.add(exercise);  // Add all exercises to the main list
                        }


                        // Initially, show all exercises (default filter)
                        filterExercisesByCategory("All");

                    } catch (JSONException e) {
                        Log.e(TAG, "fetchExercises: JSON parsing error", e);
                    }
                },
                error -> {
                    Log.e(TAG, "fetchExercises: Volley error", error);
                }
        );

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
