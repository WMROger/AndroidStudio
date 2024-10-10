package com.example.heavymetals.Home_LandingPage.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.R;

import android.app.DatePickerDialog;
import android.widget.EditText;

import java.util.Calendar;

public class ProfileCreation extends AppCompatActivity {
    private Button btnPFDnext;
    private EditText dateEditText;
    private TextView ProfileAdd_create, PCFirstName, PCLastName, PC_Skip;
    private ImageView ProfilePicture;
    private static final int PICK_IMAGE_REQUEST = 100;

    // Progress tracking keys and constants
    private static final String PREFS_NAME = "UserProgressPrefs";
    private static final String PROGRESS_KEY = "progress";
    private static final String STEP1_COMPLETED_KEY = "profile_creation_completed";  // Adjust this for each step
    private static final int PROFILE_CREATION_PROGRESS = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        ProfileAdd_create = findViewById(R.id.ProfileAdd_create);
        ProfilePicture = findViewById(R.id.ProfilePicture);
        ProfilePicture.setBackground(null);

        // Initialize PCFirstName and PCLastName before setting their values
        PCFirstName = findViewById(R.id.PCFirstName);
        PCLastName = findViewById(R.id.PCLastName);
        PC_Skip = findViewById(R.id.PC_Skip);


        // Get the first name and last name from the intent
        String firstName = getIntent().getStringExtra("first_name");
        String lastName = getIntent().getStringExtra("last_name");

        // Set the values in the text fields if they are not null
        if (firstName != null) {
            PCFirstName.setText(firstName);
        }
        if (lastName != null) {
            PCLastName.setText(lastName);
        }
        PC_Skip.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileCreation.this, MainActivity.class);
            startActivity(intent);
        });


        // Request storage permission if necessary
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Set onClick listener to ProfileAdd_create (which will open the image chooser)
        ProfileAdd_create.setOnClickListener(v -> openImageChooser());

        btnPFDnext = findViewById(R.id.btnPFDnext);
        dateEditText = findViewById(R.id.editTextText6);

        // Show date picker when date field is clicked
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Handle the "Next" button click event
        btnPFDnext.setOnClickListener(v -> {
            if (areFieldsFilled()) {
                // Mark this step as completed
                markStepAsCompleted();

                // Update progress by 25 (the amount for this step)
                updateProgress(PROFILE_CREATION_PROGRESS);

                // Proceed to the next activity (FitnessDeclaration)
                Intent intent = new Intent(ProfileCreation.this, FitnessDeclaration.class);
                startActivity(intent);
            } else {
                Toast.makeText(ProfileCreation.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if all required fields are filled
    private boolean areFieldsFilled() {
        String dob = dateEditText.getText().toString().trim();
        return !dob.isEmpty();
    }

    // Method to display the date picker dialog
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
        datePickerDialog.setOnShowListener(dialog -> {
            Button positiveButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE);
            Button negativeButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE);

            // Set custom text color for buttons
            if (positiveButton != null && negativeButton != null) {
                positiveButton.setTextColor(getResources().getColor(R.color.custom_orange));
                negativeButton.setTextColor(getResources().getColor(R.color.custom_orange));
            }
        });
        datePickerDialog.show();
    }

    // Method to open the image chooser
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

            // Load the image into the ImageView using Glide with a CircleCrop transformation
            Glide.with(this)
                    .load(imageUri)
                    .transform(new CircleCrop())  // This makes the image circular
                    .into(ProfilePicture);  // Display the image in the ProfilePicture ImageView
        }
    }

    // Method to mark this step as completed
    private void markStepAsCompleted() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STEP1_COMPLETED_KEY, true);  // Mark this step as completed
        editor.apply();
    }

    // Method to update the progress in SharedPreferences
    private void updateProgress(int progressIncrement) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int currentProgress = sharedPreferences.getInt(PROGRESS_KEY, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PROGRESS_KEY, currentProgress + progressIncrement);  // Increment progress
        editor.apply();
    }
}
