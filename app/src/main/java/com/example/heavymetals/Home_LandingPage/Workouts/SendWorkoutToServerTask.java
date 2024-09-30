package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.heavymetals.Models.Adapters.Workout;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SendWorkoutToServerTask extends AsyncTask<List<Workout>, Void, Boolean> {
    private Context context;

    public SendWorkoutToServerTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(List<Workout>... workoutsList) {
        List<Workout> workouts = workoutsList[0];  // Get the first element of the passed List

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("user_email", null);
            String authToken = sharedPreferences.getString("auth_token", null);

            if (userEmail == null || authToken == null) {
                Log.e("SaveWorkoutTask", "User email or auth token not found.");
                return false;
            }

            // Prepare the URL
            URL url = new URL("http://heavymetals.scarlet2.io/HeavyMetals/workout_save/save_workout.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Convert the workout list to JSON
            Gson gson = new Gson();
            String workoutsJson = gson.toJson(workouts);

            // Prepare POST data
            String postData = "email=" + userEmail + "&token=" + authToken + "&workouts=" + workoutsJson;

            // Send the data
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            // Get the response
            int responseCode = conn.getResponseCode();
            Log.d("SaveWorkoutTask", "Response code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("SaveWorkoutTask", "Server response: " + response.toString());
                return true;
            } else {
                Log.e("SaveWorkoutTask", "Failed to save workout. Server responded with: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            Log.e("SaveWorkoutTask", "Error saving workout to server", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Toast.makeText(context, "Workouts saved to server successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to save workouts to server.", Toast.LENGTH_SHORT).show();
        }
    }
}
