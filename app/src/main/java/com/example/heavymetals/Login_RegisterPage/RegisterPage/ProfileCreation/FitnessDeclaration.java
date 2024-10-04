package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class FitnessDeclaration extends AppCompatActivity {

    Button btnPFDnext;
    ArrayList<String> selectedGoals = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration);

        btnPFDnext = findViewById(R.id.btnPFDnext1);

        findViewById(R.id.button).setOnClickListener(v -> selectGoal("Lose Weight"));
        findViewById(R.id.button6).setOnClickListener(v -> selectGoal("Increase Strength"));
        findViewById(R.id.button7).setOnClickListener(v -> selectGoal("Build Muscle"));
        findViewById(R.id.button8).setOnClickListener(v -> selectGoal("Mobility"));
        findViewById(R.id.button9).setOnClickListener(v -> selectGoal("Wellness and Reduce Stress"));
        findViewById(R.id.button14).setOnClickListener(v -> selectGoal("Fitness"));

        btnPFDnext.setOnClickListener(v -> {
            if (!selectedGoals.isEmpty()) {
                new SendDataToServer(FitnessDeclaration.this, selectedGoals).execute();
            }
        });
    }

    private void selectGoal(String goal) {
        if (selectedGoals.contains(goal)) {
            selectedGoals.remove(goal);
        } else {
            selectedGoals.add(goal);
        }
    }

    // Static inner class to prevent memory leaks
    private static class SendDataToServer extends AsyncTask<Void, Void, String> {
        private WeakReference<FitnessDeclaration> activityReference;
        private ArrayList<String> selectedGoals;
        private String urlString = "http://heavymetals.scarlet2.io/HeavyMetals/profile/handle_user_goals.php";

        // Constructor
        SendDataToServer(FitnessDeclaration context, ArrayList<String> goals) {
            activityReference = new WeakReference<>(context);
            this.selectedGoals = goals;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String userId = "123";
                String goals = android.text.TextUtils.join(",", selectedGoals);
                String postData = "user_id=" + userId + "&selected_goals=" + goals;

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Data sent successfully!";
                } else {
                    return "Failed to send data, response code: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception occurred: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            FitnessDeclaration activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (result.equals("Data sent successfully!")) {
                Intent intent = new Intent(activity, FitnessDeclaration2.class);
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
