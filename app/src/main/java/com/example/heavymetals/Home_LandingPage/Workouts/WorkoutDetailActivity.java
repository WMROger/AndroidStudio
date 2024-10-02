package com.example.heavymetals.Home_LandingPage.Workouts;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.ExerciseResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.example.heavymetals.network.RetrofitClient;
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

        // Find the LinearLayout inside the ScrollView
        exercisesContainer = findViewById(R.id.exercises_linear_layout);
        detailSave = findViewById(R.id.detailSave);

        // Get the workout_id and session_token from the intent
        workoutId = getIntent().getIntExtra("workout_id", -1);
        sessionToken = getIntent().getStringExtra("session_token");

        if (workoutId != -1 && sessionToken != null) {
            fetchExercises(workoutId, sessionToken);
        } else {
            Log.e("WorkoutDetailActivity", "Invalid workout_id or session_token.");
        }

        detailSave.setOnClickListener(v -> finish());
    }

    private void fetchExercises(int workoutId, String sessionToken) {
        Retrofit retrofit = RetrofitClient.getClient(getApplicationContext());
        ApiService exerciseApi = retrofit.create(ApiService.class);

        // Make the call to fetch exercises
        Call<ExerciseResponse> call = exerciseApi.getExercises(sessionToken, workoutId);
        call.enqueue(new Callback<ExerciseResponse>() {
            @Override
            public void onResponse(Call<ExerciseResponse> call, Response<ExerciseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adaptersExerciseList = response.body().getExercises();
                    displayExercises(adaptersExerciseList);  // Display the fetched exercises
                } else {
                    Log.e("WorkoutDetailActivity", "Failed to fetch exercises.");
                }
            }

            @Override
            public void onFailure(Call<ExerciseResponse> call, Throwable t) {
                Log.e("WorkoutDetailActivity", "Error: " + t.getMessage());
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
