package com.example.heavymetals.Home_LandingPage.Workouts;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.heavymetals.Models.Adapters.Workout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

// Define the task in a new file
public class SendWorkoutToServerTask extends AsyncTask<Workout, Void, Void> {
    private WeakReference<Context> contextReference;

    public SendWorkoutToServerTask(Context context) {
        this.contextReference = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Workout... workouts) {
        Context context = contextReference.get();
        if (context == null) {
            return null;
        }

        // Retrieve user email and token from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", null);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (userEmail == null || authToken == null) {
            // If no user is logged in, skip sending the request
            return null;
        }

        try {
            // Make sure the URL is correct
            URL url = new URL("http://heavymetals.scarlet2.io/HeavyMetals/workout_save/save_workout/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Convert the workout object to JSON
            Gson gson = new Gson();
            String workoutName = workouts.length > 0 ? workouts[0].getTitle() : "";  // Use an empty name if no workout
            String exercisesJson = workouts.length > 0 ? gson.toJson(workouts[0].getExercises()) : "[]"; // Send empty array if no workouts

            // Add user authentication details to the POST data
            String postData = "workoutName=" + workoutName + "&exercises=" + exercisesJson + "&email=" + userEmail + "&token=" + authToken;

            // Write data to the server
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            // Log the response code and handle error responses
            int responseCode = conn.getResponseCode();
            Log.d("SaveWorkoutTask", "Response code: " + responseCode);

            // Check for success (HTTP 200 OK)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the server response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("SaveWorkoutTask", "Server response: " + response.toString());
            } else {
                // Log any errors from the server
                Log.e("SaveWorkoutTask", "Failed to save workout. Server responded with: " + responseCode);
                BufferedReader errorStream = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorStream.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorStream.close();
                Log.e("SaveWorkoutTask", "Error response from server: " + errorResponse.toString());
            }
        } catch (Exception e) {
            Log.e("SaveWorkoutTask", "Error saving workout to server", e);
        }
        return null;
    }
}

