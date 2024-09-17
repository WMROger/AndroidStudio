package com.example.heavymetals.Login_RegisterPage.RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName, lastName, email, passwordEditText, passwordConfirmationEditText;
    private Button btnDone;
    private final String url_register = "https://heavymetals.scarlet2.io/HeavyMetals/register_user.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        btnDone.setOnClickListener(v -> {
            btnDone.setEnabled(false);
            if (validateInputs()) {
                registerUser();
            } else {
                btnDone.setEnabled(true);
            }
        });

        findViewById(R.id.SignUpLoginTxt).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.Signup_Terms_Conditions).setOnClickListener(v -> {
            startActivity(new Intent(this, TermsConditionsActivity.class));
            finish();
        });
    }

    private void initializeViews() {
        firstName = findViewById(R.id.Signup_FirstName);
        lastName = findViewById(R.id.Signup_LastName);
        email = findViewById(R.id.Signup_Email);
        passwordEditText = findViewById(R.id.Signup_Password);
        passwordConfirmationEditText = findViewById(R.id.Signup_PasswordConfirmation);
        btnDone = findViewById(R.id.btnDone);
    }

    private boolean validateInputs() {
        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirmation = passwordConfirmationEditText.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || emailInput.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            showToast("All fields are required.");
            return false;
        }
        if (!firstname.matches("[a-zA-Z ]+") || !lastname.matches("[a-zA-Z ]+")) {
            showToast("Names can only contain letters.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            showToast("Invalid email address.");
            return false;
        }
        if (password.length() < 6) {
            showToast("Password must be at least 6 characters long.");
            return false;
        }
        if (!password.equals(passwordConfirmation)) {
            showToast("Passwords do not match.");
            return false;
        }
        return true;
    }

    private void registerUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_register,
                response -> {
                    Log.d("RegisterActivity", "Response: " + response);
                    btnDone.setEnabled(true);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.optInt("success") == 0) {
                            showToast("Registration Successful.");
                            startActivity(new Intent(this, TermsConditionsActivity.class));
                            finish();
                        } else {
                            showToast("Registration failed: " + jsonResponse.optString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("RegisterActivity", "JSON Error: " + e.getMessage());
                        showToast("Error parsing server response.");
                    }
                },
                error -> {
                    Log.e("RegisterActivity", "Volley Error: " + error);
                    btnDone.setEnabled(true);
                    showToast("An error occurred. Please try again.");
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name", firstName.getText().toString().trim());
                params.put("last_name", lastName.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("password", passwordEditText.getText().toString().trim());
                params.put("passwordConfirmation", passwordConfirmationEditText.getText().toString().trim());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
