package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.ResetResponse;
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
    private Button resetPasswordButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordField = findViewById(R.id.new_password);
        confirmPasswordField = findViewById(R.id.confirm_new_password);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://heavymetals.scarlet2.io/HeavyMetals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Retrieve user_id from Intent
        String userId = getIntent().getStringExtra("user_id");

        if (userId == null) {
            showToast("Invalid reset link.");
            return;
        }

        resetPasswordButton.setOnClickListener(v -> resetPassword(userId));
    }

    private void resetPassword(String userId) {
        String newPassword = newPasswordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Input validation
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please enter and confirm your new password.");
            return;
        } else if (newPassword.length() < 6) {
            showToast("Password must be at least 6 characters long.");
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            showToast("Passwords do not match.");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(ResetPasswordActivity.this);
        progressDialog.setMessage("Resetting password...");
        progressDialog.show();

        // Updated API call to match backend parameters
        Call<ResetResponse> call = apiService.resetPassword(userId, newPassword, confirmPassword);
        call.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    ResetResponse resetPasswordResponse = response.body();
                    if (resetPasswordResponse.getSuccess() == 1) {
                        showSuccessDialog();
                    } else {
                        showToast(resetPasswordResponse.getMessage());
                    }
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
            public void onFailure(Call<ResetResponse> call, Throwable t) {
                progressDialog.dismiss();
                if (t instanceof IOException) {
                    showToast("Network error. Please check your connection.");
                } else {
                    showToast("Unexpected error occurred. Please try again.");
                }
                Log.e("ResetPasswordFailure", "onFailure: " + t.getMessage(), t);
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(ResetPasswordActivity.this)
                .setMessage("Your password has been successfully reset!")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
