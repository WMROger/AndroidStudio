package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Models.Adapters.Workout;
import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.Exercise;
import com.example.heavymetals.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


public class WorkoutModule2 extends AppCompatActivity {
    private TextView WM2discard_txt;
    private LinearLayout workoutContainer;
    private ArrayList<AdaptersExercise> selectedAdaptersExercises;
    private HashMap<String, Integer> exerciseSetsMap = new HashMap<>();
    private EditText workoutNameInput;
    private Button WM2AddExercisebtn;
    private static final String TAG = "WorkoutModule2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_module2);

        // Initialize layout elements
        workoutNameInput = findViewById(R.id.workout_name_input);
        workoutContainer = findViewById(R.id.workout_container);
        WM2discard_txt = findViewById(R.id.WM2discard_txt);
        WM2AddExercisebtn = findViewById(R.id.WM2AddExercisebtn);

        // Check user login session
        checkUserSession();

        // Get selected exercises passed from another activity
        ArrayList<Exercise> selectedExercises = (ArrayList<Exercise>) getIntent().getSerializableExtra("selectedExercises");

        // Convert and add selected exercises to the container
        selectedAdaptersExercises = new ArrayList<>();
        if (selectedExercises != null && !selectedExercises.isEmpty()) {
            for (Exercise exercise : selectedExercises) {
                AdaptersExercise adaptersExercise = convertToAdaptersExercise(exercise);
                selectedAdaptersExercises.add(adaptersExercise);
                addExerciseToUI(adaptersExercise);
            }
        }

        // Save or discard logic
        WM2discard_txt.setOnClickListener(v -> {
            handleSaveOrDiscard();
        });

        // Add exercise button logic
        WM2AddExercisebtn.setOnClickListener(view -> finish());

