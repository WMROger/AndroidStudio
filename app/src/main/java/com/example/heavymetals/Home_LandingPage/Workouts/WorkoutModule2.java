package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.Adapters.Exercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.R;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutModule2 extends AppCompatActivity {
    private TextView WM2discard_txt, exerciseNameText, exerciseCategoryText;
    private LinearLayout workoutContainer;
    private HashMap<String, Integer> exerciseIconMap;
    private ImageView exerciseIcon;
    private Integer iconResource;
    private ArrayList<String> selectedExercises;
    private Button addSetButton, WM2AddExercisebtn;
    private ImageButton removeButton;
    private List<Workout> workoutList;
    private EditText workoutNameInput;

    // Define the threshold height in dp
    private static final int THRESHOLD_HEIGHT_DP = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);  // Use the correct layout here

        // Initialize the layout where exercises will be added
        workoutNameInput = findViewById(R.id.workout_name_input);  // Workout name input
        workoutContainer = findViewById(R.id.workout_container);
        WM2discard_txt = findViewById(R.id.WM2discard_txt);
        WM2AddExercisebtn = findViewById(R.id.WM2AddExercisebtn);

        // Set click listener for "Discard" button
        WM2discard_txt.setOnClickListener(v -> finish());

        WM2AddExercisebtn.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutModule2.this, Exercises_All.class);
            startActivity(intent);
        });

        // Get the selected exercises from the intent
        selectedExercises = getIntent().getStringArrayListExtra("selectedExercises");

        // Initialize the exercise icon map
        initializeExerciseIconMap();

        // Add exercises dynamically if any are passed through the intent
        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            for (String exercise : selectedExercises) {
                addExercise(exercise);
            }
        }

        // Check the height and update the button text accordingly
        checkWorkoutContainerHeight();
    }

    // Convert dp to pixels for accurate comparison
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Modify this function to save the workout name and pass it to WorkoutModule4
    private void checkWorkoutContainerHeight() {
        workoutContainer.post(() -> {
            int containerHeight = workoutContainer.getHeight();
            if (containerHeight > dpToPx(THRESHOLD_HEIGHT_DP)) {
                WM2discard_txt.setText("Save");
                WM2discard_txt.setOnClickListener(v -> {
                    String workoutName = workoutNameInput.getText().toString();
                    if (workoutName.isEmpty()) {
                        workoutName = "Unnamed Workout";  // Default name if no input
                    }

                    // Convert the selectedExercises into a list of Exercise objects
                    List<Exercise> exerciseList = new ArrayList<>();
                    for (String exerciseName : selectedExercises) {
                        Exercise exercise = new Exercise(exerciseName, 3, 10, false);  // Default sets and reps
                        exerciseList.add(exercise);
                    }

                    Workout newWorkout = new Workout(workoutName, exerciseList.size(), exerciseList);

                    // Send workout data to the server
                    sendWorkoutToServer(newWorkout);

                    // Navigate to WorkoutModule4
                    Intent intent = new Intent(this, WorkoutModule4.class);
                    intent.putExtra("workout", newWorkout);
                    startActivity(intent);
                });
            } else {
                WM2discard_txt.setText("Discard");
                WM2discard_txt.setOnClickListener(v -> finish());
            }
        });
    }

    // Function to add an exercise dynamically
    private void addExercise(String exerciseName) {
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);
        exerciseCard.setVisibility(View.VISIBLE);

        addSetButton = exerciseCard.findViewById(R.id.add_set_button);
        exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        exerciseCategoryText = exerciseCard.findViewById(R.id.exercise_category);

        exerciseNameText.setText(exerciseName);
        String exerciseCategory = getExerciseCategory(exerciseName);
        exerciseCategoryText.setText(exerciseCategory);

        exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        iconResource = exerciseIconMap.get(exerciseName);
        if (iconResource != null) {
            exerciseIcon.setImageResource(iconResource);
        } else {
            exerciseIcon.setImageResource(R.drawable.human_icon);  // Default icon if not found
        }

        removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            checkWorkoutContainerHeight();
        });

        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);
        addSetButton.setOnClickListener(v -> {
            View newSetLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);
            TextView setNumberTextView = newSetLayout.findViewById(R.id.set_value);
            int currentSetCount = setsContainer.getChildCount();
            setNumberTextView.setText(String.valueOf(currentSetCount + 1));
            EditText newRepsEditText = newSetLayout.findViewById(R.id.reps_edit_text);
            newRepsEditText.setText("10");
            setsContainer.addView(newSetLayout);
        });

        workoutContainer.addView(exerciseCard);
        checkWorkoutContainerHeight();
    }

    // Function to initialize the mapping between exercises and their corresponding icons
    private void initializeExerciseIconMap() {
        exerciseIconMap = new HashMap<>();
        exerciseIconMap.put("Bench Press", R.drawable.bench_press_icon);
        exerciseIconMap.put("Pull ups", R.drawable.pullup_icon);
        exerciseIconMap.put("Dead lift", R.drawable.deadlift_icon);
        exerciseIconMap.put("Treadmill", R.drawable.treadmill_icon);
        exerciseIconMap.put("Plank", R.drawable.plank_icon);
        exerciseIconMap.put("Bicep Curls", R.drawable.bicepcurls_icon);
    }

    // A helper method to return the category for each exercise
    private String getExerciseCategory(String exerciseName) {
        switch (exerciseName) {
            case "Bench Press":
                return "Chest";
            case "Pull ups":
                return "Upper Body";
            case "Dead lift":
                return "Back";
            case "Treadmill":
                return "Cardio";
            case "Plank":
                return "Core";
            case "Bicep Curls":
                return "Arms";
            default:
                return "General";
        }
    }

    // Function to send the workout to the server
    private void sendWorkoutToServer(Workout workout) {
        new AsyncTask<Workout, Void, Void>() {
            @Override
            protected Void doInBackground(Workout... workouts) {
                try {
                    URL url = new URL("http://heavymetals.scarlet2.io/HeavyMetals/save_workout/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setDoOutput(true);

                    // Prepare data to send
                    Gson gson = new Gson();
                    String workoutName = workouts[0].getTitle();
                    String exercisesJson = gson.toJson(workouts[0].getExercises());

                    String postData = "workoutName=" + workoutName +
                            "&exercises=" + exercisesJson;

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Handle success (optional)
                    } else {
                        // Handle error (optional)
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(workout);
    }
}
