package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailActivity extends AppCompatActivity {

    private LinearLayout exercisesContainer;
    private TextView detailSave;
    private List<AdaptersExercise> adaptersExerciseList = new ArrayList<>();
    private int workoutId;
    private String sessionToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        exercisesContainer = findViewById(R.id.exercises_linear_layout);
        detailSave = findViewById(R.id.detailSave);

        // Get the workout_id and session_token from the intent
        Intent intent = getIntent();
        workoutId = intent.getIntExtra("workout_id", -1);  // Default to -1 if missing
        sessionToken = intent.getStringExtra("session_token");  // May return null if missing

        // Log the received values for debugging
        Log.d("WorkoutDetailActivity", "Received workout_id: " + workoutId);
        Log.d("WorkoutDetailActivity", "Received session_token: " + sessionToken);

        if (workoutId != -1) {
            if (sessionToken != null) {
                // Fetch exercises from the server with the session token
                fetchExercises(workoutId, sessionToken);
            } else {
                // Handle missing session token (e.g., show a message or skip server interaction)
                Log.e("WorkoutDetailActivity", "Session token is missing, limited functionality available.");
                Toast.makeText(this, "Session token is missing. Some features may be unavailable.", Toast.LENGTH_SHORT).show();

                // You might still fetch local data or show static workout details
                fetchWorkoutDetailsLocally(workoutId);
            }
        } else {
            Log.e("WorkoutDetailActivity", "Invalid workout_id.");
            Toast.makeText(this, "Invalid workout ID.", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if workout_id is missing
        }

        // Save button to finish the activity
        detailSave.setOnClickListener(v -> finish());
    }

    // Fetch workout details locally if no session token is available
    private void fetchWorkoutDetailsLocally(int workoutId) {
        // Your logic to fetch local data
        Log.d("WorkoutDetailActivity", "Fetching workout details locally for workout ID: " + workoutId);
    }

    // Example fetchExercises method with session token
    private void fetchExercises(int workoutId, String sessionToken) {
        Log.d("WorkoutDetailActivity", "Fetching exercises for workout ID: " + workoutId);

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        ApiService exerciseApi = retrofit.create(ApiService.class);

        // Make the call to fetch exercises using the session token
        Call<ExerciseResponse> call = exerciseApi.getExercises(sessionToken, workoutId);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adaptersExerciseList = response.body().getExercises();

                    // Display the fetched exercises
                    displayExercises(adaptersExerciseList);
                } else {
                    Log.e("WorkoutDetailActivity", "Failed to fetch exercises.");
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                Log.e("WorkoutDetailActivity", "Error fetching exercises: " + t.getMessage());
            }
        });
    }



    private void updateExercisesOnServer() {
        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        ApiService exerciseApi = retrofit.create(ApiService.class);

        // Convert the list of exercises to JSON
        Gson gson = new Gson();
        String exercisesJson = gson.toJson(adaptersExerciseList);

        Call<Void> call = exerciseApi.updateExercises(sessionToken, workoutId, exercisesJson);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(WorkoutDetailActivity.this, "Exercises updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity after saving
                } else {
                    Toast.makeText(WorkoutDetailActivity.this, "Failed to update exercises.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(WorkoutDetailActivity.this, "Error updating exercises: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWorkoutDetails(int workoutId) {
        // Fetch workout details from the server or database using the workoutId
        // For example, make an API call here
        Log.d("WorkoutDetailActivity", "Fetching details for workout ID: " + workoutId);
    }




    // Method to display the fetched exercises in the LinearLayout
    private void displayExercises(List<AdaptersExercise> exercises) {
        for (AdaptersExercise adaptersExercise : exercises) {
            addExerciseToContainer(adaptersExercise);
        }
    }

    // Method to add an exercise to the container
    private void addExerciseToContainer(AdaptersExercise adaptersExercise) {
        // Create a TextView for exercise details
        TextView exerciseTextView = new TextView(this);
        exerciseTextView.setText(formatExerciseDetails(adaptersExercise));
        exerciseTextView.setTextColor(getResources().getColor(R.color.white));
        exerciseTextView.setTextSize(18);
        exerciseTextView.setPadding(32, 8, 32, 8);

        // Set layout parameters for the exercise TextView
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16); // Adding vertical margins between exercises
        exerciseTextView.setLayoutParams(layoutParams);

        // Add the TextView to the container
        exercisesContainer.addView(exerciseTextView);

        // Create a CheckBox for marking the exercise as done
        CheckBox doneCheckbox = new CheckBox(this);
        doneCheckbox.setChecked(adaptersExercise.isDone());  // Set the checkbox based on isDone status
        doneCheckbox.setText("Completed");

        // Apply the color state list to change the checkbox tint color to orange when checked
        doneCheckbox.setButtonTintList(getResources().getColorStateList(R.color.custom_orange));  // Use the color state list
        doneCheckbox.setTextColor(getResources().getColor(R.color.white));

        // Set layout parameters for the CheckBox
        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkboxParams.setMargins(0, 8, 0, 55); // Adding margins below the checkbox
        doneCheckbox.setLayoutParams(checkboxParams);

        // Add the CheckBox to the container
        exercisesContainer.addView(doneCheckbox);

        // Update the "isDone" status when the checkbox is checked/unchecked
        doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adaptersExercise.setDone(isChecked);  // Update the exercise "isDone" status in the model
        });
    }


    private String formatExerciseDetails(AdaptersExercise exercise) {
        return "Exercise: " + exercise.getName() +
                "\nSets: " + exercise.getSets() +
                "\nReps: " + exercise.getReps();
    }
}
