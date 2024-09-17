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
import com.android.volley.Response;
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
    // Initialize Variables
    EditText email, passwordEditText;
    Button loginBtn;
    TextView signUp;
    String url_login = "https://heavymetals.scarlet2.io/HeavyMetals/login_user.php"; // Ensure this is correct

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Set the content view

        // Find views by ID
        email = findViewById(R.id.Login_UserEmail);
        passwordEditText = findViewById(R.id.Password_Usertext);
        loginBtn = findViewById(R.id.Login_LoginButton);
        signUp = findViewById(R.id.LoginSignUpTxt);

        // LOGIN: Signing in
        loginBtn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            // Input validation
            if (!validateEmail() || !validatePassword()) {
                return;
            }

            // Perform network request for authentication
            authenticateUser(emailInput, passwordInput);
        });

        // Set sign-up button behavior
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateEmail() {
        String val = email.getText().toString().trim();

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else {
            email.setError(null);  // Clears the error
            return true;
        }
    }

    private boolean validatePassword() {
        String val = passwordEditText.getText().toString().trim();
        if (val.isEmpty()) {
            passwordEditText.setError("Field cannot be empty");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    private void authenticateUser(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LoginActivity", "Raw server response: " + response); // Log the raw response for debugging

                        // Check if the response is valid JSON
                        if (response.startsWith("{")) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                String success = jsonResponse.getString("success");

                                if (success.equalsIgnoreCase("1")) {
                                    if (jsonResponse.has("token")) {
                                        String token = jsonResponse.getString("token");
                                        saveToken(token);

                                        // Start MainActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish(); // Close LoginActivity
                                    } else {
                                        Log.d("LoginActivity", "Token not found in response");
                                    }
                                } else {
                                    String message = jsonResponse.optString("message", "Authentication failed");
                                    passwordEditText.setError(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("LoginActivity", "Unexpected non-JSON response: " + response);
                            Toast.makeText(LoginActivity.this, "Server error or invalid response.", Toast.LENGTH_SHORT).show();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace(); // Print stack trace for debugging
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }
}
