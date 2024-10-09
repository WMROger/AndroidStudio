package com.example.heavymetals.Login_RegisterPage.LoginPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Home_LandingPage.Profile.ProfileCreation;
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
    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;
    private final String URL_LOGIN = "https://heavymetals.scarlet2.io/HeavyMetals/login_user.php";
    private SharedPreferences sharedPreferences;
    private static final String FIRST_LOGIN_KEY = "first_login";  // Flag to track first login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.konkhmer_sleokchher);

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

        // Set onClickListener for toggling password visibility
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());

        // Set the typeface on the EditText or other text components
        passwordEditText = findViewById(R.id.Password_Usertext);
        passwordEditText.setTypeface(customTypeface);
    }

    // Initialize UI components
    private void initializeUI() {
        email = findViewById(R.id.Login_UserEmail);
        passwordEditText = findViewById(R.id.Password_Usertext);
        passwordToggle = findViewById(R.id.password_toggle);
        loginBtn = findViewById(R.id.Login_LoginButton);
        forgetPassword = findViewById(R.id.Login_ForgetPassPage);
        signUp = findViewById(R.id.LoginSignUpTxt);
    }

    private void togglePasswordVisibility() {
        int cursorPosition = passwordEditText.getSelectionStart();
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_visibility);  // Update icon to 'hide' icon
            isPasswordVisible = false;
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.ic_visibility_off);  // Update icon to 'show' icon
            isPasswordVisible = true;
        }

        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.konkhmer_sleokchher);
        passwordEditText.setTypeface(customTypeface);
        passwordEditText.setSelection(cursorPosition);
    }

    private void checkLoggedInUser() {
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (loggedInUser != null && authToken != null) {
            navigateToMainActivity();  // If the user is already logged in, go to MainActivity
        }
    }

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
                    String userId = jsonResponse.getString("user_id");

                    saveUserDetails(email, token, userId);
                    navigateBasedOnFirstLogin();  // Redirect based on first login status
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
        editor.putString("loggedInUser", email);
        editor.putString("auth_token", token);
        editor.putString("user_id", userId);
        editor.apply();
    }

    private void handleVolleyError(VolleyError error) {
        String errorMessage = "Network error occurred";
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            errorMessage += " (Status code: " + statusCode + ")";
        }

        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e("LoginActivity", "Volley Error: " + error.getMessage());
    }

    // Check if it's the first login and navigate accordingly
    private void navigateBasedOnFirstLogin() {
        boolean isFirstLogin = sharedPreferences.getBoolean(FIRST_LOGIN_KEY, true);  // Default is true for first login

        if (isFirstLogin) {
            // If it's the first login, go to ProfileCreation
            navigateToProfileCreation();

            // Set the first login flag to false so it doesn't redirect next time
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_LOGIN_KEY, false);  // Mark as not the first login
            editor.apply();
        } else {
            // If it's not the first login, go to MainActivity
            navigateToMainActivity();
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Finish LoginActivity to prevent going back
    }

    private void navigateToProfileCreation() {
        Intent intent = new Intent(LoginActivity.this, ProfileCreation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Finish LoginActivity to prevent going back
    }

    private void navigateToForgetPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
