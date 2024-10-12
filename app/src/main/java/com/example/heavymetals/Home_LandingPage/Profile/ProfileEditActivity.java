package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.heavymetals.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText firstNameEditText, lastNameEditText;
    private TextView dateOfBirthTextView, backButton;
    private ImageView ProfilePicture;
    private SharedPreferences sharedPreferences;
    private String currentProfilePicUrl;
    private Button savefile;
    private Bitmap newProfilePictureBitmap = null;
    private Button maleButton, femaleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Initialize UI components
        firstNameEditText = findViewById(R.id.edit_firstname);
        lastNameEditText = findViewById(R.id.edit_lastname);
        dateOfBirthTextView = findViewById(R.id.Date_of_Birth);
        ProfilePicture = findViewById(R.id.Profile_Picture);
        backButton = findViewById(R.id.back_profile);
        savefile = findViewById(R.id.save_profile);
        maleButton = findViewById(R.id.Gender_male);
        femaleButton = findViewById(R.id.Gender_female);




        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("loggedInUser", null);

        if (userEmail != null) {
            fetchUserDetails(userEmail);
        } else {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show();
        }

        backButton.setOnClickListener(v -> finish());

        // Allow the user to select a new profile picture
        ProfilePicture.setOnClickListener(v -> openImagePicker());

        // Save the updated profile when the save button is clicked
        savefile.setOnClickListener(v -> saveProfile());
    }

    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handling the image selected from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Get the bitmap from the selected image
                newProfilePictureBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Display the selected image in the ImageView
                Glide.with(this)
                        .load(imageUri)
                        .transform(new CircleCrop())
                        .into(ProfilePicture);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Save the updated profile to the server
    private void saveProfile() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String dateOfBirth = dateOfBirthTextView.getText().toString();

        // Retrieve user_id from SharedPreferences
        String userId = sharedPreferences.getString("user_id", null);

        // Ensure all fields are filled, including user_id
        if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // Prepare the connection to the server
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/user_details/save_profile.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Prepare the profile data (user_id, first name, last name, date of birth)
                StringBuilder postData = new StringBuilder();
                postData.append("user_id=").append(URLEncoder.encode(userId, "UTF-8"));  // Add user_id to the POST data
                postData.append("&first_name=").append(URLEncoder.encode(firstName, "UTF-8"));
                postData.append("&last_name=").append(URLEncoder.encode(lastName, "UTF-8"));
                postData.append("&date_of_birth=").append(URLEncoder.encode(dateOfBirth, "UTF-8"));
                postData.append("&email=").append(URLEncoder.encode(sharedPreferences.getString("loggedInUser", ""), "UTF-8"));

                // If the user has selected a new profile picture, encode it in Base64 and include it in the POST data
                if (newProfilePictureBitmap != null) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    newProfilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodedImage = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                    postData.append("&profile_pic=").append(URLEncoder.encode(encodedImage, "UTF-8"));
                }

                // Send the POST data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(postData.toString());
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

                // Parse the server response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");

                // Handle the result on the UI thread
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(ProfileEditActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileEditActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProfileEditActivity.this, "Error saving profile", Toast.LENGTH_LONG).show());
            }
        }).start();
    }



    // Fetch the user details from the server
    private void fetchUserDetails(String email) {
        String userId = sharedPreferences.getString("user_id", null);

        if (userId == null) {
            runOnUiThread(() -> Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show());
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL("https://heavymetals.scarlet2.io/HeavyMetals/user_details/get_profile.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Send the POST data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postData = "email=" + URLEncoder.encode(email, "UTF-8") + "&user_id=" + URLEncoder.encode(userId, "UTF-8");
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

                // Log the full response for debugging
                Log.d("API Response", response.toString());

                // Parse the response JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                boolean success = jsonResponse.getBoolean("success");

                if (success && jsonResponse.has("profile")) {
                    JSONObject profile = jsonResponse.getJSONObject("profile");
                    String firstName = profile.getString("first_name");
                    String lastName = profile.getString("last_name");
                    String dateOfBirth = profile.getString("date_of_birth");
                    currentProfilePicUrl = profile.getString("profile_pic");

                    // If gender is also returned from the server
                    String gender = profile.optString("gender", null);  // You can add this field to your JSON response

                    // Update the UI on the main thread
                    runOnUiThread(() -> {
                        firstNameEditText.setText(firstName);
                        lastNameEditText.setText(lastName);
                        dateOfBirthTextView.setText(dateOfBirth);

                        // Load profile picture using Glide with CircleCrop transformation
                        Glide.with(ProfileEditActivity.this)
                                .load("https://heavymetals.scarlet2.io/HeavyMetals/user_details/images/" + currentProfilePicUrl)
                                .transform(new CircleCrop()) // Apply the circle crop transformation
                                .placeholder(R.drawable.ic_profile) // Placeholder image
                                .error(R.drawable.ic_profile) // Error image
                                .into(ProfilePicture);

                        // Highlight the button based on the gender
                        highlightGenderButton(gender);  // Call the method to highlight the button
                    });
                } else {
                    // Log an error message if profile data is missing
                    Log.e("Profile Error", "No profile data found in the response");
                    runOnUiThread(() -> Toast.makeText(ProfileEditActivity.this, "No profile data available", Toast.LENGTH_LONG).show());
                }

            } catch (JSONException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProfileEditActivity.this, "Failed to parse server response", Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ProfileEditActivity.this, "Error fetching profile. Please try again.", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // Method to highlight gender buttons based on fetched value
    private void highlightGenderButton(String gender) {
        if (gender != null) {
            if (gender.equalsIgnoreCase("male")) {
                // Highlight the male button
                maleButton.setTextColor(getResources().getColor(R.color.white)); // Selected color
                maleButton.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight Male

                // Reset the female button
                femaleButton.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
                femaleButton.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset Female

            } else if (gender.equalsIgnoreCase("female")) {
                // Highlight the female button
                femaleButton.setTextColor(getResources().getColor(R.color.white)); // Selected color
                femaleButton.setBackgroundTintList(getResources().getColorStateList(R.color.custom_orange)); // Highlight Female

                // Reset the male button
                maleButton.setTextColor(getResources().getColor(R.color.unselected_color)); // Unselected color
                maleButton.setBackgroundTintList(getResources().getColorStateList(R.color.black)); // Reset Male
            }
        }
    }






}
