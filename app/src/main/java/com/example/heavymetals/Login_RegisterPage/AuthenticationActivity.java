package com.example.heavymetals.Login_RegisterPage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.Login_RegisterPage.RegisterPage.RegisterActivity;
import com.example.heavymetals.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInUser = sharedPreferences.getString("loggedInUser", null);
        String authToken = sharedPreferences.getString("auth_token", null);

        if (loggedInUser != null && authToken != null) {
            // If the user is already logged in, redirect to MainActivity
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear back stack
            startActivity(intent);
            finish();  // End the AuthenticationActivity
            return;  // Exit this method to prevent further execution
        }

        // If no user is logged in, continue to show the authentication page
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //STATUS BAR COLOR TO SET BELOW
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.custom_orange));

        // Initialize the variables
        Button loginButton = findViewById(R.id.Auth_login_button);
        Button registerButton = findViewById(R.id.Auth_register_button);

        // Function for login button
        loginButton.setOnClickListener(v -> {
            // Redirect to LoginActivity
            Intent intent = new Intent(AuthenticationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Function for register button
        registerButton.setOnClickListener(v -> {
            // Redirect to RegisterActivity
            Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
