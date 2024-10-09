package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.Home_LandingPage.MeasurementsActivity;
import com.example.heavymetals.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException; // Import this to handle JSON exception

public class ProfileEditActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText;
    private TextView backButton;
    private Button ViewMeasurements;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Set up padding to handle window insets (e.g., for notch or status bar area)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        firstNameEditText = findViewById(R.id.edit_firstname);
        lastNameEditText = findViewById(R.id.edit_lastname);
        backButton = findViewById(R.id.back_profile);
        ViewMeasurements = findViewById(R.id.view_measurements);

        // Fetch user details from SharedPreferences to pre-fill the fields
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null);
        if (userEmail != null) {
            // Fetch user details from server to populate the fields
            fetchUserDetails(userEmail);
        } else {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show();
        }

        ViewMeasurements.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileEditActivity.this, MeasurementsActivity.class);
        });

        backButton.setOnClickListener(v -> finish());
    }

    // Fetch the user details from the server
    private void fetchUserDetails(String email) {
        new Thread(() -> {
            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/get_username.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the POST data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "email=" + URLEncoder.encode(email, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Get the response
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    String firstName = jsonResponse.getString("first_name");
                    String lastName = jsonResponse.getString("last_name");

                    // Update the UI with the user details on the main thread
                    runOnUiThread(() -> {
                        firstNameEditText.setText(firstName);
                        lastNameEditText.setText(lastName);
                    });
                } else {
                    String message = jsonResponse.getString("message");
                    runOnUiThread(() ->
                            Toast.makeText(ProfileEditActivity.this, message, Toast.LENGTH_LONG).show());
                }

            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(ProfileEditActivity.this, "Failed to parse server response", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Update user details on the server
    private void updateUserDetails(String firstName, String lastName) {
        new Thread(() -> {
            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/update_user.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the POST data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "first_name=" + URLEncoder.encode(firstName, "UTF-8") +
                        "&last_name=" + URLEncoder.encode(lastName, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Get the response
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse the response JSON
                // Parse the response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");

                runOnUiThread(() -> {
                    try {
                        if (success) {
                            Toast.makeText(ProfileEditActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                        } else {
                            // Handle the case where success is false
                            String message = jsonResponse.getString("message");
                            Toast.makeText(ProfileEditActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Show error message if JSON parsing fails
                        Toast.makeText(ProfileEditActivity.this, "Failed to parse server response", Toast.LENGTH_LONG).show();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(ProfileEditActivity.this, "Failed to parse server response", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
