package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.Home_LandingPage.MainActivity;
import com.example.heavymetals.Login_RegisterPage.LoginActivity;
import com.example.heavymetals.R;

public class FitnessDeclaration3 extends AppCompatActivity {

    Button btnPFDnext3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_declaration3);

        btnPFDnext3 = findViewById(R.id.btnPFDnext3);

        btnPFDnext3.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}