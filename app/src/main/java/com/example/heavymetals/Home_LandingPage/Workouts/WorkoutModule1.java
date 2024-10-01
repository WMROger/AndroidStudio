package com.example.heavymetals.Home_LandingPage.Workouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavymetals.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WorkoutModule1 extends Fragment {
    private Button FEPAddworkout;
    private ImageButton addItemBtn1, addItemBtn2, addItemBtn3, addItemBtn4, addItemBtn5, addItemBtn6;
    private ArrayList<String> selectedExercises = new ArrayList<>();
    private TextView planWorkoutCounter_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_plan, container, false);

        planWorkoutCounter_txt = view.findViewById(R.id.planWorkoutCounter_txt);
        FEPAddworkout = view.findViewById(R.id.FEPAddWorkout);

        addItemBtn1 = view.findViewById(R.id.addItemBtn1);
        addItemBtn2 = view.findViewById(R.id.addItemBtn2);
        addItemBtn3 = view.findViewById(R.id.addItemBtn3);
        addItemBtn4 = view.findViewById(R.id.addItemBtn4);
        addItemBtn5 = view.findViewById(R.id.addItemBtn5);
        addItemBtn6 = view.findViewById(R.id.addItemBtn6);

        setupToggleButton(addItemBtn1, "Bench Press");
        setupToggleButton(addItemBtn2, "Treadmill");
        setupToggleButton(addItemBtn3, "Dead lift");
        setupToggleButton(addItemBtn4, "Plank");
        setupToggleButton(addItemBtn5, "Pull ups");
        setupToggleButton(addItemBtn6, "Bicep Curls");

        // Retrieve the workout count saved in Module 4
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("WorkoutData", getActivity().MODE_PRIVATE);
        int workoutCount = sharedPreferences.getInt("workoutCount", 0);  // Default to 0 if no data

        // Update the workout counter text to show the number of workouts in Module 4
        planWorkoutCounter_txt.setText("Workouts Created: " + workoutCount);

        FEPAddworkout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutModule2.class);
            intent.putStringArrayListExtra("selectedExercises", selectedExercises);
            startActivity(intent);
        });

        return view;
    }


    private void setupToggleButton(ImageButton button, String exerciseName) {
        button.setOnClickListener(v -> {
            int currentIcon = (int) button.getTag();

            if (currentIcon == R.drawable.additem_black) {
                // Prevent multiple clicks while the operation is ongoing
                button.setEnabled(false);

                // Change the button icon to indicate the item is selected
                button.setImageResource(R.drawable.additem_orange);
                button.setTag(R.drawable.additem_orange);
                selectedExercises.add(exerciseName);  // Add exercise to the list

                // Upload to database when exercise is selected
                new AddExerciseTask(button).execute(exerciseName);

            } else {
                // Prevent multiple clicks while the operation is ongoing
                button.setEnabled(false);

                // Change the button icon to indicate the item is deselected
                button.setImageResource(R.drawable.additem_black);
                button.setTag(R.drawable.additem_black);
                selectedExercises.remove(exerciseName);  // Remove exercise from the list

                // Remove from database when exercise is deselected
                new RemoveExerciseTask(button).execute(exerciseName);
            }
        });

        button.setTag(R.drawable.additem_black);
    }

    private class AddExerciseTask extends AsyncTask<String, Void, Boolean> {
        private ImageButton button;

        AddExerciseTask(ImageButton button) {
            this.button = button;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String exerciseName = params[0];
            boolean success = false;

            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/exercise_save/add_exercise.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // Get user data from SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
                String userId = sharedPreferences.getString("loggedInUser", null);

                String postData = "exerciseName=" + exerciseName + "&userId=" + userId;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                success = responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            button.setEnabled(true);
            if (!success) {
                // If there was an error, revert the button back
                button.setImageResource(R.drawable.additem_black);
                button.setTag(R.drawable.additem_black);
                Toast.makeText(getActivity(), "Failed to add exercise", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RemoveExerciseTask extends AsyncTask<String, Void, Boolean> {
        private ImageButton button;

        RemoveExerciseTask(ImageButton button) {
            this.button = button;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String exerciseName = params[0];
            boolean success = false;

            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/exercise_save/remove_exercise.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getActivity().MODE_PRIVATE);
                String userId = sharedPreferences.getString("loggedInUser", null);

                String postData = "exerciseName=" + exerciseName + "&userId=" + userId;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                success = responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            button.setEnabled(true);
            if (!success) {
                // If there was an error, revert the button back
                button.setImageResource(R.drawable.additem_orange);
                button.setTag(R.drawable.additem_orange);
                Toast.makeText(getActivity(), "Failed to remove exercise", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
