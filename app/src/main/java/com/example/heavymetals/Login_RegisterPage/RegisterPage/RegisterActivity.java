package com.example.heavymetals.Login_RegisterPage.RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.R;
import com.example.heavymetals.Models.API;
import com.example.heavymetals.Models.RegisterResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName, lastName, email, passwordEditText, passwordConfirmationEditText;
    private Button btnDone;
    private final String url_register = "https://heavymetals.scarlet2.io/HeavyMetals/register_user.php/";
    private API apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url_register)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(API.class);

        btnDone.setOnClickListener(v -> {
            if (validateInputs()) {
                registerUser();
            }
        });

        findViewById(R.id.SignUpLoginTxt).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.Signup_Terms_Conditions).setOnClickListener(v -> {
            startActivity(new Intent(this, TermsConditionsActivity.class));
            finish();
        });
    }

    private void initializeViews() {
        firstName = findViewById(R.id.Signup_FirstName);
        lastName = findViewById(R.id.Signup_LastName);
        email = findViewById(R.id.Signup_Email);
        passwordEditText = findViewById(R.id.Signup_Password);
        passwordConfirmationEditText = findViewById(R.id.Signup_PasswordConfirmation);
        btnDone = findViewById(R.id.btnDone);
    }

    private boolean validateInputs() {
        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirmation = passwordConfirmationEditText.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || emailInput.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            showToast("All fields are required.");
            return false;
        }

        if (!firstname.matches("[a-zA-Z ]+") || !lastname.matches("[a-zA-Z ]+")) {
            showToast("Names can only contain letters.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            showToast("Invalid email address.");
            return false;
        }

        if (password.length() < 6) {
            showToast("Password must be at least 6 characters long.");
            return false;
        }

        if (!password.equals(passwordConfirmation)) {
            showToast("Passwords do not match.");
            return false;
        }

        return true;
    }

    private void registerUser() {
        btnDone.setEnabled(false); // Disable button to prevent multiple clicks

        Call<RegisterResponse> call = apiService.registerUser(
                firstName.getText().toString().trim(),
                lastName.getText().toString().trim(),
                email.getText().toString().trim(),
                passwordEditText.getText().toString().trim(),
                passwordConfirmationEditText.getText().toString().trim()
        );

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                btnDone.setEnabled(true); // Re-enable button

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    int success = registerResponse.getSuccess();
                    String message = registerResponse.getMessage();

                    if (success == 1) {
                        showToast("Registration successful. Please check your email to verify your account.");
                        Intent intent = new Intent(RegisterActivity.this, TermsConditionsActivity.class);
                        intent.putExtra("first_name", firstName.getText().toString().trim());
                        intent.putExtra("last_name", lastName.getText().toString().trim());
                        startActivity(intent);
                    } else {
                        showToast("Registration failed: " + message);
                    }
                } else {
                    showToast("An error occurred. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                btnDone.setEnabled(true); // Re-enable button
                Log.e("RegisterActivity", "Retrofit Error: " + t.getMessage());
                showToast("An error occurred. Please try again.");
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
