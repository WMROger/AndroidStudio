package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heavymetals.Models.ResetResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordField;
    private EditText confirmPasswordField;
    private Button resetPasswordButton;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordField = findViewById(R.id.new_password);
        confirmPasswordField = findViewById(R.id.confirm_new_password);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        // Initialize Retrofit and ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://heavymetals.scarlet2.io/")  // Your API base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Call resetPassword method when button is clicked
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String token = getIntent().getStringExtra("token");  // Assume you are passing the token
        String newPassword = newPasswordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please enter and confirm your new password.");
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            showToast("Passwords do not match.");
            return;
        }

        // Call the resetPassword API using Retrofit
        Call<ResetResponse> call = apiService.resetPassword(token, newPassword, confirmPassword);
        call.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResetResponse resetResponse = response.body();
                    if (resetResponse.getSuccess() == 1) {
                        showToast("Password successfully reset!");
                        // Optionally, navigate the user to the login screen
                    } else {
                        showToast("Error: " + resetResponse.getMessage());
                    }
                } else {
                    showToast("An error occurred. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ResetResponse> call, Throwable t) {
                showToast("An error occurred: " + t.getMessage());
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
