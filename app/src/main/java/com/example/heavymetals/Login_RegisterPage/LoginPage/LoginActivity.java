package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Login_RegisterPage.RegisterPage.RegisterActivity;
import com.example.heavymetals.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText email, passwordEditText;
    private Button loginBtn;
    private TextView signUp, forgetPassword;
    private final String URL_LOGIN = "https://heavymetals.scarlet2.io/HeavyMetals/login_user.php";
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        checkLoggedInUser();  // Check if the user is already logged in

        setContentView(R.layout.activity_login);

        initializeUI();  // Initialize UI components

        loginBtn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            if (validateInputs(emailInput, passwordInput)) {
                authenticateUser(emailInput, passwordInput);
            }
        });

        forgetPassword.setOnClickListener(v -> navigateToForgetPassword());
        signUp.setOnClickListener(v -> navigateToSignUp());
    }

    // Check if the user is already logged in
    private void checkLoggedInUser() {
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (loggedInUser != null && authToken != null) {
            // If both user and token are present, consider user as logged in
            navigateToMainActivity();
        }
    }

    // Initialize UI components
    private void initializeUI() {
        email = findViewById(R.id.Login_UserEmail);
        passwordEditText = findViewById(R.id.Password_Usertext);
        loginBtn = findViewById(R.id.Login_LoginButton);
        forgetPassword = findViewById(R.id.Login_ForgetPassPage);
        signUp = findViewById(R.id.LoginSignUpTxt);
    }

    // Validate email and password
    private boolean validateInputs(String emailInput, String passwordInput) {
        if (emailInput.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        }
        if (passwordInput.isEmpty()) {
            passwordEditText.setError("Field cannot be empty");
            return false;
        }
        return true;
    }

    // Authenticate user with server
    private void authenticateUser(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                response -> handleLoginResponse(response, email),
                this::handleVolleyError
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void handleLoginResponse(String response, String email) {
        Log.d("LoginActivity", "Raw server response: " + response);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String success = jsonResponse.getString("success");

            if (success.equalsIgnoreCase("1")) {
                if (jsonResponse.has("token") && jsonResponse.has("user_id")) {
                    String token = jsonResponse.getString("token");
                    String userId = jsonResponse.getString("user_id");  // Get user_id from response

                    saveUserDetails(email, token, userId);  // Save email, token, and user_id in SharedPreferences
                    navigateToMainActivity();
                } else {
                    Log.d("LoginActivity", "Token or user_id not found in response");
                    Toast.makeText(this, "Login failed: Missing token or user ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                String message = jsonResponse.optString("message", "Authentication failed");
                passwordEditText.setError(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserDetails(String email, String token, String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUser", email);   // Save the user email
        editor.putString("auth_token", token);     // Save auth token
        editor.putString("user_id", userId);       // Save user ID
        editor.apply();

        // Logging the stored values for debugging
        Log.d("LoginActivity", "Saved Email: " + email);
        Log.d("LoginActivity", "Saved Token: " + token);
        Log.d("LoginActivity", "Saved User ID: " + userId);
    }



    // Handle Volley errors
    private void handleVolleyError(VolleyError error) {
        String errorMessage = "Network error occurred";
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            errorMessage += " (Status code: " + statusCode + ")";
        }

        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e("LoginActivity", "Volley Error: " + error.getMessage());
    }

    // Navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Finish LoginActivity to prevent going back
    }

    // Navigate to ForgetPasswordActivity
    private void navigateToForgetPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    // Navigate to SignUp (RegisterActivity)
    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
