package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.R;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import android.widget.Button;

public class ProfileCreation extends AppCompatActivity {
    private Button btnPFDnext;
    private EditText PCFirstName, PCLastName, dateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnPFDnext = findViewById(R.id.btnPFDnext);
        PCFirstName = findViewById(R.id.PCFirstName);
        PCLastName = findViewById(R.id.PCLastName);
        dateEditText = findViewById(R.id.editTextText6);

        // Show date picker when date field is clicked
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Handle the "Next" button click event
        btnPFDnext.setOnClickListener(v -> {
            if (areFieldsFilled()) {
                // Proceed to next activity
                Intent intent = new Intent(ProfileCreation.this, FitnessDeclaration.class);
                startActivity(intent);
            } else {
                // Show a toast message if any field is empty
                Toast.makeText(ProfileCreation.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean areFieldsFilled() {
        String firstName = PCFirstName.getText().toString().trim();
        String lastName = PCLastName.getText().toString().trim();
        String dob = dateEditText.getText().toString().trim();

        // Check if all fields are filled
        return !firstName.isEmpty() && !lastName.isEmpty() && !dob.isEmpty();
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDatePicker,
                (view, year1, month1, dayOfMonth) -> {
                    // Month is 0-indexed so add 1
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    dateEditText.setText(selectedDate);
                },
                year, month, day
        );

        // Show the dialog
        datePickerDialog.show();

        // After showing the dialog, customize the button colors
        Button positiveButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE);
        Button negativeButton = datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE);

        // Set the color for OK and Cancel buttons
        if (positiveButton != null && negativeButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.custom_orange)); // Set custom color for OK button
            negativeButton.setTextColor(getResources().getColor(R.color.custom_orange)); // Set custom color for Cancel button
        }
    }

}
