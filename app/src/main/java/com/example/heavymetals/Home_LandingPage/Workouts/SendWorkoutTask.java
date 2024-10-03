package com.example.heavymetals.Home_LandingPage.Workouts;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.heavymetals.Home_LandingPage.Workouts.WorkoutModule2;
import com.example.heavymetals.Models.Adapters.AdaptersExercise;
import com.example.heavymetals.Models.Adapters.Workout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class SendWorkoutTask extends AsyncTask<Workout, Void, Boolean> {
    private final WeakReference<WorkoutModule2> activityReference;
    private String responseMessage = "";  // To store the server's response message
    private String workoutId;  // To store the newly created workout ID from the server

    SendWorkoutTask(WorkoutModule2 activity) {
        activityReference = new WeakReference<>(activity);
    }

    @Override
    protected Boolean doInBackground(Workout... workouts) {
        WorkoutModule2 activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return false;

        SharedPreferences sharedPreferences = activity.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null); // Assuming this is the user ID or email
        String authToken = sharedPreferences.getString("auth_token", null); // Authorization token for validation

        // Check if the user session and auth token are available
        if (userEmail == null || authToken == null) {
            Log.e(TAG, "No user session or auth token found");
            return false;
        }

        try {
            // Convert workout and exercises to JSON format
            Gson gson = new Gson();
            Workout workout = workouts[0];
            String workoutName = URLEncoder.encode(workout.getTitle(), "UTF-8"); // Encode workout name

            // Send the workout name to the server and create a workout entry
            String workoutPostData = "session_token=" + URLEncoder.encode(authToken, "UTF-8") +
                    "&workout_name=" + workoutName;


            URL workoutUrl = new URL("https://heavymetals.scarlet2.io/HeavyMetals/workout_save/add_workout.php");
            HttpURLConnection workoutConn = (HttpURLConnection) workoutUrl.openConnection();
            workoutConn.setRequestMethod("POST");
            workoutConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            workoutConn.setRequestProperty("Authorization", "Bearer " + authToken);
            workoutConn.setDoOutput(true);

            // Send the POST data for workout
            OutputStream osWorkout = workoutConn.getOutputStream();
            osWorkout.write(workoutPostData.getBytes());
            osWorkout.flush();
            osWorkout.close();

            int workoutResponseCode = workoutConn.getResponseCode();
            if (workoutResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader inWorkout = new BufferedReader(new InputStreamReader(workoutConn.getInputStream()));
                String inputLine;
                StringBuilder workoutResponse = new StringBuilder();
                while ((inputLine = inWorkout.readLine()) != null) {
                    workoutResponse.append(inputLine);
                }
                inWorkout.close();

                // Now handle the response to check if it's a valid JSON object
                if (workoutResponse.toString().startsWith("{")) {
                    try {
                        JSONObject jsonResponse = new JSONObject(workoutResponse.toString());
                        if (jsonResponse.has("workout_id")) {
                            workoutId = jsonResponse.getString("workout_id");
                            Log.d(TAG, "Workout created successfully with ID: " + workoutId);
                        } else {
                            String errorMessage = jsonResponse.optString("message", "No workout_id in the response.");
                            Log.e(TAG, "Failed to create workout. Server message: " + errorMessage);
                            return false;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
                        return false;
                    }
                } else {
                    Log.e(TAG, "Unexpected response from server: " + workoutResponse.toString());
                    return false;
                }
            } else {
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(workoutConn.getErrorStream()));
                String inputLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((inputLine = errorStream.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                errorStream.close();
                Log.e(TAG, "Failed to create workout. Server returned: " + workoutResponseCode + " with message: " + errorResponse.toString());
                return false;
            }




            // Send exercises linked to the workout
            List<AdaptersExercise> exercises = workout.getExercises();

            String exercisesJson = gson.toJson(exercises); // Just convert to JSON without encoding
            Log.d(TAG, "Exercises JSON: " + exercisesJson);

            String exercisePostData = "session_token=" + URLEncoder.encode(authToken, "UTF-8") +
                    "&workout_id=" + workoutId +
                    "&exercises=" + URLEncoder.encode(exercisesJson, "UTF-8");
            Log.d(TAG, "Sending workout data: " + workoutPostData);
            Log.d(TAG, "Sending exercise data: " + exercisePostData);



            // Set up connection for exercises
            URL exerciseUrl = new URL("https://heavymetals.scarlet2.io/HeavyMetals/workout_save/add_exercise.php");
            HttpURLConnection exerciseConn = (HttpURLConnection) exerciseUrl.openConnection();
            exerciseConn.setRequestMethod("POST");
            exerciseConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            exerciseConn.setRequestProperty("Authorization", "Bearer " + authToken);
            exerciseConn.setDoOutput(true);

            // Send the POST data for exercises
            OutputStream osExercise = exerciseConn.getOutputStream();
            osExercise.write(exercisePostData.getBytes());
            osExercise.flush();
            osExercise.close();

            int exerciseResponseCode = exerciseConn.getResponseCode();
            if (exerciseResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader inExercise = new BufferedReader(new InputStreamReader(exerciseConn.getInputStream()));
                String inputLine;
                StringBuilder exerciseResponse = new StringBuilder();
                while ((inputLine = inExercise.readLine()) != null) {
                    exerciseResponse.append(inputLine);
                }
                inExercise.close();

                // Log and store the response message
                responseMessage = exerciseResponse.toString();
                Log.d(TAG, "Exercises successfully saved");
                return true;
            } else {
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(exerciseConn.getErrorStream()));
                String inputLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((inputLine = errorStream.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                errorStream.close();
                Log.e(TAG, "Failed to save exercises. Server returned: " + exerciseResponseCode + " with message: " + errorResponse.toString());
                return false;
            }


        } catch (Exception e) {
            Log.e(TAG, "Error while saving workout and exercises to server", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        WorkoutModule2 activity = activityReference.get();
        if (activity == null || activity.isFinishing()) return;

        if (success) {
            // Display the server response to the user
            Toast.makeText(activity, "Workout and exercises saved successfully!\n" + responseMessage, Toast.LENGTH_LONG).show();
        } else {
            // Show failure and log response message
            Toast.makeText(activity, "Failed to save workout and exercises. Try again.\n" + responseMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to save workout and exercises. Response: " + responseMessage);
        }
    }
}
