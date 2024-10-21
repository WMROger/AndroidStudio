package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WorkoutDetailActivity extends AppCompatActivity {

    private LinearLayout exercisesContainer;
    private TextView detailSave;
    private List<AdaptersExercise> adaptersExerciseList = new ArrayList<>();
    private int workoutId;
    private String sessionToken;

    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String KEY_EXERCISES = "saved_exercises";

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

        Log.d("WorkoutDetailActivity", "Received workout_id: " + workoutId);
        Log.d("WorkoutDetailActivity", "Received session_token: " + sessionToken);

        // Always fetch exercises from the server
        if (workoutId != -1 && sessionToken != null) {
            fetchExercises(workoutId, sessionToken);
        } else {
            Toast.makeText(this, "Invalid workout ID or session token.", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if workout_id or session_token is missing
        }

        // Save button logic
        detailSave.setOnClickListener(v -> {
            if (detailSave.getText().toString().equals("Save")) {
                saveExercisesLocally(adaptersExerciseList);  // Save the updated exercises list locally
                Toast.makeText(this, "Exercises saved locally.", Toast.LENGTH_SHORT).show();

                // Redirect to WorkoutModule4 or finish the current activity
                Intent intent1 = new Intent(WorkoutDetailActivity.this, WorkoutModule4.class); // Update the class with your correct module/activity name
                startActivity(intent1);

                // Optionally, call finish() if you want to close this activity after the redirect
                finish();
            } else {
                finish();  // Close the activity if nothing to save
            }
        });

    }

    // Method to fetch exercises from the server
    private void fetchExercises(int workoutId, String sessionToken) {
        Log.d("WorkoutDetailActivity", "Fetching exercises for workout ID: " + workoutId);

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        ApiService exerciseApi = retrofit.create(ApiService.class);

        Call<ExerciseResponse> call = exerciseApi.getExercises(sessionToken, workoutId);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                Log.d("WorkoutDetailActivity", "Update exercises API response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("WorkoutDetailActivity", "Raw response body: " + response.body());

                    try {
                        List<AdaptersExercise> exercises = response.body().getExercises();

                        if (exercises != null && !exercises.isEmpty()) {
                            // Exercises are valid, display them
                            Log.d("WorkoutDetailActivity", "Exercises update response: " + response.body().getMessage());
                            clearExercisesUI();
                            adaptersExerciseList = exercises;  // Store fetched exercises
                            displayExercises(exercises);

                            // Save fetched exercises locally
                            saveExercisesLocally(exercises);
                        } else {
                            handleEmptyOrNullExercises(exercises);
                        }
                    } catch (Exception e) {
                        Log.e("WorkoutDetailActivity", "Error processing response: " + e.getMessage(), e);
                    }
                } else {
                    Log.e("WorkoutDetailActivity", "Failed to update exercises. Response code: " + response.code());
                    handleResponseError(response);

                    // If the server request fails, load exercises from local storage
                    loadAndDisplayExercisesFromLocal();
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                Log.e("WorkoutDetailActivity", "Error fetching exercises: " + t.getMessage());

                // On failure, load exercises from local storage
                loadAndDisplayExercisesFromLocal();
            }
        });
    }

    // Method to save the list of exercises locally in SharedPreferences, including completed status
    private void saveExercisesLocally(List<AdaptersExercise> exercises) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the list of exercises to JSON, including the "completed" status
        Gson gson = new Gson();
        String exercisesJson = gson.toJson(exercises);

        // Save the JSON string in SharedPreferences
        editor.putString(KEY_EXERCISES, exercisesJson);
        editor.apply();  // Asynchronously save the data

        Log.d("WorkoutDetailActivity", "Exercises saved locally.");
    }

    // Method to load the exercises list from SharedPreferences and display them
    private void loadAndDisplayExercisesFromLocal() {
        List<AdaptersExercise> savedExercises = loadExercisesFromLocal();

        if (!savedExercises.isEmpty()) {
            adaptersExerciseList = savedExercises;
            displayExercises(savedExercises);
        } else {
            Log.d("WorkoutDetailActivity", "No exercises found in local storage.");
        }
    }

    // Load the exercises list from SharedPreferences, including the "completed" status
    private List<AdaptersExercise> loadExercisesFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Retrieve the JSON string from SharedPreferences
        String exercisesJson = sharedPreferences.getString(KEY_EXERCISES, null);

        if (exercisesJson != null) {
            // Convert the JSON string back to a list of exercises, including the "completed" status
            Gson gson = new Gson();
            Type type = new TypeToken<List<AdaptersExercise>>() {}.getType();
            List<AdaptersExercise> exercises = gson.fromJson(exercisesJson, type);

            Log.d("WorkoutDetailActivity", "Exercises loaded from local storage.");
            return exercises;
        }

        return new ArrayList<>();  // Return an empty list if no exercises are found
    }

    // Handle cases where exercises list is null or empty
    private void handleEmptyOrNullExercises(List<AdaptersExercise> exercises) {
        if (exercises == null) {
            Log.d("WorkoutDetailActivity", "Exercises list is null.");
        } else {
            Log.d("WorkoutDetailActivity", "Exercises list is empty.");
        }

        // Display "No exercises available" only when the list is null or empty
        Toast.makeText(WorkoutDetailActivity.this, "No exercises available.", Toast.LENGTH_SHORT).show();
    }

    // Method to display exercises in the UI
    private void displayExercises(List<AdaptersExercise> exercises) {
        // Load saved completed states from local storage
        List<AdaptersExercise> savedExercises = loadExercisesFromLocal();

        for (AdaptersExercise fetchedExercise : exercises) {
            // Check if the fetched exercise exists in the saved list and update the completed status
            for (AdaptersExercise savedExercise : savedExercises) {
                if (fetchedExercise.getName().equals(savedExercise.getName())) {  // Use ID for better comparison if available
                    fetchedExercise.setDone(savedExercise.isDone());
                    break;
                }
            }
            addExerciseToContainer(fetchedExercise);
        }
    }

    // Add exercises dynamically to the UI with checkboxes
    private void addExerciseToContainer(AdaptersExercise adaptersExercise) {
        TextView exerciseTextView = new TextView(this);
        exerciseTextView.setText(formatExerciseDetails(adaptersExercise));
        exerciseTextView.setTextColor(getResources().getColor(R.color.white));
        exerciseTextView.setTextSize(18);
        exerciseTextView.setPadding(32, 8, 32, 8);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16);
        exerciseTextView.setLayoutParams(layoutParams);

        exercisesContainer.addView(exerciseTextView);

        CheckBox doneCheckbox = new CheckBox(this);
        doneCheckbox.setChecked(adaptersExercise.isDone());  // Set the checkbox to reflect the saved state
        doneCheckbox.setText("Completed");
        doneCheckbox.setButtonTintList(getResources().getColorStateList(R.color.custom_orange));
        doneCheckbox.setTextColor(getResources().getColor(R.color.white));

        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkboxParams.setMargins(0, 8, 0, 55);
        doneCheckbox.setLayoutParams(checkboxParams);

        exercisesContainer.addView(doneCheckbox);

        // Update the "isDone" status when the checkbox is checked/unchecked
        doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adaptersExercise.setDone(isChecked);  // Update the exercise "isDone" status in the model
            detailSave.setText("Save");  // Enable save action if a change is made
        });
    }

    private String formatExerciseDetails(AdaptersExercise exercise) {
        return "Exercise: " + exercise.getName() +
                "\nSets: " + exercise.getSets() +
                "\nReps: " + exercise.getReps();
    }

    private void clearExercisesUI() {
        exercisesContainer.removeAllViews();
    }

    private void handleResponseError(Response<ExerciseResponse> response) {
        try {
            if (response.errorBody() != null) {
                Log.e("WorkoutDetailActivity", "Response error body: " + response.errorBody().string());
                Toast.makeText(WorkoutDetailActivity.this, "Failed to update exercises. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("WorkoutDetailActivity", "Response body is null.");
                Toast.makeText(WorkoutDetailActivity.this, "An unknown error occurred. Please try again.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("WorkoutDetailActivity", "Error reading error body: " + e.getMessage(), e);
            Toast.makeText(WorkoutDetailActivity.this, "Error processing the request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
