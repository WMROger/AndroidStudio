package com.example.heavymetals.Login_RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation.FitnessDeclaration;
import com.example.heavymetals.Login_RegisterPage.RegisterPage.RegisterActivity;
import com.example.heavymetals.R;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //STATUS BAR COLOR TO SET BELOW
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.custom_orange));

        //Initialize the variables
        Button loginButton = findViewById(R.id.Auth_login_button);
        Button registerButton = findViewById(R.id.Auth_register_button);

        //Function for button
        loginButton.setOnClickListener(v -> {
            // Perform login operation
            // After successful login, transition to MainActivity
            Intent intent = new Intent(AuthenticationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> {
            // Perform login operation
            // After successful login, transition to MainActivity
            Intent intent = new Intent(AuthenticationActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
