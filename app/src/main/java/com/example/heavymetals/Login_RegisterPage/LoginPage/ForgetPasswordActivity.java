package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.Models.API;
import com.example.heavymetals.Models.ResetResponse;
import com.example.heavymetals.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button searchButton;
    private TextView backButton;
    private Retrofit retrofit;
    private API apiService;
    private final String baseUrl = "https://heavymetals.scarlet2.io/HeavyMetals/forget_password.php/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize views
        backButton = findViewById(R.id.Forget_BackTxt);
        initializeViews();
        setupRetrofit();

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        searchButton.setOnClickListener(v -> {
            if (validateEmail()) {
                requestPasswordReset();
            }
        });

        findViewById(R.id.Forget_BackTxt).setOnClickListener(v -> finish()); // Close the activity to go back
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.Login_UserEmail);
        searchButton = findViewById(R.id.Forget_ConfirmSearch);
    }

    private void setupRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(API.class);
    }

    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            showToast("Email is required.");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email address.");
            return false;
        }
        return true;
    }

    private void requestPasswordReset() {
        String email = emailEditText.getText().toString().trim();
        Call<ResetResponse> call = apiService.requestPasswordReset(email);

        call.enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(Call<ResetResponse> call, Response<ResetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResetResponse resetResponse = response.body();
                    if (resetResponse.getSuccess() == 1) {
                        showToast("Password reset link sent to your email.");
                    } else {
                        showToast("Error: " + resetResponse.getMessage());
                    }
                } else {
                    showToast("An error occurred. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<ResetResponse> call, Throwable t) {
                Log.e("ForgetPasswordActivity", "Retrofit Error: " + t.getMessage());
                showToast("An error occurred. Please try again.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
