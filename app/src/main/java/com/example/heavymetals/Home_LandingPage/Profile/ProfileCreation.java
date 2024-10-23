package com.example.heavymetals.Home_LandingPage.Profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileCreation extends AppCompatActivity {
    private Button btnPFDnext;
    private EditText dateEditText,numberEditText;
    private TextView ProfileAdd_create, PCFirstName, PCLastName, PC_Skip;
    private ImageView ProfilePicture;
    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri selectedImageUri;

    // Progress tracking keys and constants
    private static final String PREFS_NAME = "UserProgressPrefs";
    private static final String PROGRESS_KEY = "progress";
    private static final String STEP1_COMPLETED_KEY = "profile_creation_completed";
    private static final int PROFILE_CREATION_PROGRESS = 25;
    private static final String PHILIPPINE_PHONE_REGEX = "^(\\+63|0)9\\d{9}$";  // Philippine mobile number regex

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        // Fetch the user's details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);
        String email = sharedPreferences.getString("loggedInUser", null);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (userId == null || email == null) {
            Toast.makeText(ProfileCreation.this, "User not logged in. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize UI elements
        ProfileAdd_create = findViewById(R.id.ProfileAdd_create);
        PCFirstName = findViewById(R.id.PCFirstName);
        PCLastName = findViewById(R.id.PCLastName);
        ProfilePicture = findViewById(R.id.ProfilePicture);
        numberEditText = findViewById(R.id.edit_phonenumber);  // EditText for phone number
        dateEditText = findViewById(R.id.editTextText6);
        btnPFDnext = findViewById(R.id.btnPFDnext);
        PC_Skip = findViewById(R.id.PC_Skip);

        // Set up the date picker on dateEditText click
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Fetch the user's profile details from the server
        fetchUserProfile(userId);

        // Set up the image chooser when profile picture is clicked
        ProfileAdd_create.setOnClickListener(v -> openImageChooser());

        // Handle skip button click
        PC_Skip.setOnClickListener(v -> {
            resetProgress();
            Intent intent = new Intent(ProfileCreation.this, MainActivity.class);
            startActivity(intent);
        });

        // Handle "Next" button click
        // Handle "Next" button click
        btnPFDnext.setOnClickListener(v -> {
            if (areFieldsFilled() && isAgeValid() && isValidPhilippineNumber(numberEditText.getText().toString())) {
                String profilePicUrl = (selectedImageUri != null) ? selectedImageUri.toString() : "https://heavymetals.scarlet2.io/HeavyMetals/assets/ic_profile.svg";

                // Format date for the server
                String dateOfBirthFormatted = formatDateForServer(dateEditText.getText().toString());

                // Save the user profile with formatted date
                saveUserProfile(userId, Uri.parse(profilePicUrl), dateOfBirthFormatted, numberEditText.getText().toString());

                // Mark step as completed and proceed
                markStepAsCompleted();
                updateProgress(PROFILE_CREATION_PROGRESS);
                Intent intent = new Intent(ProfileCreation.this, FitnessDeclaration.class);
                startActivity(intent);
            } else if (!isAgeValid()) {
                Toast.makeText(ProfileCreation.this, "You must be at least 10 years old to proceed.", Toast.LENGTH_SHORT).show();
            } else if (!isValidPhilippineNumber(numberEditText.getText().toString())) {
                numberEditText.setError("Invalid Philippine phone number. Must start with +639 or 09 and contain 10 digits.");
            } else {
                Toast.makeText(ProfileCreation.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Save user profile, including phone number, first name, and last name
    private void saveUserProfile(String userId, Uri profilePicUri, String dateOfBirth, String phoneNumber) {
        // Fetch first name and last name from the UI
        String firstName = PCFirstName.getText().toString().trim();
        String lastName = PCLastName.getText().toString().trim();

        // Check if any required field is missing
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || userId == null || userId.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(ProfileCreation.this, "Error: Missing required fields (user_id, first_name, last_name, phone_number)", Toast.LENGTH_LONG).show());
            return;  // Don't proceed if any field is missing
        }

        new Thread(() -> {
            try {
                // Create Multipart body without the profile picture by default
                okhttp3.MultipartBody.Builder builder = new okhttp3.MultipartBody.Builder()
                        .setType(okhttp3.MultipartBody.FORM)
                        .addFormDataPart("user_id", userId)
                        .addFormDataPart("first_name", firstName)  // Include first name
                        .addFormDataPart("last_name", lastName)    // Include last name
                        .addFormDataPart("date_of_birth", dateOfBirth)
                        .addFormDataPart("phone_number", phoneNumber);  // Add phone number

                // Check if profile picture is selected, otherwise proceed without the picture
                if (profilePicUri != null) {
                    File profilePicFile = new File(getRealPathFromURI(profilePicUri));
                    if (profilePicFile.exists()) {
                        // Add selected profile picture to the request
                        builder.addFormDataPart("profile_pic", profilePicFile.getName(),
                                okhttp3.RequestBody.create(profilePicFile, okhttp3.MediaType.parse("image/*")));
                    }
                }

                okhttp3.RequestBody requestBody = builder.build();

                // Create a request
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://heavymetals.scarlet2.io/HeavyMetals/user_details/save_profile.php")
                        .post(requestBody)
                        .build();

                // Execute the request
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
                okhttp3.Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.getBoolean("success");

                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(ProfileCreation.this, "Profile saved successfully!", Toast.LENGTH_LONG).show();
                            // Proceed to FitnessDeclaration activity after success response
                            markStepAsCompleted();
                            updateProgress(PROFILE_CREATION_PROGRESS);
                            Intent intent = new Intent(ProfileCreation.this, FitnessDeclaration.class);
                            startActivity(intent);
                            finish();  // Optional: Finish current activity
                        } else {
                            String message = jsonResponse.optString("message", "Unknown error occurred");
                            Toast.makeText(ProfileCreation.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ProfileCreation.this, "Error: Failed to save profile", Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("ProfileCreation", "Error saving profile", e);
                runOnUiThread(() -> Toast.makeText(ProfileCreation.this, "Error saving profile. Please try again.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }


    // Validate phone number based on Philippine format
    private boolean isValidPhilippineNumber(String phoneNumber) {
        return phoneNumber.matches(PHILIPPINE_PHONE_REGEX);
    }

    // Check if fields are filled
    private boolean areFieldsFilled() {
        return !dateEditText.getText().toString().trim().isEmpty() &&
                !numberEditText.getText().toString().trim().isEmpty();  // Ensure phone number field is filled
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



    private void fetchUserProfile(String userId) {
        new Thread(() -> {
            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/user_details/get_profile.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Prepare POST data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "user_id=" + URLEncoder.encode(userId, "UTF-8");
                writer.write(postData);
                writer.flush();
                writer.close();
                os.close();

                // Get the response from the server
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
                    JSONObject profile = jsonResponse.getJSONObject("profile");
                    String firstName = profile.getString("first_name");
                    String lastName = profile.getString("last_name");
                    String profilePic = profile.getString("profile_pic");
                    String dateOfBirth = profile.getString("date_of_birth");

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        PCFirstName.setText(firstName);
                        PCLastName.setText(lastName);
                        dateEditText.setText(dateOfBirth);

                        // Load profile picture
                        Glide.with(ProfileCreation.this)
                                .load(profilePic)
                                .transform(new CircleCrop())
                                .into(ProfilePicture);
                    });

                } else {
                    String message = jsonResponse.getString("message");
                    runOnUiThread(() -> Toast.makeText(ProfileCreation.this, message, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e("ProfileCreation", "Error fetching profile", e);
                runOnUiThread(() -> Toast.makeText(ProfileCreation.this, "Error fetching profile. Please try again.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }


    // Reset progress
    private void resetProgress() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, 0);
        editor.apply();
    }


    // Date picker dialog
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


    // Check if age is at least 10 years
    private boolean isAgeValid() {
        String dateOfBirth = dateEditText.getText().toString().trim();

        if (dateOfBirth.isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Calendar dob = Calendar.getInstance();
            dob.setTime(sdf.parse(dateOfBirth));  // Parse date of birth from the input

            // Get the current date
            Calendar today = Calendar.getInstance();

            // Calculate the age
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            // Check if the current date is before the birthday in the current year
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            // Return true only if the age is 10 or older
            return age >= 10;
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Return false if there is an error parsing the date
        }
    }



    private String formatDateForServer(String dateOfBirth) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US); // Input format (from the app)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Output format (for MySQL)
            return outputFormat.format(inputFormat.parse(dateOfBirth)); // Convert and return the new format
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if there's an issue with formatting
        }
    }

    // Open image chooser
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
            selectedImageUri = data.getData();  // Store selected image URI
            Glide.with(this)
                    .load(selectedImageUri)
                    .transform(new CircleCrop())
                    .into(ProfilePicture);
        }
    }

    // Mark step as completed
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
