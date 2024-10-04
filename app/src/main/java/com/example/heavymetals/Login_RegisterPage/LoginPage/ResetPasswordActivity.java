package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordField, confirmPasswordField;
    private TextView codePreview;
    private Button resetPasswordButton;
    private ApiService apiService;  // Declare ApiService instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordField = findViewById(R.id.new_password);
        confirmPasswordField = findViewById(R.id.confirm_new_password);
        codePreview = findViewById(R.id.codePreview);  // Assuming this TextView is in your XML
        resetPasswordButton = findViewById(R.id.reset_password_button);

        // Initialize Retrofit and ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://heavymetals.scarlet2.io/HeavyMetals/")  // Your base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);  // Initialize the ApiService

        // Get the token and code from the Intent
        String token = getIntent().getStringExtra("token");
        String code = getIntent().getStringExtra("code");

        // Display the code in the TextView
        if (code != null) {
            codePreview.setText("Verification Code: " + code);
        }

        // Set click listener for the reset button
        resetPasswordButton.setOnClickListener(v -> resetPassword(token, code));
    }

    private void resetPassword(String token, String code) {
        String newPassword = newPasswordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please enter and confirm your new password.");
        } else if (!newPassword.equals(confirmPassword)) {
            showToast("Passwords do not match.");
        } else {
            // Call the backend to reset the password and pass the token and code
            Call<Void> call = apiService.resetPassword(token, newPassword, confirmPassword, code);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        showToast("Password successfully reset!");
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("Failed to reset password. Response Code: " + response.code());
                        try {
                            Log.e("ResetPasswordError", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    showToast("Error: " + t.getMessage());
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

