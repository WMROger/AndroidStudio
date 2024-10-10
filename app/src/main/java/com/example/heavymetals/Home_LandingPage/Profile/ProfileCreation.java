package com.example.heavymetals.Home_LandingPage.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.R;

import org.json.JSONException;
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
import java.util.Calendar;

// Add these imports if necessary

public class ProfileCreation extends AppCompatActivity {
    private Button btnPFDnext;
    private EditText dateEditText;
    private TextView ProfileAdd_create, PCFirstName, PCLastName, PC_Skip;
    private ImageView ProfilePicture;
    private static final int PICK_IMAGE_REQUEST = 100;

    // Progress tracking keys and constants
    private static final String PREFS_NAME = "UserProgressPrefs";
    private static final String PROGRESS_KEY = "progress";
    private static final String STEP1_COMPLETED_KEY = "profile_creation_completed";
    private static final int PROFILE_CREATION_PROGRESS = 25;

    // URL for getting username
    private static final String GET_USERNAME_URL = "https://heavymetals.scarlet2.io/HeavyMetals/get_username.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        // Fetch the user's email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);
        String email = sharedPreferences.getString("loggedInUser", null); // Replace "loggedInUser" with your actual key

        ProfileAdd_create = findViewById(R.id.ProfileAdd_create);
        ProfilePicture = findViewById(R.id.ProfilePicture);
        ProfilePicture.setBackground(null);

        PCFirstName = findViewById(R.id.PCFirstName);
        PCLastName = findViewById(R.id.PCLastName);
        PC_Skip = findViewById(R.id.PC_Skip);

        // Fetch the user's first and last name from the server
        fetchUserDetails(email); // Pass the email to the method

        // Handle Skip button click
        PC_Skip.setOnClickListener(v -> {
            // Reset the progress to 0 on skipping
            resetProgress();
            // Redirect to MainActivity
            Intent intent = new Intent(ProfileCreation.this, MainActivity.class);
            startActivity(intent);
        });

        // Request storage permission if necessary
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Set onClick listener for image chooser
        ProfileAdd_create.setOnClickListener(v -> openImageChooser());

        btnPFDnext = findViewById(R.id.btnPFDnext);
        dateEditText = findViewById(R.id.editTextText6);

        // Show date picker when date field is clicked
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Handle the "Next" button click event
        btnPFDnext.setOnClickListener(v -> {
            if (areFieldsFilled()) {
                markStepAsCompleted();
                updateProgress(PROFILE_CREATION_PROGRESS);
                Intent intent = new Intent(ProfileCreation.this, FitnessDeclaration.class);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileCreation.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(String email) {
        new Thread(() -> {
            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/get_username.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the POST data with the actual user email
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

                    // Update the UI with the user's name on the main thread
                    runOnUiThread(() -> {
                        PCFirstName.setText(firstName);
                        PCLastName.setText(lastName);
                    });
                } else {
                    String message = jsonResponse.getString("message");
                    runOnUiThread(() -> Toast.makeText(ProfileCreation.this, message, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }




    // Reset the progress to 0 when skipping
    private void resetProgress() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, 0);  // Set progress to 0 when skipping
        editor.apply();
    }

    // Check if all required fields are filled
    private boolean areFieldsFilled() {
        return !dateEditText.getText().toString().trim().isEmpty();
    }

    // Display the date picker dialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePicker,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateEditText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Open the image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            Glide.with(this)
                    .load(imageUri)
                    .transform(new CircleCrop())
                    .into(ProfilePicture);
        }
    }

    // Mark this step as completed
    private void markStepAsCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STEP1_COMPLETED_KEY, true);
        editor.apply();
    }

    // Update progress in SharedPreferences
    private void updateProgress(int progressIncrement) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentProgress = sharedPreferences.getInt(PROGRESS_KEY, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, currentProgress + progressIncrement);
        editor.apply();
    }
}
