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
        if (loggedInUser != null) {
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

    // Handle the login response from the server
    private void handleLoginResponse(String response, String email) {
        Log.d("LoginActivity", "Raw server response: " + response);

        try {
            JSONObject jsonResponse = new JSONObject(response);
            String success = jsonResponse.getString("success");

            if (success.equalsIgnoreCase("1")) {
                if (jsonResponse.has("token")) {
                    String token = jsonResponse.getString("token");
                    saveUserDetails(email, token);  // Save email and token in SharedPreferences
                    navigateToMainActivity();
                } else {
                    Log.d("LoginActivity", "Token not found in response");
                    Toast.makeText(this, "Login failed: Token missing", Toast.LENGTH_SHORT).show();
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

    // Save user email and token to SharedPreferences
    private void saveUserDetails(String email, String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUser", email);  // Save the user email
        editor.putString("auth_token", token);    // Save auth token (if needed)
        editor.apply();
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
