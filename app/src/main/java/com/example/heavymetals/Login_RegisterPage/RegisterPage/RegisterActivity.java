package com.example.heavymetals.Login_RegisterPage.RegisterPage;

import android.content.Intent;
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
import com.example.heavymetals.Login_RegisterPage.LoginActivity;
import com.example.heavymetals.R;

import java.util.HashMap;
import java.util.Map;

import android.util.Patterns;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {
    EditText firstName, lastName, email, passwordEditText, passwordConfirmationEditText;
    TextView loginTxt, TnC;
    Button btnDone;
    String url_register = "https://heavymetals.scarlet2.io/HeavyMetals/register_user.php";// Your registration URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TnC = findViewById(R.id.Signup_Terms_Conditions);
        firstName = findViewById(R.id.Signup_FirstName);
        lastName = findViewById(R.id.Signup_LastName);
        email = findViewById(R.id.Signup_Email);
        passwordEditText = findViewById(R.id.Signup_Password);
        passwordConfirmationEditText = findViewById(R.id.Signup_PasswordConfirmation);
        btnDone = findViewById(R.id.btnDone);
        loginTxt = findViewById(R.id.SignUpLoginTxt);

        // Done button function
        btnDone.setOnClickListener(v -> {
            String firstname = firstName.getText().toString().trim();
            String lastname = lastName.getText().toString().trim();
            String username = email.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String passwordConfirmation = passwordConfirmationEditText.getText().toString().trim();

            if (validateInputs(firstname, lastname, username, password, passwordConfirmation)) {
                registerUser(firstname, lastname, username, password, passwordConfirmation);
            }
        });


        // Login button/text function
        loginTxt.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        TnC.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, TermsConditionsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInputs(String firstName, String lastName, String email, String password, String passwordConfirmation) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            Log.d("RegisterActivity", "Validation Error: All fields are required.");
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!firstName.matches("[a-zA-Z ]+")) {
            Log.d("RegisterActivity", "Validation Error: Invalid first name.");
            Toast.makeText(this, "First name can only contain letters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!lastName.matches("[a-zA-Z ]+")) {
            Log.d("RegisterActivity", "Validation Error: Invalid last name.");
            Toast.makeText(this, "Last name can only contain letters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("RegisterActivity", "Validation Error: Invalid email address.");
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Log.d("RegisterActivity", "Validation Error: Password too short.");
            Toast.makeText(this, "Password must be at least 8 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(passwordConfirmation)) {
            Log.d("RegisterActivity", "Validation Error: Passwords do not match.");
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void registerUser(String firstName, String lastName, String email, String password, String passwordConfirmation) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_register,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log the full server response for debugging
                        Log.d("RegisterResponse", "Server Response: " + response);

                        // Check if response is valid and not empty
                        if (response != null && !response.isEmpty()) {
                            try {
                                // Parse the response to JSON
                                JSONObject jsonResponse = new JSONObject(response);

                                // Extract 'success' and 'message' fields
                                int success = jsonResponse.optInt("success", 0);
                                String message = jsonResponse.optString("message", "No message provided");

                                // Log the parsed response details
                                Log.d("RegisterActivity", "Success: " + success + ", Message: " + message);

                                // Handle the response based on success flag
                                if (success == 1) {
                                    // Registration successful
                                    Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(RegisterActivity.this, TermsConditionsActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Registration failed (e.g., email already taken)
                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // Handle JSON parsing errors
                                Log.e("RegisterActivity", "Error parsing JSON response", e);
                                Toast.makeText(RegisterActivity.this, "Error parsing server response.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Handle empty or null response
                            Log.e("RegisterActivity", "Empty or null response received.");
                            Toast.makeText(RegisterActivity.this, "Server returned an empty response.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error details
                        String errorMessage = (error.networkResponse != null && error.networkResponse.data != null)
                                ? new String(error.networkResponse.data) : error.getMessage();

                        Log.e("RegisterActivity", "Volley Error: " + errorMessage);

                        // Display error message
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("email", email);
                params.put("password", password);
                params.put("passwordConfirmation", passwordConfirmation);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

}
