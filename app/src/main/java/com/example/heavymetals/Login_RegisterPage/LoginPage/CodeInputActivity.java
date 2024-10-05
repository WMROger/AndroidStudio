package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Models.VerifyCodeResponse;
import com.example.heavymetals.R;
import com.example.heavymetals.network.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CodeInputActivity extends AppCompatActivity {

    private EditText codeInputField;
    private Button verifyCodeButton;
    private ApiService apiService;  // Declare ApiService instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_input);

        codeInputField = findViewById(R.id.code_input_field);
        verifyCodeButton = findViewById(R.id.verify_code_button);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://heavymetals.scarlet2.io/HeavyMetals/")  // Your base URL
                .addConverterFactory(GsonConverterFactory.create(gson))  // Use lenient Gson here
                .build();

        apiService = retrofit.create(ApiService.class);  // Initialize ApiService instance

        // Set click listener for the Verify Code button
        verifyCodeButton.setOnClickListener(v -> verifyCode());
    }

    private void verifyCode() {
        String code = codeInputField.getText().toString().trim();

        if (code.isEmpty()) {
            showToast("Please enter the verification code.");
        } else if (code.length() != 5) {
            showToast("Code must be 5 characters.");
        } else {
            // Call the backend to verify the code
            Call<VerifyCodeResponse> call = apiService.verifyCode(code);  // Use apiService instance to make the call
            call.enqueue(new Callback<VerifyCodeResponse>() {
                @Override
                public void onResponse(Call<VerifyCodeResponse> call, Response<VerifyCodeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        VerifyCodeResponse verifyCodeResponse = response.body();

                        if (verifyCodeResponse.getSuccess() == 1) {
                            // Code verified successfully
                            navigateToResetPasswordActivity(verifyCodeResponse.getUserId());
                        } else {
                            showToast(verifyCodeResponse.getMessage());
                            Log.d("VerifyCode", "Response Message: " + verifyCodeResponse.getMessage());
                        }
                    } else {
                        // Log full error response for debugging
                        try {
                            String errorBody = response.errorBody().string();
                            logLong("VerifyCodeError", "Error Body: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        showToast("Failed to verify the code. Please try again.");
                    }
                }

                @Override
                public void onFailure(Call<VerifyCodeResponse> call, Throwable t) {
                    // Log the detailed error message
                    Log.e("VerifyCodeFailure", "onFailure: " + t.getMessage(), t);
                    showToast("Error: " + t.getMessage());
                }
            });

        }
    }
    public static void logLong(String tag, String message) {
        if (message.length() > 4000) {
            Log.d(tag, message.substring(0, 4000));
            logLong(tag, message.substring(4000));
        } else {
            Log.d(tag, message);
        }
    }

    private void navigateToResetPasswordActivity(String userId) {
        Intent intent = new Intent(CodeInputActivity.this, ResetPasswordActivity.class);
        intent.putExtra("user_id", userId); // Replace yourResetToken with the actual token
        startActivity(intent);

        finish(); // Optionally call finish() to close the current activity
    }



    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
