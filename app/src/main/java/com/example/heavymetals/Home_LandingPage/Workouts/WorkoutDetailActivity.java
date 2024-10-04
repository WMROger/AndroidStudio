package com.example.heavymetals.Home_LandingPage.Workouts;

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
        workoutId = getIntent().getIntExtra("workout_id", -1);
        sessionToken = getIntent().getStringExtra("session_token");

        // Add debugging to check the received workout ID
        Log.d("WorkoutDetailActivity", "Received workout_id: " + workoutId);

        if (workoutId != -1) {
            fetchExercises(workoutId, sessionToken);  // Fetch exercises based on the workoutId
        } else {
            Log.e("WorkoutDetailActivity", "Invalid workout_id or session_token.");
        }
        detailSave.setOnClickListener(v -> {
            // Send the updated exercise statuses back to the server
//            updateExercisesOnServer();
            finish();
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




    private void fetchExercises(int workoutId, String sessionToken) {
        Log.d("WorkoutDetailActivity", "Fetching exercises for workout ID: " + workoutId);

        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        ApiService exerciseApi = retrofit.create(ApiService.class);

        // Make the call to fetch exercises
        Call<ExerciseResponse> call = exerciseApi.getExercises(sessionToken, workoutId);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adaptersExerciseList = response.body().getExercises();

                    // Debugging to log the number of exercises fetched
                    Log.d("WorkoutDetailActivity", "Number of exercises fetched: " + adaptersExerciseList.size());

                    displayExercises(adaptersExerciseList);  // Display the fetched exercises
                } else {
                    Log.e("WorkoutDetailActivity", "Failed to fetch exercises.");
                    if (response.body() != null) {
                        Log.e("WorkoutDetailActivity", "Error message: " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                Log.e("WorkoutDetailActivity", "Error fetching exercises: " + t.getMessage());
            }
        });
    }



    // Method to display the fetched exercises in the LinearLayout
    private void displayExercises(List<AdaptersExercise> exercises) {
        for (AdaptersExercise adaptersExercise : exercises) {
            addExerciseToContainer(adaptersExercise);
        }
    }

    // Existing method to add an exercise to the container
    private void addExerciseToContainer(AdaptersExercise adaptersExercise) {
        TextView exerciseTextView = new TextView(this);
        exerciseTextView.setText(formatExerciseDetails(adaptersExercise));
        exerciseTextView.setTextColor(getResources().getColor(R.color.white));
        exerciseTextView.setTextSize(18);
        exerciseTextView.setPadding(32, 8, 32, 8);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16); // Adding vertical margins between exercises
        exerciseTextView.setLayoutParams(layoutParams);

        exercisesContainer.addView(exerciseTextView);

        CheckBox doneCheckbox = new CheckBox(this);
        doneCheckbox.setChecked(adaptersExercise.isDone());
        doneCheckbox.setText("Completed");
        doneCheckbox.setButtonTintList(getResources().getColorStateList(R.color.white));
        doneCheckbox.setTextColor(getResources().getColor(R.color.white));

        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkboxParams.setMargins(0, 8, 0, 55); // Adding margins below the checkbox
        doneCheckbox.setLayoutParams(checkboxParams);

        doneCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            adaptersExercise.setDone(isChecked);  // Update the exercise "isDone" status
        });

        exercisesContainer.addView(doneCheckbox);
    }

    private String formatExerciseDetails(AdaptersExercise exercise) {
        return "Exercise: " + exercise.getName() +
                "\nSets: " + exercise.getSets() +
                "\nReps: " + exercise.getReps() +
                "\nDone: " + (exercise.isDone() ? "Yes" : "No");
    }
}