        // Check if workout container's height requires a "Save" action
        checkWorkoutContainerHeight();
    }

    /**
     * Convert Exercise object to AdaptersExercise object.
     */
    private AdaptersExercise convertToAdaptersExercise(Exercise exercise) {
        return new AdaptersExercise(
                exercise.getId(),      // Ensure you are using exercise_list_id here
                exercise.getName(),
                1,  // Default sets
                10, // Default reps
                false, // Default isDone status
                exercise.getImageUrl()  // Pass correct image URL
        );
    }

    /**
     * Save or discard workout based on the current state.
     */
    private void handleSaveOrDiscard() {
        String workoutName = workoutNameInput.getText().toString().trim();  // Ensure no leading/trailing spaces

        if (WM2discard_txt.getText().toString().equals("Save")) {
            // Save action
            if (workoutName.isEmpty()) {
                Toast.makeText(this, "Please enter a workout name", Toast.LENGTH_SHORT).show();
                return;  // Stop further processing
            }

            // Create a new workout with ID 0 initially
            Workout newWorkout = new Workout(0, workoutName, selectedAdaptersExercises);

            // Now create the workout on the server (First request)
            createWorkoutOnServer(newWorkout);
        } else {
            // Discard action: Return to MainActivity (Home Fragment)
            Intent intent = new Intent(WorkoutModule2.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // To prevent coming back to this activity
        }
    }

    /**
     * Sends the workout name and session token to the server to create the workout.
     */
    private void createWorkoutOnServer(Workout newWorkout) {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/workout_save/add_workout.php";

        // Send request as form data using StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getInt("success") == 1) {
                            int workoutId = jsonResponse.getInt("workout_id");
                            Log.d(TAG, "Workout created successfully with ID: " + workoutId);

                            // Now save the exercises using the newly created workout_id
                            newWorkout.setWorkoutId(workoutId);
                            sendExercisesToServer(newWorkout);
                        } else {
                            Toast.makeText(WorkoutModule2.this, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e(TAG, "Error response from server: " + error.getMessage());
                    Toast.makeText(WorkoutModule2.this, "Error creating workout", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("workout_name", newWorkout.getTitle());  // Send workout name as form data
                params.put("session_token", getSessionToken());     // Send session token as form data
                return params;
            }
        };

        // Add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /**
     * Sends the exercises to the server after the workout has been created.
     */
    private void sendExercisesToServer(Workout newWorkout) {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/workout_save/add_exercise.php";  // Adjust URL

        // Create JSON object for exercises
        JSONObject exercisesData = new JSONObject();
        try {
            exercisesData.put("workout_id", newWorkout.getWorkoutId());  // Get workout_id from the model
            exercisesData.put("session_token", getSessionToken());       // Add session token

            // Create exercises array
            JSONArray exercisesArray = new JSONArray();
            for (AdaptersExercise exercise : newWorkout.getExercises()) {
                JSONObject exerciseJson = new JSONObject();
                exerciseJson.put("exercise_name", exercise.getName());  // Set exercise name
                exerciseJson.put("sets", exercise.getSets());           // Set sets
                exerciseJson.put("reps", exercise.getReps());           // Set reps
                exerciseJson.put("exercise_list_id", exercise.getId()); // Set exercise list ID
                exercisesArray.put(exerciseJson);                       // Add exercise to array
            }
            exercisesData.put("exercises", exercisesArray);

            // Log the constructed JSON for debugging
            Log.d(TAG, "Sending exercises data to server: " + exercisesData.toString());

            // Send request to save exercises
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, exercisesData,
                    response -> {
                        Log.d(TAG, "Server response: " + response.toString());
                        Toast.makeText(WorkoutModule2.this, "Workout and exercises saved successfully!", Toast.LENGTH_SHORT).show();

                        // Redirect to WorkoutModule4 after saving the workout
                        Intent intent = new Intent(WorkoutModule2.this, WorkoutModule4.class);
                        intent.putExtra("workout_id", newWorkout.getWorkoutId());
                        startActivity(intent);
                        finish(); // To prevent returning to this activity

                    },
                    error -> {
                        Log.e(TAG, "Error response from server: " + error.getMessage());
                        Toast.makeText(WorkoutModule2.this, "Error saving exercises", Toast.LENGTH_SHORT).show();
                    }
            );

            // Add the request to the queue
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the session token from SharedPreferences.
     */
    private String getSessionToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);
        Log.d("WorkoutModule2", "Retrieved Session Token: " + token);
        return token;
    }




    /**
     * Sends the workout data (in JSON format) to the server using Volley.
     */
    private void sendJsonToServer(JSONObject workoutData) {
        String url = "https://heavymetals.scarlet2.io/HeavyMetals/workout_save/add_exercise.php";

        // Log the JSON data being sent to the server
        Log.d(TAG, "Sending workout data to server: " + workoutData.toString());

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, workoutData,
                response -> {
                    // Handle successful response
                    Log.d(TAG, "Server response: " + response.toString());
                    Toast.makeText(WorkoutModule2.this, "Workout saved successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error response
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Error response from server: " + error.networkResponse.statusCode);
                        Log.e(TAG, "Error details: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(WorkoutModule2.this, "Error saving workout: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the Volley queue
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Add an exercise to the workout UI.
     */
    private void addExerciseToUI(AdaptersExercise adaptersExercise) {
        View exerciseCard = LayoutInflater.from(this).inflate(R.layout.exercise_item, workoutContainer, false);
        TextView exerciseNameText = exerciseCard.findViewById(R.id.exercise_name);
        ImageView exerciseIcon = exerciseCard.findViewById(R.id.exercise_icon);
        ImageButton removeButton = exerciseCard.findViewById(R.id.remove_exercise_button);
        LinearLayout setsContainer = exerciseCard.findViewById(R.id.sets_container);
        Button addSetButton = exerciseCard.findViewById(R.id.add_set_button);

        exerciseNameText.setText(adaptersExercise.getName());

        // Get the image URL from adaptersExercise
        String imageUrl = adaptersExercise.getImageUrl();
        Log.d(TAG, "Image URL for " + adaptersExercise.getName() + ": " + imageUrl);

        // Check if the URL is valid and not empty
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Log.d(TAG, "Loading image for: " + adaptersExercise.getName() + ", URL: " + imageUrl);

            // Use Picasso to load the image
            Picasso.get()
                    .load(imageUrl)
                    .resize(200, 200)  // Resize the image if you want it larger or smaller
                    .centerCrop()      // Center and crop to make it fit the dimensions
                    .placeholder(R.drawable.human_icon)  // Show placeholder during loading
                    .error(R.drawable.human_icon)        // Show fallback image if loading fails
                    .into(exerciseIcon);

        } else {
            // Handle empty or invalid URL case
            Log.w(TAG, "No valid image URL, using default image.");
            Picasso.get()
                    .load(R.drawable.human_icon)
                    .into(exerciseIcon);
        }

        // Remove exercise logic
        removeButton.setOnClickListener(v -> {
            workoutContainer.removeView(exerciseCard);
            exerciseSetsMap.remove(adaptersExercise.getName());
            checkWorkoutContainerHeight();
        });

        // Default 1 set when exercise is added
        exerciseSetsMap.put(adaptersExercise.getName(), 1);
        addSetToContainer(setsContainer, 1, true);  // First set is editable

        // Add set button logic
        addSetButton.setOnClickListener(v -> {
            int currentSetCount = exerciseSetsMap.getOrDefault(adaptersExercise.getName(), 0);
            currentSetCount++;
            exerciseSetsMap.put(adaptersExercise.getName(), currentSetCount);
            addSetToContainer(setsContainer, currentSetCount, false);  // Subsequent sets follow first
        });

        workoutContainer.addView(exerciseCard);
        checkWorkoutContainerHeight();
    }

    /**
     * Adds a set to the provided container.
     * If it's the first set, the reps are editable.
     * Subsequent sets will follow the reps set in the first set.
     */
    private void addSetToContainer(LinearLayout setsContainer, int setCount, boolean isFirstSet) {
        View setLayout = LayoutInflater.from(this).inflate(R.layout.set_item_layout, setsContainer, false);
        TextView setNumberTextView = setLayout.findViewById(R.id.set_value);
        EditText repsEditText = setLayout.findViewById(R.id.reps_edit_text);  // Ensure this is an EditText

        setNumberTextView.setText(String.valueOf(setCount));  // Display the set number

        if (isFirstSet) {
            // Create a TextWatcher for the first set
            TextWatcher repsTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No need to handle
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty()) {
                        int reps;
                        try {
                            reps = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            reps = 10; // Default reps if input is invalid
                        }
                        updateRepsForAllSets(setsContainer, reps);  // Update reps for all sets
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // No need to handle
                }
            };

            // Add the TextWatcher to the first set's EditText
            repsEditText.addTextChangedListener(repsTextWatcher);
        } else {
            // For subsequent sets, disable editing and follow the first set's reps
            repsEditText.setEnabled(false);
            repsEditText.setText(getFirstSetReps(setsContainer));  // Get the reps from the first set
        }

        setsContainer.addView(setLayout);
    }

    /**
     * Update reps for all sets based on the first set's reps.
     */
    private void updateRepsForAllSets(LinearLayout setsContainer, int reps) {
        // Loop through all the sets and update the reps, but without triggering the TextWatcher
        for (int i = 0; i < setsContainer.getChildCount(); i++) {
            View setLayout = setsContainer.getChildAt(i);
            EditText repsEditText = setLayout.findViewById(R.id.reps_edit_text);  // Make sure it's EditText

            repsEditText.setText(String.valueOf(reps));  // Set the reps text
        }
    }

    /**
     * Get the reps from the first set in the container.
     */
    private String getFirstSetReps(LinearLayout setsContainer) {
        View firstSetLayout = setsContainer.getChildAt(0);
        EditText firstSetRepsEditText = firstSetLayout.findViewById(R.id.reps_edit_text);  // Ensure this is EditText
        return firstSetRepsEditText.getText().toString();
    }

    /**
     * Check workout container height and adjust "Save" or "Discard" label.
     */
    private void checkWorkoutContainerHeight() {
        workoutContainer.post(() -> {
            int containerHeight = workoutContainer.getHeight();
            if (containerHeight > dpToPx(100)) {
                WM2discard_txt.setText("Save");
            } else {
                WM2discard_txt.setText("Discard");
            }
        });
    }

    /**
     * Convert dp to px based on screen density.
     */
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Save workout ID to SharedPreferences for future reference.
     */
    private void saveWorkoutIdToPreferences(int workoutId) {
        SharedPreferences sharedPreferences = getSharedPreferences("WorkoutPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_workout_id", workoutId);
        editor.apply();
    }

    /**
     * Check if the user is logged in; otherwise redirect to login.
     */
    private void checkUserSession() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);

        if (loggedInUser == null) {
            Toast.makeText(this, "No user session found. Please log in.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } else {
            Log.d(TAG, "User is logged in: " + loggedInUser);
        }
    }

    /**
     * Redirect to login activity.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Save the workout details for the user (to the server or locally).
     */
    private void saveWorkoutForUser(Workout workout) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);

        if (loggedInUser == null) {
            Toast.makeText(this, "No logged-in user found. Please login.", Toast.LENGTH_LONG).show();
            redirectToLogin();
        } else {
            // Call your method or AsyncTask to save the workout to the server
            new SendWorkoutTask(this).execute(workout);
        }
    }
}
